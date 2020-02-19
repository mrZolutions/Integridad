'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillOfflineCtrl
 * @description
 * # BillOfflineCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('BillOfflineCtrl', function(_, $location, utilStringService, $localStorage, projectService, consumptionService,
                                            clientService, productService, authService, billOfflineService, cashierService, utilSeqService,
                                            validatorService, warehouseService, countryListService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.clientList = undefined;
        vm.isEmp = true;
        vm.pagoTot = undefined;
        vm.countryList = countryListService.getCountryList();
        vm.citiesList = countryListService.getCitiesEcuador();

        vm.prices = [
            { name: 'EFECTIVO', cod: 'cashPercentage'}, { name: 'TARJETA', cod: 'cardPercentage'}
        ];

        vm.medList = [
            {code: 'efectivo', name: 'Efectivo'},
            {code: 'cheque', name: 'Cheque'},
            {code: 'tarjeta_credito', name: 'Tarjeta de Crédito'},
            {code: 'tarjeta_debito', name: 'Tarjeta de Débito'},
            {code: 'transferencia', name: 'Transferencia'}
        ];

        vm.formList = [
            '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO',
            '15 - COMPENSACION DE DEUDAS',
            '16 - TARJETAS DE DEBITO',
            '17 - DINERO ELECTRONICO',
            '18 - TARJETA PREPAGO',
            '19 - TARJETA DE CREDITO',
            '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
            '21 - ENDOSO DE TITULOS'
        ];

        vm.creditCardList = [
            'DINNERS CLUB',
            'VISA',
            'MASTERCARD',
            'AMERICAN'
        ];

        vm.seqChanged = false;

        function _activate() {
            vm.advertencia = false;
            vm.valParra = '2455e4bf-68f3-4071-b5c9-833d62512b00';
            vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';
            vm.mrZolutions = '4907601b-6e54-4675-80a8-ab6503e1dfeb';
            vm.pineda = '6e663299-d61c-44de-b42c-a3d6595b46d2';

            vm.error = undefined;
            vm.success = undefined;
            vm.aux = undefined;
            vm.newClient = undefined;
            vm.newBillOffline = true;
            vm.newConsumptionOff = true;
            vm.newBuyOff = true;
            vm.billedOffline = false;
            vm.clientSelected = undefined;
            vm.dateBillOffline = undefined;
            vm.seqNumber = undefined; //Secuencia de Factura
            vm.csmSeqNumber = undefined; //Secuencia de Consumos
            vm.consumptioned = false;
            vm.warehouse = undefined;
            vm.productList = undefined;
            vm.productToAdd = undefined;
            vm.pagoTot = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            vm.indexDetail = undefined;
            vm.discount = undefined;
            vm.priceType = vm.prices[0];
            vm.seqChanged = false;
            vm.impuestosTotales = [];
            vm.impuestoICE = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "3",
                "codigo_porcentaje": "2"
            };
            vm.impuestoIVA = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "2",
                "codigo_porcentaje": "2"
            };
            vm.impuestoIVAZero = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "2",
                "codigo_porcentaje": "0"
            };
            vm.medio = {};
            vm.pagosOffline = [];
            vm.user = $localStorage.user;
            vm.userClientId = $localStorage.user.subsidiary.userClient.id;
            vm.subsidiaryId = $localStorage.user.subsidiary.id;
            vm.subOfflineActive = $localStorage.user.subsidiary.offline;
            vm.userId = $localStorage.user.id;
            if (vm.subOfflineActive) {
                vm.loading = true;
                clientService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                    vm.clientList = response;
                    var finalConsumer = _.filter(vm.clientList, function(client) { return client.identification === '9999999999999'});
                    vm.clientSelectOffline(finalConsumer[0]);
                    _getSeqNumber();
                    _initializeBillOffline();
                    _initializeConsumption();
                    var today = new Date();
                    $('#pickerBillOfflineDate').data("DateTimePicker").date(today);
                    vm.loading = false;
                    setTimeout(function() {
                        document.getElementById("input4").focus();
                    }, 500);
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.advertencia = true;    
            };
        };

        vm.volver = function() {
            _activate();
        };

        vm.nuevaBillOffline = function() {
            _activate();
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt(vm.user.cashier.billOfflineNumberSeq) + 1;
            vm.seqNumberFirstPart = vm.user.subsidiary.threeCode + '-' + vm.user.cashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
        };

        function _initializeBillOffline() {
            vm.billOffline = {
                client: vm.clientSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                dateCreated: vm.dateBillOffline.getTime(),
                discount: 0,
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                detailsOffline: [],
                pagosOffline: []
            };
        };

        vm.filterBarCode = function(){
            if (vm.billOfflineBarCode.length === 13) {
                productService.getLazyBySusidiaryIdBarCode($localStorage.user.subsidiary.id, vm.billOfflineBarCode).then(function(response) {
                    if (!_.isEmpty(response)) {
                        vm.quantity = 1;
                        vm.paraticularDiscount = undefined;
                        vm.toAdd = response[0];
                        vm.toAddExistency = _.last(vm.toAdd.productBySubsidiaries).quantity;
                        vm.toAddPrice = vm.getCost(vm.toAdd[vm.priceType.cod], vm.toAdd.averageCost);
                        setTimeout(function() {
                            var input = document.getElementById("productQuantity");
                            input.focus();
                            input.select();
                        }, 200);
                    };
                });
            };
        };

        vm.clientSelectOffline = function(client) {
            vm.success = undefined;
            vm.companyData = $localStorage.user.subsidiary;
            vm.dateBillOffline = new Date();
            vm.clientSelected = client;
            vm.pagosOffline = [];
            setTimeout(function() {
                document.getElementById("input4").focus();
            }, 500);
        };

        vm.clientSelectOfflineChanged = function(client) {
            vm.clientSelected = client;
            vm.billOffline.client = vm.clientSelected;
            vm.consumption.client = vm.clientSelected;
            vm.pagosOffline = [];
            setTimeout(function() {
                document.getElementById("input4").focus();
            }, 500);
        };

        vm.clientConsult = function(client) {
            vm.loading = true;
            billOfflineService.getBillsOfflineByClientId(client.id).then(function(response) {
                vm.billOfflineList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientChangeOffline = function() {
            vm.clientSelected = undefined;
            setTimeout(function() {
                document.getElementById("inputCl1").focus();
            }, 200);
        };

        vm.createClient = function() {
            vm.newClient = true;
            projectService.getNumberOfProjects(vm.userClientId).then(function(response) {
                vm.success = undefined;
                vm.error = undefined
                var number = parseInt(response);
                vm.client = {
                    country: 'Ecuador',
                    city: 'Quito',
                    codApp: number + 1,
                    userClient: $localStorage.user.subsidiary.userClient
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveClient = function() {
            var idValid = true;
            var validationError = utilStringService.isAnyInArrayStringEmpty([
                vm.client.name, vm.client.identification, vm.client.codApp
            ]);
            if (vm.client.typeId === 'CED') {
                idValid = validatorService.isCedulaValid(vm.client.identification);
            } else if (vm.client.typeId === 'RUC') {
                idValid = validatorService.isRucValid(vm.client.identification);
            } else if (vm.client.typeId === 'IEX') {
                idValid = true;
            };

            if (validationError) {
                vm.error = 'Debe ingresar Nombres completos, una identificacion y el Codigo de Contabilidad';
            } else if (!idValid) {
                vm.error = 'Identificacion invalida';
            } else {
                vm.loading = true;
                createCli();
            };
        };

        function createCli() {
            clientService.getClientByUserClientAndIdentification(vm.userClientId, vm.client.identification).then(function(response) {
                if (response.length === 0) {
                    clientService.create(vm.client).then(function(response) {
                        vm.newClient = undefined;
                        vm.error = undefined;
                        vm.success = 'Registro realizado con exito';
                        setInterval(_activate(), 500);
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.error = 'El Cliente Ya Existe';
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelCreateClient = function() {
            _activate();
        };

        function _getTotalSubtotal() {
            vm.billOffline.subTotal = 0.0;
            vm.billOffline.iva = 0.0;
            vm.billOffline.ivaZero = 0.0;
            vm.billOffline.ice = 0.0;
            vm.billOffline.baseTaxes = 0.0;
            vm.billOffline.baseNoTaxes = 0.0;
            vm.billOffline.discount = 0.0;
            //Calculos para Consumos
            vm.consumption.subTotal = 0;
            vm.consumption.iva = 0;
            vm.consumption.ivaZero = 0;
            vm.consumption.ice = 0;
            vm.consumption.baseTaxes = 0;
            vm.consumption.baseNoTaxes = 0;

            var discountWithIva = 0.0;
            var discountWithNoIva = 0.0;
            _.each(vm.billOffline.detailsOffline, function(detail) {
                vm.billOffline.subTotal = parseFloat((parseFloat(vm.billOffline.subTotal) + parseFloat(detail.total)).toFixed(2));
                vm.consumption.subTotal = vm.billOffline.subTotal;
                var tot = parseFloat((detail.total).toFixed(2));
                if (vm.billOffline.discountPercentage) {
                    tot = parseFloat((detail.total).toFixed(2));
                };
                if (detail.product.iva) {
                    vm.billOffline.baseTaxes += parseFloat((detail.total).toFixed(2));
                    vm.consumption.baseTaxes = vm.billOffline.baseTaxes;
                    if (vm.userClientId === vm.laQuinta) {
                        vm.billOffline.iva = 0.0;
                        vm.consumption.iva = 0.0;
                    } else {
                        vm.billOffline.iva = parseFloat((parseFloat(vm.billOffline.iva) + (parseFloat(tot) * 0.12)).toFixed(2));
                        vm.consumption.iva = vm.billOffline.iva;
                    };
                    if (vm.billOffline.discountPercentage) {
                        discountWithIva = parseFloat((parseFloat(discountWithIva) + (parseFloat(detail.total) * parseFloat((vm.billOffline.discountPercentage) / 100))).toFixed(2));
                    };
                } else {
                    vm.billOffline.baseNoTaxes += parseFloat((detail.total).toFixed(2));
                    vm.consumption.baseNoTaxes = vm.billOffline.baseNoTaxes;
                    vm.billOffline.ivaZero = 0.0;
                    vm.consumption.ivaZero = 0.0;
                    if (vm.billOffline.discountPercentage) {
                        discountWithNoIva = parseFloat((parseFloat(discountWithNoIva) + ((parseFloat(vm.billOffline.discountPercentage) / 100) * parseFloat(detail.total))).toFixed(2));
                    };
                };
            });
            if (vm.billOffline.discountPercentage) {
                var w = 0.0;
                var x = 0.0;
                w = parseFloat((100 - vm.billOffline.discountPercentage).toFixed(2));
                x = parseFloat(((vm.billOffline.subTotal * 100) / w).toFixed(2));
                vm.billOffline.discount = parseFloat((x * (vm.billOffline.discountPercentage / 100)).toFixed(2));
            } else if (vm.paraticularDiscount) {
                _.each(vm.billOffline.detailsOffline, function(detail) {
                    vm.billOffline.discount = vm.billOffline.discount + (detail.quantity * (detail.costEach * (detail.discount / 100)));
                });
            } else {
                vm.billOffline.discount = 0;
                _.each(vm.billOffline.detailsOffline, function(detail) {
                    vm.billOffline.discount = vm.billOffline.discount - (detail.quantity * (detail.costEach * (detail.discount / 100)));
                });
                vm.billOffline.discount = vm.billOffline.discount * -1;
            };

            vm.impuestoICE.base_imponible = 0.0;
            vm.impuestoIVA.base_imponible = parseFloat((vm.billOffline.baseTaxes).toFixed(2));
            vm.impuestoIVAZero.base_imponible = parseFloat((vm.billOffline.baseNoTaxes).toFixed(2));
            vm.impuestoICE.valor = vm.billOffline.ice;
            vm.impuestoIVA.valor = vm.billOffline.iva;
            vm.impuestoIVAZero.valor = 0.0;
            vm.billOffline.total = parseFloat((parseFloat(vm.billOffline.baseTaxes)
                +  parseFloat(vm.billOffline.iva)
                +  parseFloat(vm.billOffline.baseNoTaxes)).toFixed(2));
            vm.consumption.total = (parseFloat(vm.consumption.baseTaxes)
                + parseFloat(vm.consumption.baseNoTaxes)
                + parseFloat(vm.consumption.iva)).toFixed(2);
        };

        vm.acceptNewSeq = function() {
            vm.seqErrorNumber = undefined;
            vm.loading = true;
            var stringSeq =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
            billOfflineService.getBillsOfflineByStringSeq(stringSeq, vm.companyData.id).then(function(response) {
                if (response.length === 0) {
                    vm.seqNumber = stringSeq;
                    vm.numberAddedOne = parseInt(vm.seqNumberSecondPart);
                    vm.seqChanged = true;
                    $('#modalEditBillNumber').modal('hide');
                } else {
                    vm.seqErrorNumber = "NUMERO DE FACTURA YA EXISTENTE";
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.reCalculateTotal = function() {
            _getTotalSubtotal();
        };

        function _filterProduct() {
            vm.totalPages = 0;
            var variable = vm.searchText? vm.searchText : null;
            if (variable == null) {
                var busqueda = variable;
            } else {
                var busqueda = variable.toUpperCase();
            };
            productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, busqueda).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.billOffline.detailsOffline, function(detail) {
                        return detail.product.id === response.content[i].id;
                    });
                    if (productFound === undefined) {
                        var sub = _.find(response.content[i].productBySubsidiaries, function(s) {
                            return (s.subsidiary.id === $localStorage.user.subsidiary.id && s.active === true);
                        });
                        if (sub) {
                            response.content[i].quantity = sub.quantity
                            vm.productList.push(response.content[i]);
                        };
                    };
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.filterEvent = function(event) {
            vm.page = 0;
            if (event.keyCode === 13 || event.charCode === 13) {
                _filterProduct();
            };
        };

        vm.filter = function() {
            vm.page = 0;
            _filterProduct();
        };

        vm.paginate = function(page) {
            vm.page = page;
            _filterProduct();
        };

        vm.getActiveClass = function(index) {
            var classActive = vm.page === index? 'active' : '';
            return classActive;
        };

        vm.range = function() {
            return new Array(vm.totalPages);
        };

        vm.addProduct = function() {
            vm.indexDetail = undefined;
            vm.loading = true;
            vm.errorQuantity = undefined;
            vm.page = 0;
            vm.searchText = undefined;
            setTimeout(function() {
                document.getElementById("prod011").focus();
            }, 200);
            _filterProduct();
        };

        vm.selectProductToAdd = function(productSelect) {
            if (vm.quantity == 0 || vm.quantity == undefined) {
                vm.quantity = 1;
            };
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            };
            vm.productToAdd = angular.copy(productSelect);
            var costEachCalculated = vm.getCost(productSelect[vm.priceType.cod], productSelect.averageCost);
            vm.productToAdd.costEachCalculated = costEachCalculated;
        };

        vm.addProdBarCode = function() {
            if (vm.toAdd !== undefined) {
                vm.productToAdd = angular.copy(vm.toAdd);
                vm.selectProductToAdd(vm.productToAdd);
                vm.acceptProduct(true);
                vm.billOfflineBarCode = undefined;
                vm.toAdd = undefined;
                vm.paraticularDiscount = undefined;
            };
        };

        vm.cancelBarCode = function(){
            vm.billOfflineBarCode = undefined;
              vm.toAdd = undefined;
              vm.quantity = undefined;
        };

        vm.openPayMode = function(event){
            if (event.keyCode === 102 || event.charCode === 102 || event.keyCode === 70 || event.charCode === 70) {
                $('#modalAddPago').modal('show');
                vm.medio.medio = vm.medList[0];
                vm.loadMedio();
                setTimeout(function() {
                    document.getElementById("addPaymentBtn").focus();
                }, 500);
            };
        };

        vm.fill = function(event) {
            if (event.keyCode === 32 || event.charCode === 32) {
                if (vm.billOfflineBarCode.length < 13) {
                    vm.billOfflineBarCodeFixed = vm.billOfflineBarCode;
                    for (var i = vm.billOfflineBarCode.length; i < 13; i++) {
                        vm.billOfflineBarCodeFixed = vm.billOfflineBarCodeFixed.concat('0');
                    };
                    vm.billOfflineBarCode = vm.billOfflineBarCodeFixed.trim();
                    vm.filterBarCode();
                };
            };
        };

        vm.acceptProduct = function(closeModal) {
            vm.errorQuantity = undefined;
            if (vm.billOffline.discountPercentage == null || vm.billOffline.discountPercentage == undefined) {
                vm.billOffline.discountPercentage = 0;
            };
            if (vm.paraticularDiscount == null || vm.paraticularDiscount == undefined) {
                vm.paraticularDiscount = 0;
            };
            var discount = 1;
            if ((vm.paraticularDiscount !== null || vm.paraticularDiscount !== undefined) && !isNaN(vm.paraticularDiscount)) {
                discount = (parseFloat(vm.paraticularDiscount) / 100);
            } else if ((vm.billOffline.discountPercentage !== null || vm.billOffline.discountPercentage !== undefined) && !isNaN(vm.billOffline.discountPercentage)) {
                discount = (parseFloat(vm.billOffline.discountPercentage) / 100);
            };
            var detail = {
                discount: vm.billOffline.discountPercentage ? vm.billOffline.discountPercentage : vm.paraticularDiscount ? vm.paraticularDiscount : 0,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: parseFloat(((vm.quantity * vm.productToAdd.costEachCalculated) - (vm.quantity * (vm.productToAdd.costEachCalculated) * discount)).toFixed(2)),
                adicional: vm.adicional
            };
            if (vm.indexDetail !== undefined) {
                vm.billOffline.detailsOffline[vm.indexDetail] = detail;
                vm.consumption.detailsConsumption[vm.indexDetail] = detail;
            } else {
                vm.billOffline.detailsOffline.push(detail);
                vm.consumption.detailsConsumption.push(detail);
            };
            vm.productToAdd = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            _getTotalSubtotal();
            if (closeModal) {
                $('#modalAddProduct').modal('hide');
                vm.toAdd = undefined;
                vm.toAddExistency = undefined;
                vm.toAddPrice = undefined;
                vm.billOfflineBarCode = undefined;
                document.getElementById("input4").focus();
            } else {
                var newProductList = _.filter(vm.productList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
            vm.paraticularDiscount = undefined;
        };

        vm.editDetail = function(detail, index) {
            vm.indexDetail = index;
            vm.productToAdd = detail.product;
            vm.quantity = detail.quantity;
            vm.adicional = detail.adicional;
            setTimeout(function() {
                document.getElementById("prod011").focus();
            }, 200);
            _getTotalSubtotal();
        };

        vm.removeDetail = function(index) {
            vm.billOffline.detailsOffline.splice(index,1);
            vm.consumption.detailsConsumption.splice(index,1);
            _getTotalSubtotal();
        };

        vm.validateAdm = function() {
            vm.errorValidateAdm = undefined;
            var userAdm = $localStorage.user.user;
            userAdm.password = vm.passwordAdm;
            authService.authUser(userAdm).then(function(response) {
                vm.loading = false;
                vm.isEmp = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.errorValidateAdm = error.data;
            });
        };

        vm.loadMedio = function() {
            var payed = 0;
            _.each(vm.pagosOffline, function(pago) {
                payed += parseFloat(pago.total);
            });
            if (vm.medio.code === 'efectivo' || vm.medio.code === 'transferencia') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.billOffline.total - payed).toFixed(2));
            };
            if (vm.medio.code === 'cheque') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.billOffline.total - payed).toFixed(2));
            };
            if (vm.medio.code === 'tarjeta_credito' || vm.medio.code === 'tarjeta_debito') {
                vm.medio.payForm = '19 - TARJETA DE CREDITO';
                vm.medio.total = parseFloat((vm.billOffline.total - payed).toFixed(2));
                vm.medio.statusPago = 'PAGADO';
            };
        };

        vm.verifyUser = function() {
            vm.isEmp = $localStorage.user.userType.code === 'EMP';
        };

        vm.getCost = function(textCost, averageCost) {
            var aC = 1 + ((parseFloat(textCost)) / 100);
            var cost = aC * averageCost;
            return parseFloat(cost.toFixed(2));
        };

        vm.addPago = function() {
            vm.pagosOffline.push(angular.copy(vm.medio));
            vm.medio = {};
            setTimeout(function(){
                document.getElementById("processBillBtn").focus();
            }, 500);
        };

        vm.removePago = function(index) {
            vm.pagosOffline.splice(index, 1);
        };

        vm.getTotalPago = function() {
            vm.aux = 0;
            vm.varPago = 0;
            if (vm.billOffline) {
                vm.getCambio = 0;
                _.each(vm.pagosOffline, function(med) {
                    vm.varPago = parseFloat(parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2);
                });
                vm.getCambio = (vm.varPago - vm.billOffline.total).toFixed(2);
                vm.aux = (vm.varPago - vm.getCambio).toFixed(2);
            };
            return vm.varPago;
        };

        vm.getTotalPagoB = function() {
            vm.aux = 0;
            vm.varPago = 0;
            if (vm.billOffline) {
                vm.getCambio = 0;
                _.each(vm.pagosOffline, function(med) {
                    vm.varPago = parseFloat((parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2));
                });
                vm.getCambioB = parseFloat((vm.varPago - vm.billOffline.total).toFixed(2));
                vm.aux = parseFloat((vm.varPago - vm.getCambio).toFixed(2));
            };
            return vm.varPago;
        };

        vm.billOfflineSelect = function(billOffline) {
            vm.loading = true;
            billOfflineService.getBillsOfflineById(billOffline.id).then(function(response) {
                vm.billOfflineList = undefined;
                vm.billOffline = response;
                vm.companyData = $localStorage.user.subsidiary;
                vm.clientSelected = response.client;
                vm.pagosOffline = response.pagosOffline;
                var dateToShow = new Date(response.dateCreated);
                vm.seqNumber = response.stringSeq;
                $('#pickerBillOfflineDate').data("DateTimePicker").date(dateToShow);
                vm.newBillOffline = false;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.printToCart = function(printBillOffline) {

            var contentToPrint = 'printMatrixBillOfflineId';
            switch (vm.userClientId) {
                case vm.pineda:
                    contentToPrint = 'printMatrixBillIdPineda';
                    break;
                default:
                    contentToPrint = 'printMatrixBillOfflineId';
            }

            var innerContents = document.getElementById(printBillOffline).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillOffline', 'width=300,height=400');
            popupWinindow.document.write('<html><head><title></title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate()
        };

        vm.printBillThermal = function(billPrint) {
            var innerContents = document.getElementById(billPrint).innerHTML;
            var texto = innerContents.fixed();
            var popupWinindow = window.open('', 'billPrint', 'width=300,height=400');
            popupWinindow.document.write('<html><body>');
            popupWinindow.document.write(texto);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate()
        };

        vm.cancelBillOffline = function() {
            _activate();
        };

        vm.getDateToPrint = function() {
            if (vm.billOffline != undefined) {
                return $('#pickerBillOfflineDate').data("DateTimePicker").date().toDate();
            };
        };

        vm.billOfflineDeactivate = function() {
            vm.loading = true;
            var index = vm.billOfflineList.indexOf(vm.cancelaBillOffline);
            if (vm.cancelaBillOffline.creditNoteApplied === false) {
                billOfflineService.deactivateBillOffline(vm.cancelaBillOffline).then(function(response) {
                    var index = vm.billOfflineList.indexOf(vm.cancelaBillOffline);
                    if (index > -1) {
                        vm.billOfflineList.splice(index, 1);
                    };
                    vm.cancelaBillOffline = undefined;
                    vm.success = "Factura Offline Anulada";
                    vm.loading = false;
                  }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                  });
            } else {
                vm.loading = false;
                vm.error = "La Factura NO se Puede Anular debido a que presenta una Nota de Crédito";
            };
          };

        vm.saveBillOffline = function() {
            vm.loading = true;
            $('#modalAddPago').modal('hide');
            vm.impuestosTotales = [];
            vm.billOffline.detailsKardexOffline = [];
            if (vm.billOffline.observation === null || vm.billOffline.observation === undefined || vm.billOffline.observation === '') {
                vm.billOffline.observation = '--';
            };
            if (vm.impuestoIVA.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVA);
            };
            if (vm.impuestoIVAZero.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVAZero);
            };
            if (vm.billOffline.discountPercentage === undefined) {
                vm.billOffline.discountPercentage = 0;
            };
            _.each(vm.billOffline.detailsOffline, function(det) {
                var costWithIva = parseFloat((det.total * 1.12).toFixed(2));
                var costWithIce = parseFloat((det.total * 1.10).toFixed(2));
                var impuestos = [];
                var impuesto = {};

                if (det.product.iva) {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.billOffline.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(2));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.12).toFixed(2));
                    impuesto.tarifa = 12.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                } else {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.billOffline.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(2));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.0000).toFixed(2));
                    impuesto.tarifa = 0.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '0';
                    impuestos.push(impuesto);
                };
                if (det.product.ice) {
                    impuesto.base_imponible = parseFloat((det.costEach).toFixed(2));
                    impuesto.valor = costWithIce;
                    impuesto.tarifa = 10.0;
                    impuesto.codigo = '3';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                };

                if (vm.billOffline.discountPercentage === undefined) {
                    vm.billOffline.discountPercentage = 0;
                };

                var kardexoff = {
                    billOffline: vm.billOffline.id,
                    product: det.product,
                    codeWarehouse: '--',
                    dateRegister: $('#pickerBillOfflineDate').data("DateTimePicker").date().toDate().getTime(),
                    details: 'FACTURA-OFFLINE-VENTA Nro. ' + vm.seqNumber,
                    observation: 'EGRESO',
                    detalle: '--',
                    prodCostEach: det.costEach,
                    prodName: det.product.name,
                    prodQuantity: det.quantity,
                    prodTotal: det.total,
                    subsidiaryId: vm.subsidiaryId,
                    userClientId: vm.userClientId,
                    userId: vm.userId
                };
                vm.billOffline.detailsKardexOffline.push(kardexoff);
            });

            var obj = {clave_acceso: '1124225675', id:'id12345'};
            vm.billOffline.pagosOffline = vm.pagosOffline;
            vm.billOffline.claveDeAcceso = obj.clave_acceso;
            vm.billOffline.idSri = obj.id;
            vm.billOffline.billSeq = vm.numberAddedOne;
            vm.billOffline.stringSeq = vm.seqNumber;
            vm.billOffline.priceType = vm.priceType.name;
            vm.billOffline.dateCreated = $('#pickerBillOfflineDate').data("DateTimePicker").date().toDate().getTime();

            billOfflineService.getBillsOfflineByStringSeq(vm.seqNumber, vm.companyData.id).then(function(response) {
                if (response.length === 0) {
                    billOfflineService.createBillOffline(vm.billOffline, 1).then(function(respBill) {
                        vm.billedOffline = true;
                        vm.newBillOffline = false;
                        vm.newBuyOff = false;
                        vm.billOfflineCreated = respBill;
                        $localStorage.user.cashier.billOfflineNumberSeq = vm.billOffline.billSeq;
                        if (vm.seqChanged) {
                            cashierService.update($localStorage.user.cashier).then(function(resp) {
                                // cashier updated
                            }).catch(function(error) {
                                vm.loading = false;
                                vm.error = error.data;
                            });
                        };

                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.seqErrorNumber = "NUMERO DE FACTURA YA EXISTENTE";
                    vm.loading = false;
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        //Consumption Code
        function _getCsmSeqNumber() {
            vm.numberCsmAddedOne = parseInt($localStorage.user.cashier.csmNumberSeq) + 1;
            vm.csmSeqNumber = utilSeqService._pad_with_zeroes(vm.numberCsmAddedOne, 6);
        };

        function _initializeConsumption() {
            vm.consumption = {
                client: vm.clientSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                dateConsumption: vm.dateBillOffline.getTime(),
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                baseNoTaxes: 0,
                baseTaxes: 0,
                detailsConsumption: []
            };
        };

        vm.saveConsumptionOff = function(consumption) {
            vm.loading = true;
            $('#modalAddPago').modal('hide');
            _getCsmSeqNumber();
            warehouseService.getAllWarehouseByUserClientId(vm.userClientId).then(function(response) {
                vm.warehouseList = response;
                var finalWarehouse = _.filter(vm.warehouseList, function(warehouse) { return warehouse.subsidiary.id === vm.subsidiaryId});
                vm.consumption.warehouse = finalWarehouse[0];
                vm.consumption.dateConsumption = $('#pickerBillOfflineDate').data("DateTimePicker").date().toDate().getTime();
                vm.consumption.csmSeq = parseInt(vm.numberCsmAddedOne);
                vm.consumption.csmNumberSeq = vm.csmSeqNumber;
                vm.consumption.clientName = vm.clientSelected.name;
                vm.consumption.codeWarehouse = '--';
                vm.consumption.nameSupervisor = '--';
                vm.consumption.observation = 'CONSUMO INTERNO';
                vm.consumption.detailsKardex = [];
                _.each(vm.consumption.detailsConsumption, function(item) {
                    var kardex = {
                        consumption: consumption.id,
                        product: item.product,
                        codeWarehouse: '--',
                        dateRegister: $('#pickerBillOfflineDate').data("DateTimePicker").date().toDate().getTime(),
                        details: 'CONSUMO INTERNO Nro. ' + vm.csmSeqNumber,
                        observation: 'EGRESO',
                        detalle: vm.consumption.observation,
                        prodCostEach: item.costEach,
                        prodName: item.product.name,
                        prodQuantity: item.quantity,
                        prodTotal: item.total,
                        subsidiaryId: vm.subsidiaryId,
                        userClientId: vm.userClientId,
                        userId: vm.userId
                    };
                    vm.consumption.detailsKardex.push(kardex);
                });
                consumptionService.create(consumption).then(function(respConsumption) {
                    $localStorage.user.cashier.csmNumberSeq = vm.numberCsmAddedOne;
                    vm.consumptionCreated = respConsumption;
                    vm.consumptioned = true;
                    vm.newBuyOff = false;
                    vm.newConsumptionOff = false;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
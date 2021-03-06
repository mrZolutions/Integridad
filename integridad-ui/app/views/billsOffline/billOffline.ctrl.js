'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillOfflineCtrl
 * @description
 * # BillOfflineCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('BillOfflineCtrl', function(_, $location, utilStringService, holderService, projectService, consumptionService,
                                            clientService, productService, authService, billOfflineService, cashierService, utilSeqService,
                                            validatorService, warehouseService, countryListService, configCuentasService) {
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
            // {code: 'transferencia', name: 'Transferencia'}
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
            vm.terneza = 'c4d6c059-2de1-412c-aa0e-90a84331e083';
            vm.ordonez = '366cfcb7-195c-4622-b8f2-09cb021e7609';

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
            vm.user = holderService.get();
            vm.userClientId = vm.user.subsidiary.userClient.id;
            vm.subsidiaryId = vm.user.subsidiary.id;
            vm.subOfflineActive = vm.user.subsidiary.offline;
            vm.userId = vm.user.id;
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

            configCuentasService.getConfigCuentaByUserClientAndOptionCode(vm.userClientId, 'FACTEF').then(function (response){
                vm.cuentaContableEfectivo = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getComprobanteCobroSeqNumber() {
            vm.numbAddedOne = parseInt(vm.user.cashier.compCobroNumberSeq) + 1;
            vm.comprobanteCobroSeq = vm.numbAddedOne;
            vm.comprobanteCobroStringSeq = utilSeqService._pad_with_zeroes(vm.numbAddedOne, 6);
        };

        function _getDailyCiSeqNumber() {
            vm.numberAddedOne = parseInt(vm.user.cashier.dailyCiNumberSeq) + 1;
            vm.dailyCiSeq = vm.numberAddedOne;
            vm.dailyCiStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
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
                userIntegridad: vm.user,
                subsidiary: vm.user.subsidiary,
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
                productService.getLazyBySusidiaryIdBarCode(vm.user.subsidiary.id, vm.billOfflineBarCode).then(function(response) {
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
            vm.companyData = vm.user.subsidiary;
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
                    userClient: vm.user.subsidiary.userClient
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
            if (vm.billOffline.discountPercentage == null || vm.billOffline.discountPercentage == undefined || vm.billOffline.discountPercentage == '') {
                vm.billOffline.discountPercentage = 0;
            };
            _.map(vm.billOffline.detailsOffline, function(detail) {
                detail.discount = parseFloat(vm.billOffline.discountPercentage) + parseFloat(detail.discountData);
                var costEachCalculated = vm.getCost(detail.product[vm.priceType.cod], detail.product.averageCost);
                detail.costEach = costEachCalculated;
                detail.total = parseFloat((parseFloat(detail.quantity) * (parseFloat(detail.costEach) - (parseFloat(detail.costEach) * parseFloat(detail.discount / 100)))).toFixed(4));
            });
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
            productService.getLazyBySusidiaryId(vm.user.subsidiary.id, vm.page, busqueda, undefined).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    // var productFound = _.find(vm.billOffline.detailsOffline, function(detail) {
                    //     return detail.product.id === response.content[i].id;
                    // });
                    // if (productFound === undefined) {
                        var sub = _.find(response.content[i].productBySubsidiaries, function(s) {
                            return (s.subsidiary.id === vm.user.subsidiary.id && s.active === true);
                        });
                        if (sub) {
                            response.content[i].quantity = sub.quantity
                            vm.productList.push(response.content[i]);
                        };
                    // };
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

        vm.openModalPayMode = function(){
            $('#modalAddPago').modal('show');
                vm.medio = vm.medList[0];
                vm.loadMedio();
                var elementToSelect = document.getElementById("medioTotal");
                setTimeout(function() {
                    elementToSelect.focus();
                    elementToSelect.select();
                }, 500);
        }

        vm.openPayMode = function(event){
            if (event.keyCode === 102 || event.charCode === 102 || event.keyCode === 70 || event.charCode === 70) {
                vm.openModalPayMode();
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

        vm.getTextCambio = function() {
            return vm.getCambio <= 0 ? 0 : vm.getCambio;
        };

        vm.acceptProduct = function(closeModal) {
            vm.errorQuantity = undefined;
            if (vm.billOffline.discountPercentage == null || vm.billOffline.discountPercentage == undefined) {
                vm.billOffline.discountPercentage = 0;
            };
            var discountDetail = vm.paraticularDiscount ? parseFloat(vm.paraticularDiscount) + parseFloat(vm.billOffline.discountPercentage) : parseFloat(vm.billOffline.discountPercentage);
            var detail = {
                discountData: vm.paraticularDiscount,
                discount: discountDetail,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: parseFloat(((vm.quantity * vm.productToAdd.costEachCalculated) - (vm.quantity * (vm.productToAdd.costEachCalculated) * (discountDetail/100))).toFixed(2)),
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
            } 
            // else {
            //     var newProductList = _.filter(vm.productList, function(prod) {
            //         return prod.id !== detail.product.id;
            //     });
            //     vm.productList = newProductList;
            // };
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
            var userAdm = vm.user.user;
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
            vm.isEmp = vm.user.userType.code === 'EMP';
        };

        vm.getCost = function(textCost, averageCost) {
            var aC = 1 + ((parseFloat(textCost)) / 100);
            var cost = aC * averageCost;
            return parseFloat(cost.toFixed(2));
        };

        vm.addPago = function() {
            vm.medio.medio = vm.medio.code;
            if(vm.medio.code === 'efectivo'){
                var credit = {
                    payNumber: 1,
	                diasPlazo: 1,
	                fecha: new Date().getTime(),
	                statusCredits: "PAGADO",
	                documentNumber: vm.seqNumber,
	                valor: vm.billOffline.total,
                };
                vm.medio.creditoNumeroPagos= 1,
                vm.medio.creditoIntervalos= 1,
                vm.medio.credits=[credit];
            }
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
                vm.companyData = vm.user.subsidiary;
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

        vm.printToCart = function() {

            var contentToPrint = 'printMatrixBillOfflineId';
            switch (vm.userClientId) {
                case vm.terneza:
                    contentToPrint = 'printMatrixBillIdPineda';
                    break;
                case vm.ordonez:
                    contentToPrint = 'printMatrixBillIdOrdonez';
                    break;
                default:
                    contentToPrint = 'printMatrixBillOfflineId';
            }

            var innerContents = document.getElementById(contentToPrint).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillOffline', 'width=300,height=400');
            popupWinindow.document.write('<html><head><title></title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.document.body.style.cssText = 'font-family:Arial, Helvetica, sans-serif !important; font-size: 12px';
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

                if(vm.userClientId !== vm.terneza)
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
                    vm.comprobanteCobro = {};
                    vm.dailybookCi = {};

                    if(vm.pagosOffline.length === 1){
                        if(vm.pagosOffline[0].code === 'efectivo'){
                            vm.comprobanteCobro = {
                                client: vm.clientSelected,
                                userIntegridad: vm.user,
                                subsidiary: vm.user.subsidiary,
                                clientName: vm.clientSelected.name,
                                clientRuc: vm.clientSelected.identification,
                                subTotalDoce: 0,
                                iva: 0,
                                total: 0,
                                detailComprobanteCobro: []
                            };
                            
                            vm.itemBill = {
                                numCheque: '-',
                                cuenta: '-',
                                banco: '-',
                                tipoAbono: 'EFC',
                                totalAbono: vm.billOffline.total,
                                billNumber: vm.billOffline.stringSeq,
                                dateBill: vm.billOffline.dateCreated
                            };
                
                            _getComprobanteCobroSeqNumber();
                
                            vm.comprobanteCobro.detailComprobanteCobro.push(vm.itemBill);
                            vm.comprobanteCobro.billNumber = vm.billOffline.stringSeq;
                            vm.comprobanteCobro.dateComprobante = vm.billOffline.dateCreated;
                            vm.comprobanteCobro.dateComprobanteCreated = vm.billOffline.dateCreated;
                            vm.comprobanteCobro.comprobanteSeq = vm.comprobanteCobroSeq;
                            vm.comprobanteCobro.comprobanteStringSeq = vm.comprobanteCobroStringSeq;
                            vm.comprobanteCobro.comprobanteConcep = 'Cancela Fact. ' + vm.billOffline.stringSeq;
                            vm.comprobanteCobro.comprobanteEstado = 'PROCESADO';
                            vm.comprobanteCobro.total = vm.billOffline.total;
                            vm.comprobanteCobro.subTotalDoce = parseFloat((vm.billOffline.total / 1.12).toFixed(2));
                            vm.comprobanteCobro.iva = parseFloat((vm.billOffline.total * 0.12).toFixed(2));
                            vm.comprobanteCobro.paymentId = '-';

                            vm.dailybookCi = {
                                client: vm.clientSelected,
                                userIntegridad: vm.user,
                                subsidiary: vm.user.subsidiary,
                                clientProvName: vm.clientSelected.name,
                                subTotalDoce: 0,
                                iva: 0,
                                subTotalCero: 0,
                                total: 0,
                                detailDailybookContab: []
                            };
                            _getDailyCiSeqNumber();
                            vm.selectedTypeBook = '3';
                            vm.generalDetailCi_1 = vm.clientSelected.name + ' Cancela Facts. ' + vm.billOffline.stringSeq;
                            vm.typeContabCi = 'COMP. DE INGRESO';
                            vm.itema = {
                                typeContab: vm.typeContabCi,
                                codeConta: vm.clientSelected.codConta,
                                descrip: 'CLIENTES NO RELACIONADOS',
                                tipo: 'CREDITO (C)',
                                baseImponible: parseFloat(vm.billOffline.total),
                                name: vm.generalDetailCi_1,
                                haber: parseFloat(vm.billOffline.total)
                            };
                            vm.itema.numCheque = '--';
                            vm.itema.dailybookNumber = vm.dailyCiStringSeq;
                            vm.itema.userClientId = vm.userClientId;
                            vm.itema.dateDetailDailybook = vm.billOffline.dateCreated;
                            vm.dailybookCi.detailDailybookContab.push(vm.itema);
                            vm.generalDetailCi_2 = 'Cobro de Facts. ' + vm.billOffline.stringSeq + ' en EFECTIVO';
                            // Todo DEFINIR CAMPOS DE CODE CONTA Y DESCRIPCION
                            vm.itemb = {
                                typeContab: vm.typeContabCi,
                                codeConta: vm.cuentaContableEfectivo ? vm.cuentaContableEfectivo.code : '--',
                                descrip: vm.cuentaContableEfectivo ? vm.cuentaContableEfectivo.description : '--',
                                tipo: 'DEBITO (D)',
                                baseImponible: parseFloat(vm.billOffline.total),
                                name: vm.generalDetailCi_2,
                                deber: parseFloat(vm.billOffline.total)
                            };
                            vm.itemb.numCheque = '--';
                            vm.itemb.dailybookNumber = vm.dailyCiStringSeq;
                            vm.itemb.userClientId = vm.userClientId;
                            vm.itemb.dateDetailDailybook = vm.billOffline.dateCreated;
                            vm.dailybookCi.detailDailybookContab.push(vm.itemb);
                            vm.dailybookCi.codeTypeContab = vm.selectedTypeBook;
                            vm.dailybookCi.nameBank = '--';
                            vm.dailybookCi.billNumber = vm.billOffline.stringSeq;
                            vm.dailybookCi.numCheque = '--';
                            vm.dailybookCi.typeContab = vm.typeContabCi;
                            vm.dailybookCi.dailyCiSeq = vm.dailyCiSeq;
                            vm.dailybookCi.dailyCiStringSeq = vm.dailyCiStringSeq;
                            vm.dailybookCi.dailyCiStringUserSeq = 'PAGO GENERADO ' + vm.dailyCiStringSeq;
                            vm.dailybookCi.clientProvName = vm.clientSelected.name;
                            vm.dailybookCi.generalDetail = vm.generalDetailCi_1;
                            vm.dailybookCi.total = vm.billOffline.total;
                            vm.dailybookCi.iva = parseFloat((vm.billOffline.total * 0.12).toFixed(2));
                            vm.dailybookCi.subTotalDoce = parseFloat((vm.billOffline.total / 1.12).toFixed(2));
                            vm.dailybookCi.subTotalCero = 0;
                            vm.dailybookCi.dateRecordBook = vm.billOffline.dateCreated;
                        }
                    }
                    var requirement = {
                        bill:vm.billOffline,
                        comprobanteCobro: vm.comprobanteCobro,
                        dailybookCi: vm.dailybookCi,
                    }

                    billOfflineService.createBillOffline(requirement, 1).then(function(respBill) {
                        vm.billedOffline = true;
                        vm.newBillOffline = false;
                        vm.newBuyOff = false;
                        vm.billOfflineCreated = respBill;
                        vm.user.cashier.billOfflineNumberSeq = vm.billOffline.billSeq;
                        if(vm.comprobanteCobro.comprobanteSeq !== undefined){
                            vm.user.cashier.compCobroNumberSeq = vm.comprobanteCobro.comprobanteSeq;
                            vm.user.cashier.dailyCiNumberSeq = vm.dailybookCi.dailyCiSeq;
                        }
                        holderService.set(vm.user)
                        vm.loading = false;
                        vm.rowsToFill = [];
                        for (var i = vm.billOffline.detailsOffline.length; i < 10; i++) {
                            vm.rowsToFill.push(i)
                        }

                        setTimeout(function() {
                            // vm.user.cashier.specialPrint ? vm.printToCart('printMatrixBillId') : document.getElementById("printBtnBill").click();
                            // document.getElementById("printBtnBill").click();
                            vm.printToCart('printMatrixBillId')
                            // vm.nuevaBill();
                        }, 300);
                        if (vm.seqChanged) {
                            cashierService.update(vm.user.cashier).then(function(resp) {
                                // cashier updated
                            }).catch(function(error) {
                                vm.loading = false;
                                vm.error = error.data;
                            });
                        };
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
            vm.numberCsmAddedOne = parseInt(vm.user.cashier.csmNumberSeq) + 1;
            vm.csmSeqNumber = utilSeqService._pad_with_zeroes(vm.numberCsmAddedOne, 6);
        };

        function _initializeConsumption() {
            vm.consumption = {
                client: vm.clientSelected,
                userIntegridad: vm.user,
                subsidiary: vm.user.subsidiary,
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
                    vm.user.cashier.csmNumberSeq = vm.numberCsmAddedOne;
                    holderService.set(vm.user);
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
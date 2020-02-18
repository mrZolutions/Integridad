'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # BillCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('BillCtrl', function(_, $location, utilStringService, $localStorage, consumptionService,
                                     clientService, productService, authService, billService, warehouseService,
                                     cashierService, requirementService, utilSeqService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.isEmp = true;
        vm.prices = [
            {name: 'EFECTIVO', cod: 'cashPercentage'}, {name: 'MAYORISTA', cod: 'majorPercentage'},
            {name: 'CREDITO', cod: 'creditPercentage'}, {name: 'TARJETA', cod: 'cardPercentage'}
        ];
        vm.medList = [
            {code: 'efectivo', name: 'Efectivo'},
            {code: 'cheque', name: 'Cheque'},
            {code: 'cheque_posfechado', name: 'Cheque posfechado'},
            {code: 'tarjeta_credito', name: 'Tarjeta de crédito'},
            {code: 'tarjeta_debito', name: 'Tarjeta de débito'},
            {code: 'dinero_electronico_ec', name: 'Dinero electrónico'},
            {code: 'credito', name: 'Crédito'},
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
            vm.clientList = undefined;
            vm.error = undefined;
            vm.aux = undefined;
            vm.estado = undefined;
            vm.newBill = true;
            vm.newConsumptionOn = true;
            vm.newBuy = true;
            vm.billed = false;
            vm.clientSelected = undefined;
            vm.dateBill = undefined;
            vm.seqNumber = undefined;
            vm.csmSeqNumber = undefined;
            vm.productList = undefined;
            vm.productToAdd = undefined;
            vm.pagoTot = undefined;
            vm.quantity = undefined;
            vm.userClientId = undefined;
            vm.adicional = undefined;
            vm.detailList = undefined;
            vm.indexDetail = undefined;
            vm.priceType = vm.prices[0];
            vm.seqChanged = false;
            vm.quotations = undefined;
            vm.impuestosTotales = [];
            vm.items = [];
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
            vm.pagos = [];
            vm.user = $localStorage.user;
            vm.userId = $localStorage.user.id;
            vm.userClientId = $localStorage.user.subsidiary.userClient.id;
            vm.subsidiaryId = $localStorage.user.subsidiary.id;
            vm.subOnlineActive = $localStorage.user.subsidiary.online;
            vm.userCashier = $localStorage.user.cashier;
            vm.userSubsidiary = $localStorage.user.subsidiary;
            if (vm.subOnlineActive) {
                vm.loading = true;
                clientService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                    vm.clientList = response;
                    var finalConsumer = _.filter(vm.clientList, function(client){ return client.identification === '9999999999999'});
                    vm.clientSelect(finalConsumer[0]);
                    _getSeqNumber();
                    _initializeBill();
                    _initializeConsumption();
                    var today = new Date();
                    $('#pickerBillDate').data("DateTimePicker").date(today);
                    setTimeout(function() {
                        document.getElementById("input4").focus();
                    }, 500);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.advertencia = true;
            };
        };

        vm.getDetailsOfBills = function() {
            vm.loading = true;
            billService.getDetailsOfBillsByUserClientId(vm.userClientId).then(function(response) {
                vm.detailList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.volver = function() {
            _activate();
        };

        vm.nuevaBill = function() {
            _activate();
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt(vm.userCashier.billNumberSeq) + 1;
            vm.seqNumberFirstPart = vm.userSubsidiary.threeCode + '-' + vm.userCashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
        };

        function _initializeBill() {
            vm.bill = {
                client: vm.clientSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                dateCreated: vm.dateBill.getTime(),
                discount: 0,
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                details: [],
                pagos: []
            };
        };

        vm.filterBarCode = function(){
            if(vm.billBarCode.length === 13){
                productService.getLazyBySusidiaryIdBarCode($localStorage.user.subsidiary.id, vm.billBarCode).then(function(response) {
                    if(!_.isEmpty(response)) {
                        vm.quantity = 1;
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

        vm.clientSelect = function(client) {
            vm.quotations = [];
            vm.companyData = vm.userSubsidiary;
            vm.dateBill = new Date();
            vm.clientSelected = client;
            vm.pagos = [];
            setTimeout(function() {
                document.getElementById("input4").focus();
            }, 500);
        };

        vm.clientSelectChanged = function(client) {
            vm.clientSelected = client;
            vm.bill.client = vm.clientSelected;
            vm.consumption.client = vm.clientSelected;
            vm.pagos = [];
            setTimeout(function() {
                document.getElementById("input4").focus();
            }, 500);
        };

        vm.clientConsult = function(client) {
            vm.loading = true;
            billService.getAllBillsByClientId(client.id).then(function(response) {
                vm.billList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientChange = function() {
            vm.clientSelected = undefined;
            setTimeout(function() {
                document.getElementById("inputCl0").focus();
            }, 200);
        };

        function _getTotalSubtotal() {
            vm.bill.subTotal = 0.0;
            vm.bill.iva = 0.0;
            vm.bill.ivaZero = 0.0;
            vm.bill.ice = 0.0;
            vm.bill.baseTaxes = 0.0;
            vm.bill.baseNoTaxes = 0.0;
            //Calculos para Consumos
            vm.consumption.subTotal = 0;
            vm.consumption.iva = 0;
            vm.consumption.ivaZero = 0;
            vm.consumption.ice = 0;
            vm.consumption.baseTaxes = 0;
            vm.consumption.baseNoTaxes = 0;

            var discountWithIva = 0.0;
            var discountWithNoIva = 0.0;
            _.each(vm.bill.details, function(detail) {
                vm.bill.subTotal = parseFloat((parseFloat(vm.bill.subTotal) + parseFloat(detail.total)).toFixed(4));
                vm.consumption.subTotal = vm.bill.subTotal;
                var tot = parseFloat((detail.total).toFixed(4));
                if (vm.bill.discountPercentage) {
                    tot = parseFloat((detail.total).toFixed(4));
                };
                if (detail.product.iva) {
                    vm.bill.baseTaxes += parseFloat((detail.total).toFixed(4));
                    vm.consumption.baseTaxes = vm.bill.baseTaxes;
                    vm.bill.iva = parseFloat((parseFloat(vm.bill.iva) + (parseFloat(tot) * 0.12)).toFixed(4));
                    vm.consumption.iva = vm.bill.iva;
                    if (vm.bill.discountPercentage) {
                        discountWithIva = parseFloat((parseFloat(discountWithIva) + (parseFloat(detail.total) * parseFloat((vm.bill.discountPercentage) / 100))).toFixed(4));
                    };
                } else {
                    vm.bill.baseNoTaxes += parseFloat((detail.total).toFixed(4));
                    vm.consumption.baseNoTaxes = vm.bill.baseNoTaxes;
                    vm.bill.ivaZero = parseFloat((parseFloat(vm.bill.ivaZero) + parseFloat(tot)).toFixed(4));
                    vm.consumption.ivaZero = vm.bill.ivaZero;
                    if (vm.bill.discountPercentage) {
                        discountWithNoIva = parseFloat((parseFloat(discountWithNoIva) + ((parseFloat(vm.bill.discountPercentage) / 100) * parseFloat(detail.total))).toFixed(4));
                    };
                };
                if (detail.product.ice) {
                    vm.bill.ice = parseFloat((parseFloat(vm.bill.ice) + (parseFloat(tot) * 0.10)).toFixed(4));
                };
            });
            if (vm.bill.discountPercentage) {
                var w = 0.0;
                var x = 0.0;
                w = parseFloat((100 - vm.bill.discountPercentage).toFixed(4));
                x = parseFloat(((vm.bill.subTotal * 100) / w).toFixed(4));
                vm.bill.discount = parseFloat((x * (vm.bill.discountPercentage / 100)).toFixed(4));
            } else {
                vm.bill.discount = 0;
            };
            vm.impuestoICE.base_imponible = parseFloat((vm.bill.subTotal).toFixed(4));
            vm.impuestoIVA.base_imponible = parseFloat((vm.bill.baseTaxes).toFixed(4));
            vm.impuestoIVAZero.base_imponible = parseFloat((vm.bill.baseNoTaxes).toFixed(4));
            vm.impuestoICE.valor = vm.bill.ice;
            vm.impuestoIVA.valor = vm.bill.iva;
            vm.impuestoIVAZero.valor = 0.0;
            vm.bill.baseTaxes = parseFloat((vm.bill.baseTaxes).toFixed(4));
            vm.bill.baseNoTaxes = parseFloat((vm.bill.baseNoTaxes).toFixed(4));
            vm.bill.total = parseFloat((parseFloat(vm.bill.baseTaxes)
                +  parseFloat(vm.bill.baseNoTaxes)
                +  parseFloat(vm.bill.iva)
                +  parseFloat(vm.bill.ice)).toFixed(2));
            vm.consumption.total = (parseFloat(vm.consumption.baseTaxes)
                + parseFloat(vm.consumption.baseNoTaxes)
                + parseFloat(vm.consumption.iva)).toFixed(2);
        };

        function _addDays(date, days) {
            var result = new Date(date);
            result.setDate(result.getDate() + days);
            return result.getTime();
        };

        vm.acceptNewSeq = function() {
            vm.seqErrorNumber = undefined;
            vm.loading = true;
            var stringSeq =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
            billService.getByStringSeq(stringSeq, vm.companyData.id).then(function(response) {
                if (response.length === 0) {
                    vm.seqNumber = stringSeq;
                    vm.numberAddedOne = parseInt(vm.seqNumberSecondPart);
                    vm.seqChanged = true;
                    $('#modalEditBillNumber').modal('hide');
                } else {
                    vm.seqErrorNumber = "NUMERO DE FACTURA YA EXISTENTE"
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.reCalculateTotal = function() {
            if (vm.bill.discountPercentage == null || vm.bill.discountPercentage == undefined) {
                vm.bill.discountPercentage = 0;
            };
            _.map(vm.bill.details, function(detail) {
                if (vm.bill.discountPercentage) {
                    detail.discount = vm.bill.discountPercentage;
                } else {
                    detail.discount = 0;
                };
                var costEachCalculated = vm.getCost(detail.product[vm.priceType.cod], detail.product.averageCost);
                detail.costEach = costEachCalculated;
                detail.total = parseFloat((parseFloat(detail.quantity) * (parseFloat(detail.costEach) - (parseFloat(detail.costEach) * parseFloat(detail.discount / 100)))).toFixed(4));
            });
            _getTotalSubtotal();
        };

        vm.clientCreate = function() {
            $location.path('/clients/create');
        };

        function _filterProduct() {
            vm.totalPages = 0;
            var variable = vm.searchText? vm.searchText : null;
            if (variable == null) {
                var busqueda = variable;
            } else {
                var busqueda = variable.toUpperCase();
            };
            productService.getLazyBySusidiaryIdForBill($localStorage.user.subsidiary.id, vm.page, busqueda).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.bill.details, function(detail) {
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
                vm.billBarCode = undefined;
                vm.toAdd = undefined;
            };
        };

        vm.cancelBarCode = function(){
            vm.billBarCode = undefined;
            vm.toAdd = undefined;
            vm.quantity = undefined;
        };

        vm.fill = function(event){
            if (event.keyCode === 32 || event.charCode === 32) {
                if (vm.billBarCode.length < 13) {
                    vm.billBarCodeFixed = vm.billBarCode;
                    for (var i = vm.billBarCode.length; i < 13; i++) {
                        vm.billBarCodeFixed = vm.billBarCodeFixed.concat('0');
                    };
                    vm.billBarCode = vm.billBarCodeFixed.trim();
                    vm.filterBarCode();
                };
            };
            if (event.keyCode === 102 || event.charCode === 102 || event.keyCode === 70 || event.charCode === 70) {
                $('#modalAddPago').modal('show');
                vm.medio.medio = vm.medList[0];
                vm.loadMedio();
                setTimeout(function() {
                    document.getElementById("addPaymentBtn").focus();
                }, 500);
            };
        };

        vm.acceptProduct = function(closeModal) {
            vm.errorQuantity = undefined;
            if (vm.bill.discountPercentage == null || vm.bill.discountPercentage == undefined) {
                vm.bill.discountPercentage = 0;
            };
            var detail = {
                discount: vm.bill.discountPercentage ? vm.bill.discountPercentage : 0,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: parseFloat(((vm.quantity * vm.productToAdd.costEachCalculated) - (vm.quantity * (vm.productToAdd.costEachCalculated) * (vm.bill.discountPercentage / 100))).toFixed(4)),
                adicional: vm.adicional
            };
            if (vm.indexDetail !== undefined) {
                vm.bill.details[vm.indexDetail] = detail;
                vm.consumption.detailsConsumption[vm.indexDetail] = detail;
            } else {
                vm.bill.details.push(detail);
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
                vm.billBarCode = undefined;
                document.getElementById("input4").focus();
            } else {
                var newProductList = _.filter(vm.productList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
        };

        vm.getCost = function(textCost, averageCost) {
            var aC = 1 + ((parseFloat(textCost)) / 100);
            var cost = aC * averageCost;
            return parseFloat(cost.toFixed(4));
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
            vm.bill.details.splice(index,1);
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

        vm.verifyUser = function() {
            vm.isEmp = $localStorage.user.userType.code === 'EMP';
        };

        vm.loadMedio = function() {
            var payed = 0;
            _.each(vm.pagos, function(pago) {
                payed += parseFloat(pago.total);
            });
            if (vm.medio.medio === 'efectivo' || vm.medio.medio === 'dinero_electronico_ec' || vm.medio.medio === 'transferencia') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
                // CAMBIO SRI POR CONFIRMAR
                // vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
            };
            if (vm.medio.medio === 'credito') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                // CAMBIO SRI POR CONFIRMAR
                // vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
                if (vm.priceType.name === 'CREDITO') {
                    vm.medio.statusPago = 'PENDIENTE';
                } else {
                    vm.medio.statusPago = 'PAGADO';
                };
            };
            if (vm.medio.medio === 'cheque' || vm.medio.medio === 'cheque_posfechado') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
            };
            if (vm.medio.medio === 'tarjeta_credito' || vm.medio.medio === 'tarjeta_debito') {
                vm.medio.payForm = '19 - TARJETA DE CREDITO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
                vm.medio.statusPago = 'PAGADO';
            };
        };

        vm.loadCredit = function() {
            var creditArray = [];
            var diasPlazo = parseInt(vm.medio.creditoIntervalos);
            var d = new Date();
            var statusCredits = 'PENDIENTE';
            var total = parseFloat((vm.bill.total / vm.medio.creditoNumeroPagos).toFixed(4));
            vm.seqNumberCredits = vm.seqNumber;
            vm.estado = statusCredits;
            for (var i = 1; i <= parseInt(vm.medio.creditoNumeroPagos); i++) {
                var credito = {
                    payNumber: i,
                    diasPlazo: diasPlazo,
                    fecha: _addDays(d, diasPlazo),
                    statusCredits: statusCredits,
                    documentNumber: vm.seqNumberCredits,
                    valor: total
                };
                diasPlazo += parseInt(vm.medio.creditoIntervalos);
                creditArray.push(credito);
            };
            vm.medio.credits = creditArray;
            vm.pagoTot = credito.total;
        };

        vm.getFechaCobro = function() {
            var d = new Date();
            vm.medio.fechaCobro = _addDays(d, parseInt(vm.medio.chequeDiasPlazo));
        };

        vm.addPago = function() {
            vm.pagos.push(angular.copy(vm.medio));
            vm.medio = {};
        };

        vm.removePago = function(index) {
            vm.pagos.splice(index, 1);
        };

        vm.getTotalPago = function() {
            vm.aux = 0;
            vm.varPago = 0;
            if (vm.bill) {
                vm.getCambio = 0;
                _.each(vm.pagos, function(med) {
                    vm.varPago = parseFloat(parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2);
                });
                vm.getCambio = (vm.varPago - vm.bill.total).toFixed(2);
                vm.aux = (vm.varPago - vm.getCambio).toFixed(2);
            };
            return vm.varPago;
        };

        vm.getTotalPagoB = function() {
            vm.aux = 0;
            vm.varPago = 0;
            if (vm.bill) {
                vm.getCambio = 0;
                _.each(vm.pagos, function(med) {
                    vm.varPago = parseFloat((parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2));
                });
                vm.getCambioB = parseFloat((vm.varPago - vm.bill.total).toFixed(2));
                vm.aux = parseFloat((vm.varPago - vm.getCambio).toFixed(2));
            };
            return vm.varPago;
        };

        vm.billSelect = function(bill) {
            vm.loading = true;
            billService.getById(bill.id).then(function(response) {
                vm.detailList = undefined;
                vm.billList = undefined;
                vm.bill = response;
                vm.companyData = $localStorage.user.subsidiary;
                vm.clientSelected = response.client;
                vm.pagos = response.pagos;
                var dateToShow = new Date(response.dateCreated);
                vm.seqNumber = response.stringSeq;
                $('#pickerBillDate').data("DateTimePicker").date(dateToShow);
                vm.newBill = false;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelBill = function() {
            _activate();
        };

        vm.getDateToPrint = function() {
            if (vm.bill != undefined) {
                return $('#pickerBillDate').data("DateTimePicker").date().toDate();
            };
        };

        vm.printToCartAndCancel = function(printBillId) {
            var innerContents = document.getElementById(printBillId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillId', 'width=400,height=500');
            popupWinindow.document.write('<html><head><title>printMatrixBillId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate();
        };

        vm.printToCart = function(printBillId) {
            var innerContents = document.getElementById(printBillId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillId', 'width=400,height=500');
            popupWinindow.document.write('<html><head><title>printMatrixBillId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
        };

        vm.downloadBillTxtTM20 = function(billToDownloadEpsonTM20) {
            var b = document.body.appendChild(document.createElement("b"));
            b.href = "data:text/plain; charset=utf-8," + document.getElementById(billToDownloadEpsonTM20).innerText;
            b.download = "billDownloaded.txt";
            b.click();
            document.body.removeChild(b);
        };

        vm.billDeactivate = function() {
            vm.loading = true;
            var index = vm.billList.indexOf(vm.cancelaBill);
            if (vm.cancelaBill.creditNoteApplied === false) {
                billService.cancelBill(vm.cancelaBill).then(function(response) {
                    var index = vm.billList.indexOf(vm.cancelaBill);
                    if (index > -1) {
                        vm.billList.splice(index, 1);
                    };
                    vm.cancelaBill = undefined;
                    vm.success = "Factura de Venta Anulada";
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

        vm.getQuotations = function() {
            vm.loading = true;
            billService.getQuotationsByClientId(vm.clientSelected.id).then(function(response) {
                vm.quotations = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.selectQuotation = function(quotation) {
            vm.loading = true;
            billService.getById(quotation.id).then(function(response) {
                vm.bill = response;
                vm.bill.id = undefined;
                vm.bill.claveDeAcceso = undefined;
                _getTotalSubtotal();
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getClaveAcceso = function() {
            vm.loading = true;
            $('#modalAddPago').modal('hide');
            vm.impuestosTotales = [];
            vm.bill.detailsKardex = [];
            if (vm.bill.observation === null || vm.bill.observation === undefined || vm.bill.observation === '') {
                vm.bill.observation = '--';
            };
            if (vm.impuestoIVA.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVA);
            };
            if (vm.impuestoIVAZero.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVAZero);
            };
            vm.bill.billSeq = vm.numberAddedOne;
            if (vm.bill.discountPercentage === undefined) {
                vm.bill.discountPercentage = 0;
            };
            _.each(vm.bill.details, function(det) {
                var costWithIva = parseFloat((det.total * 1.12).toFixed(4));
                var costWithIce = parseFloat((det.total * 1.10).toFixed(4));
                var impuestos = [];
                var impuesto = {};
                var detaAdic = {
                    "det": det.adicional
                };

                if (det.product.iva) {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.12).toFixed(4));
                    impuesto.tarifa = 12.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                } else {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.0000).toFixed(4));
                    impuesto.tarifa = 0.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '0';
                    impuestos.push(impuesto);
                };

                if (det.product.ice) {
                    impuesto.base_imponible = parseFloat((det.costEach).toFixed(4));
                    impuesto.valor = costWithIce;
                    impuesto.tarifa = 10.0;
                    impuesto.codigo = '3';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                };

                var item = {
                    "cantidad": det.quantity,
                    "codigo_principal": det.product.codeIntegridad,
                    "codigo_auxiliar": det.product.barCode,
                    "precio_unitario": parseFloat(det.costEach),
                    "descripcion": det.product.name,
                    "precio_total_sin_impuestos": parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4)),
                    "descuento": parseFloat(((det.quantity * det.costEach) * parseFloat((vm.bill.discountPercentage) / 100)).toFixed(4)),
                    "unidad_medida": det.product.unitOfMeasurementFull,
                    "detalles_adicionales": detaAdic
                };

                if (!_.isEmpty(impuestos)) {
                    item.impuestos = impuestos;
                };

                vm.items.push(item);

                var kardex = {
                    bill: vm.bill.id,
                    product: det.product,
                    codeWarehouse: '--',
                    dateRegister: $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime(),
                    details: 'FACTURA-VENTA Nro. ' + vm.seqNumber,
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

                vm.bill.detailsKardex.push(kardex);
            });

            var req = requirementService.createRequirement(vm.clientSelected, vm.bill, $localStorage.user, vm.impuestosTotales, vm.items, vm.pagos);

            billService.getClaveDeAcceso(req, vm.companyData.userClient.id).then(function(resp) {
                vm.bill.pagos = vm.pagos;
                if (vm.bill.discountPercentage === undefined) {
                    vm.bill.discountPercentage = 0;
                };
                var obj = JSON.parse(resp.data);
                //var obj = {clave_acceso: '1234560', id:'id12345'};
                if (obj.errors === undefined) {
                    vm.bill.claveDeAcceso = obj.clave_acceso;
                    vm.bill.idSri = obj.id;
                    vm.bill.stringSeq = vm.seqNumber;
                    vm.bill.priceType = vm.priceType.name;
                    if (vm.estado === 'PENDIENTE') {
                        vm.bill.saldo = (vm.bill.total).toString();
                    } else {
                        vm.bill.saldo = '0';
                    };
                    // 1 is typeDocument Bill **************!!!
                    billService.create(vm.bill, 1).then(function(respBill) {
                        vm.billed = true;
                        vm.newBill = false;
                        vm.newBuy = false;
                        $localStorage.user.cashier.billNumberSeq = vm.bill.billSeq;
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
                    vm.loading = false;
                    vm.error = "Error al obtener Clave de Acceso: " + JSON.stringify(obj.errors);
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
                dateConsumption: vm.dateBill.getTime(),
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                baseNoTaxes: 0,
                baseTaxes: 0,
                detailsConsumption: []
            };
        };
        
        vm.saveConsumption = function(consumption) {
            vm.loading = true;
            $('#modalAddPago').modal('hide');
            _getCsmSeqNumber();
            warehouseService.getAllWarehouseByUserClientId(vm.userClientId).then(function(response) {
                vm.warehouseList = response;
                var finalWarehouse = _.filter(vm.warehouseList, function(warehouse) { return warehouse.subsidiary.id === vm.subsidiaryId});
                vm.consumption.warehouse = finalWarehouse[0];
                vm.consumption.dateConsumption = $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime();
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
                        dateRegister: $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime(),
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
                    vm.newBuy = false;
                    vm.newConsumptionOn = false;
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
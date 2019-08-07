'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillOfflineCtrl
 * @description
 * # BillOfflineCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('BillOfflineCtrl', function(_, $location, utilStringService, $localStorage,
                                            clientService, productService, authService, billOfflineService, $window,
                                            cashierService, utilSeqService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.clientList = undefined;
        vm.isEmp = true;
        vm.pagoTot = undefined;
        
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

        vm.valParra = '2455e4bf-68f3-4071-b5c9-833d62512b00';
        vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';
        vm.mrZolutions = '4907601b-6e54-4675-80a8-ab6503e1dfeb';
                                            
        function _activate() {
            vm.error = undefined;
            vm.aux = undefined;
            vm.newBillOffline = undefined;
            vm.billedOffline = false;
            vm.clientSelected = undefined;
            vm.dateBillOffline = undefined;
            vm.seqNumber = undefined;
            vm.productList = undefined;
            vm.productToAdd = undefined;
            vm.pagoTot = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            vm.loading = true;
            vm.indexDetail = undefined;
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
            clientService.getLazyByUserClientId(vm.user.subsidiary.userClient.id).then(function(response) {
                vm.clientList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
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
                total: 0.0,
                subTotal: 0.0,
                iva: 0.0,
                ice: 0.0,
                detailsOffline: [],
                pagosOffline: []
            };
        };

        vm.clientSelect = function(client) {
            vm.companyData = $localStorage.user.subsidiary;
            vm.dateBillOffline = new Date();
            vm.clientSelected = client;
            vm.pagosOffline = [];
            _getSeqNumber();
            _initializeBillOffline();
            var today = new Date();
            $('#pickerBillOfflineDate').data("DateTimePicker").date(today);
            vm.newBillOffline = true;
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

        function _getTotalSubtotal() {
            vm.billOffline.subTotal = 0.0;
            vm.billOffline.iva = 0.0;
            vm.billOffline.ivaZero = 0.0;
            vm.billOffline.ice = 0.0;
            vm.billOffline.baseTaxes = 0.0;
            vm.billOffline.baseNoTaxes = 0.0;
            var discountWithIva = 0.0;
            var discountWithNoIva = 0.0;
            _.each(vm.billOffline.detailsOffline, function(detail) {
                vm.billOffline.subTotal = parseFloat((parseFloat(vm.billOffline.subTotal) + parseFloat(detail.total)).toFixed(2));
                var tot = parseFloat((detail.total).toFixed(2));
                if (vm.billOffline.discountPercentage) {
                    tot = parseFloat((detail.total).toFixed(2));
                };
                if (detail.product.iva) {
                    vm.billOffline.baseTaxes += parseFloat((detail.total).toFixed(2));
                    vm.billOffline.iva = 0.0;
                    if (vm.billOffline.discountPercentage) {
                        discountWithIva = parseFloat((parseFloat(discountWithIva) + (parseFloat(detail.total) * parseFloat((vm.billOffline.discountPercentage) / 100))).toFixed(2));
                    };
                } else {
                    vm.billOffline.baseNoTaxes += parseFloat((detail.total).toFixed(2));
                    vm.billOffline.ivaZero = 0.0;
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
            } else {
                vm.billOffline.discount = 0;
            };
            vm.impuestoICE.base_imponible = 0.0;
            vm.impuestoIVA.base_imponible = parseFloat((vm.billOffline.baseTaxes).toFixed(2));
            vm.impuestoIVAZero.base_imponible = parseFloat((vm.billOffline.baseNoTaxes).toFixed(2));
            vm.impuestoICE.valor = vm.billOffline.ice;
            vm.impuestoIVA.valor = vm.billOffline.iva;
            vm.impuestoIVAZero.valor = 0.0;
            vm.billOffline.total = parseFloat((parseFloat(vm.billOffline.baseTaxes)
                +  parseFloat(vm.billOffline.baseNoTaxes)).toFixed(2));
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
                    vm.seqErrorNumber = "NUMERO DE FACTURA YA EXISTENTE"
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.reCalculateTotal = function() {
            if (vm.billOffline.discountPercentage == null || vm.billOffline.discountPercentage == undefined) {
                vm.billOffline.discountPercentage = 0;
            };
            _.map(vm.billOffline.detailsOffline, function(detail) {
                if (vm.billOffline.discountPercentage) {
                    detail.discount = vm.billOffline.discountPercentage;
                } else {
                    detail.discount = 0;
              };
              var costEachCalculated = vm.getCost(detail.product[vm.priceType.cod], detail.product.averageCost);
              detail.costEach = costEachCalculated;
              detail.total = parseFloat((parseFloat(detail.quantity) * (parseFloat(detail.costEach) - (parseFloat(detail.costEach) * parseFloat(detail.discount / 100)))).toFixed(2));
            });
            _getTotalSubtotal();
        };

        function _filterProduct() {
            vm.totalPages = 0;
            var variable = vm.searchText? vm.searchText : null;
            productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, variable).then(function(response) {
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
            _filterProduct();
        };
    
        vm.selectProductToAdd = function(productSelect) {
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            };
            vm.productToAdd = angular.copy(productSelect);
            var costEachCalculated = vm.getCost(productSelect[vm.priceType.cod], productSelect.averageCost);
            vm.productToAdd.costEachCalculated = costEachCalculated;
            vm.quantity = 1;
        };
    
        vm.acceptProduct = function(closeModal) {
            vm.errorQuantity = undefined;
            if (vm.billOffline.discountPercentage == null || vm.billOffline.discountPercentage == undefined) {
                vm.billOffline.discountPercentage = 0;
            };
            var detail = {
                discount: vm.billOffline.discountPercentage? vm.billOffline.discountPercentage : 0,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: parseFloat(((vm.quantity * vm.productToAdd.costEachCalculated) - (vm.quantity * (vm.productToAdd.costEachCalculated) * (vm.billOffline.discountPercentage / 100))).toFixed(2)),
                adicional: vm.adicional
            };
            if (vm.indexDetail !== undefined) {
                vm.billOffline.detailsOffline[vm.indexDetail] = detail;
            } else {
                vm.billOffline.detailsOffline.push(detail);
            };
            vm.productToAdd = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            _getTotalSubtotal();
            if (closeModal) {
                $('#modalAddProduct').modal('hide');
            } else {
                var newProductList = _.filter(vm.productList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
        };

        vm.editDetail = function(detail, index) {
            vm.indexDetail = index;
            vm.productToAdd = detail.product;
            vm.quantity = detail.quantity;
            vm.adicional = detail.adicional;
            _getTotalSubtotal();
        };
    
        vm.removeDetail = function(index) {
            vm.billOffline.detailsOffline.splice(index,1);
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
            if (vm.medio.medio === 'efectivo' || vm.medio.medio === 'transferencia') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.billOffline.total - payed).toFixed(2));
            };
            if (vm.medio.medio === 'cheque') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.billOffline.total - payed).toFixed(2));
            };
            if (vm.medio.medio === 'tarjeta_credito' || vm.medio.medio === 'tarjeta_debito') {
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

        vm.printToCartAndCancel = function(printBillOffline) {
            var innerContents = document.getElementById(printBillOffline).innerHTML;
            
            var popupWinindow = window.open('', 'printMatrixBillOffline', 'width=300,height=400');
            popupWinindow.document.write('<html><head><title></title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate();
        };

        vm.printToCart = function(printBillOffline) {
            var innerContents = document.getElementById(printBillOffline).innerHTML;
            
            var popupWinindow = window.open('', 'printMatrixBillOffline', 'width=300,height=400');
            popupWinindow.document.write('<html><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
        };

        vm.printToCartAndCancelParra = function(printBillOffline) {
            var innerContents = document.getElementById(printBillOffline).innerHTML;
            var texto = innerContents;
            var popupWinindow = window.open('', 'printMatrixBillOffline', 'width=300,height=400');
            popupWinindow.document.write('<html><head><title></title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(texto);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate();
        };

        vm.printToCartParra = function(printBillOffline) {
            var innerContents = document.getElementById(printBillOffline).innerHTML;
            var texto = innerContents;
            var popupWinindow = window.open('', 'printMatrixBillOffline', 'width=300,height=400');
            popupWinindow.document.write('<html><body>');
            popupWinindow.document.write(texto);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
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
            var index = vm.billOfflineList.indexOf(vm.cancelBillOffline);
            billOfflineService.deactivateBillOffline(vm.cancelBillOffline).then(function(response) {
              var index = vm.billOfflineList.indexOf(vm.cancelBillOffline);
              if (index > -1) {
                  vm.billOfflineList.splice(index, 1);
              };
              vm.cancelBillOffline = undefined
              vm.loading = false;
            }).catch(function(error) {
              vm.loading = false;
              vm.error = error.data;
            });
          };

        vm.saveBillOffline = function() {
            vm.loading = true;
            $('#modalAddPago').modal('hide');
            vm.impuestosTotales = [];
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
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.billOffline.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.12).toFixed(2));
                    impuesto.tarifa = 12.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                } else {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.billOffline.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.0000).toFixed(2));
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
                
                if (vm.billOffline.discountPercentage === undefined) {
                    vm.billOffline.discountPercentage = 0;
                };
            });

            var obj = {clave_acceso: '1124225675', id:'id12345'};
            vm.billOffline.pagosOffline = vm.pagosOffline;
            vm.billOffline.claveDeAcceso = obj.clave_acceso;
            vm.billOffline.idSri = obj.id;
            vm.billOffline.billSeq = vm.numberAddedOne;
            vm.billOffline.stringSeq = vm.seqNumber;
            vm.billOffline.priceType = vm.priceType.name;
            if (vm.userClientId === vm.valParra) {
                vm.billOffline.iva = parseFloat((parseFloat(vm.billOffline.subTotal) * 0.12).toFixed(2));
                vm.billOffline.total = parseFloat((parseFloat(vm.billOffline.subTotal) + parseFloat(vm.billOffline.iva)).toFixed(2));
            };

            billOfflineService.createBillOffline(vm.billOffline, 1).then(function(respBill) {
                vm.billedOffline = true;
                vm.billOfflineCreated = respBill;
                $localStorage.user.cashier.billOfflineNumberSeq = vm.billOffline.billSeq;
                if (vm.seqChanged) {
                    cashierService.update($localStorage.user.cashier).then(function(resp) {
                    
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
        };
    
        vm.exit = function() {
            $location.path('/home');
        };
                                              
        (function initController() {
            _activate();
        })();
});
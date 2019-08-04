'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # BillCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('BillCtrl', function(_, $rootScope, $location, utilStringService, $localStorage,
                                     clientService, productService, authService, billService, $window,
                                     cashierService, requirementService, utilSeqService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.clientList = undefined;
        vm.isEmp = true;
        vm.pagoTot = undefined;
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
            vm.error = undefined;
            vm.aux = undefined;
            vm.estado = undefined;
            vm.newBill = true;
            vm.billed = false;
            vm.clientSelected = undefined;
            vm.dateBill = undefined;
            vm.seqNumber = undefined;
            vm.productList = undefined;
            vm.productToAdd = undefined;
            vm.pagoTot = undefined;
            vm.quantity = undefined;
            vm.userClientId = undefined;
            vm.adicional = undefined;
            vm.loading = true;
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
            vm.userClientId = $localStorage.user.subsidiary.userClient.id;
            vm.userCashier = $localStorage.user.cashier;
            vm.userSubsidiary = $localStorage.user.subsidiary;
            clientService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                vm.clientList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
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
                total: 0.0,
                subTotal: 0.0,
                iva: 0.0,
                ice: 0.0,
                details: [],
                pagos: []
            };
        };

        vm.clientSelect = function(client) {
            vm.quotations = [];
            vm.companyData = vm.userSubsidiary;
            vm.dateBill = new Date();
            vm.clientSelected = client;
            vm.pagos = [];
            _getSeqNumber();
            _initializeBill();
            var today = new Date();
            $('#pickerBillDate').data("DateTimePicker").date(today);
            vm.newBill = true;
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

        function _getTotalSubtotal() {
            vm.bill.subTotal = 0.0;
            vm.bill.iva = 0.0;
            vm.bill.ivaZero = 0.0;
            vm.bill.ice = 0.0;
            vm.bill.baseTaxes = 0.0;
            vm.bill.baseNoTaxes = 0.0;
            var discountWithIva = 0.0;
            var discountWithNoIva = 0.0;
            _.each(vm.bill.details, function(detail) {
                vm.bill.subTotal = parseFloat((parseFloat(vm.bill.subTotal) + parseFloat(detail.total)).toFixed(4));
                var tot = parseFloat((detail.total).toFixed(4));
                if (vm.bill.discountPercentage) {
                    tot = parseFloat((detail.total).toFixed(4));
                };
                if (detail.product.iva) {
                    vm.bill.baseTaxes += parseFloat((detail.total).toFixed(4));
                    vm.bill.iva = parseFloat((parseFloat(vm.bill.iva) + (parseFloat(tot) * 0.12)).toFixed(4));
                    if (vm.bill.discountPercentage) {
                        discountWithIva = parseFloat((parseFloat(discountWithIva) + (parseFloat(detail.total) * parseFloat((vm.bill.discountPercentage) / 100))).toFixed(4));
                    };
                } else {
                    vm.bill.baseNoTaxes += parseFloat((detail.total).toFixed(4));
                    vm.bill.ivaZero = parseFloat((parseFloat(vm.bill.ivaZero) + parseFloat(tot)).toFixed(4));
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
            productService.getLazyBySusidiaryIdForBill($localStorage.user.subsidiary.id, vm.page, variable).then(function(response) {
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
            if (vm.bill.discountPercentage == null || vm.bill.discountPercentage == undefined) {
                vm.bill.discountPercentage = 0;
            };
            var detail = {
                discount: vm.bill.discountPercentage? vm.bill.discountPercentage : 0,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: parseFloat(((vm.quantity * vm.productToAdd.costEachCalculated) - (vm.quantity * (vm.productToAdd.costEachCalculated) * (vm.bill.discountPercentage / 100))).toFixed(4)),
                adicional: vm.adicional
            };
            if (vm.indexDetail !== undefined) {
                vm.bill.details[vm.indexDetail] = detail;
            } else {
                vm.bill.details.push(detail);
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
            _getTotalSubtotal();
        };

        vm.removeDetail = function(index) {
            vm.bill.details.splice(index,1);
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
            var index = vm.billList.indexOf(vm.cancelBill);
            billService.cancelBill(vm.cancelBill).then(function(response) {
                var index = vm.billList.indexOf(vm.cancelBill);
                if (index > -1) {
                    vm.billList.splice(index, 1);
                };
                vm.cancelBill = undefined
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
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
                    dateRegister: $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime(),
                    details: 'VENTA FACTURA Nro. ' + vm.seqNumber,
                    observation: 'EGRESO',
                    prodCostEach: det.costEach,
                    prodName: det.product.name,
                    prodQuantity: det.quantity,
                    prodTotal: det.total
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

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
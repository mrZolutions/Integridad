'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CreditNoteCtrl
 * @description
 * # CreditNoteCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('CreditNoteCtrl', function( _, $rootScope, $location, providerService, $localStorage,
                                            clientService, billService, creditNoteService, utilSeqService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.idBill = undefined;

        vm.typeCreditNote = [
            {code: '1', type: 'NOTA DE CRÉDITO - FACTURAS DE VENTAS'},
            {code: '2', type: 'NOTA DE CRÉDITO - FACTURAS DE COMPRAS'},
            {code: '3', type: 'NOTA DE CRÉDITO - FACTURAS OFFLINE'}
        ];

        function _activate() {
            vm.user = $localStorage.user;
            vm.userClientId = $localStorage.user.subsidiary.userClient.id;
            vm.userCashier = $localStorage.user.cashier;
            vm.userSubsidiary = $localStorage.user.subsidiary;

            vm.clientList = undefined;
            vm.clientSelected = undefined;
            vm.billList = undefined;
            vm.bill = undefined;
            vm.claveDeAcceso = undefined;
            vm.error = undefined;
            vm.success = undefined;
            vm.creditNoteType = undefined;
            vm.selectedTypeCreditNote = undefined;
            vm.providerList = undefined;
            vm.providerSelected = undefined;
            vm.cellar = undefined;
            vm.billOffline = undefined;

            vm.impuestoIVA = {
                "base_imponible": 0,
                "valor": 0,
                "codigo": "2",
                "codigo_porcentaje": "2"
            };

            vm.impuestoIVAZero = {
                "base_imponible": 0,
                "valor": 0,
                "codigo": "2",
                "codigo_porcentaje": "0"
            };
        };

        vm.loadTypeCreditNote = function(creditNoteType) {
            vm.creditNoteType = creditNoteType;
            switch (vm.creditNoteType) {
                case '1':
                    vm.selectedTypeCreditNote = vm.creditNoteType;
                    clientService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                        vm.clientList = response;
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                break;
                case '2':
                    vm.selectedTypeCreditNote = vm.creditNoteType;
                    providerService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                        vm.providerList = response;
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                break;
                case '3':
                    vm.selectedTypeCreditNote = vm.creditNoteType;
                    clientService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                        vm.clientList = response;
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                break;
            };
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt(vm.userCashier.creditNoteNumberSeq) + 1;
            vm.seqNumberFirstPart = vm.userSubsidiary.threeCode + '-' + vm.userCashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
        };

        function _getTotales() {
            vm.bill.subTotal = 0;
            vm.bill.iva = 0;
            vm.bill.ivaZero = 0;
            vm.bill.ice = 0;
            vm.bill.baseTaxes = 0;
            vm.bill.baseNoTaxes = 0;
            var subtotal = 0;
            var descuento = 0;
            var baseZero = 0;
            var baseDoce = 0;
            var iva = 0;
            var total = 0;
            _.each(vm.bill.details, function(detail) {
                subtotal = subtotal + (detail.quantity * detail.costEach);
                if (detail.product.iva) {
                    baseDoce = baseDoce + (detail.quantity * detail.costEach);
                } else {
                    baseZero = baseZero + (detail.quantity * detail.costEach);
                };
            });

            descuento = parseFloat(((vm.bill.discount * subtotal)/100).toFixed(4));
            baseDoce = baseDoce - descuento;
            iva = baseDoce * 0.12;

            total = baseDoce + baseZero + iva;

            vm.bill.subTotal = subtotal;
            vm.bill.discount = descuento;
            vm.bill.baseNoTaxes = baseZero;
            vm.bill.baseTaxes = baseDoce;
            vm.bill.iva = iva;
            vm.bill.total = total;

            vm.impuestoICE.base_imponible = vm.bill.subTotal
            vm.impuestoIVA.base_imponible = vm.bill.baseTaxes
            vm.impuestoIVAZero.base_imponible = vm.bill.baseNoTaxes
            vm.impuestoICE.valor = vm.bill.ice;
            vm.impuestoIVA.valor = vm.bill.iva
            vm.impuestoIVAZero.valor = 0.0;
        };

        vm.clientSelect = function(client) {
            vm.error = undefined;
            vm.success = undefined;
            vm.companyData = $localStorage.user.subsidiary;
            vm.clientSelected = client;
            billService.getAllBillsByClientId(vm.clientSelected.id).then(function(response) {
                vm.billList = response;
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.billSelect = function(billSelected) {
            vm.items = [];
            billService.getById(billSelected.id).then(function(response) {
                vm.bill = angular.copy(response);
                vm.bill.details = [];
                vm.idBill = billSelected.id;
                vm.bill.client = vm.clientSelected;
                vm.bill.userIntegridad= $localStorage.user;
                vm.bill.subsidiary= $localStorage.user.subsidiary;
                _.each(response.details, function(detail) {
                    detail.id = undefined;
                    vm.bill.details.push(detail);
                });
                vm.bill.documentStringSeq = response.stringSeq;
                _getSeqNumber();
                var today = new Date();
                $('#pickerCreditNoteDate').data("DateTimePicker").date(today);
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.editDetail = function(index) {
            vm.indexDetail = index;
            vm.detail = angular.copy(vm.bill.details[index]);
        };

        vm.editNewDetail = function() {
            vm.detail.total = parseFloat((parseFloat(vm.detail.quantity) * parseFloat(vm.detail.costEach)).toFixed(4));
            vm.bill.details[vm.indexDetail] = angular.copy(vm.detail);
            vm.indexDetail = undefined;
            vm.detail = undefined;
            _getTotales();
        };

        vm.cancelCreditNote = function() {
            _activate();
        };

        vm.saveCreditNote = function() {
            vm.loading = true;
            vm.impuestoIVA = {
                "base_imponible": vm.bill.baseTaxes,
                "valor": vm.bill.iva,
                "codigo": "2",
                "codigo_porcentaje": "2"
            };
            vm.impuestoIVAZero = {
                "base_imponible": vm.bill.baseNoTaxes,
                "valor": 0,
                "codigo": "2",
                "codigo_porcentaje": "0"
            };
            vm.impuestosTotales = [];
            if (vm.bill.baseTaxes > 0) {
                vm.impuestosTotales.push(vm.impuestoIVA);
            };
            if (vm.bill.baseNoTaxes > 0) {
                vm.impuestosTotales.push(vm.impuestoIVAZero);
            };
            vm.bill.creditSeq = vm.numberAddedOne;
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
                    "cantidad":det.quantity,
                    "codigo_principal": det.product.codeIntegridad,
                    "codigo_auxiliar": det.product.barCode,
                    "precio_unitario": det.costEach,
                    "descripcion": det.product.name,
                    "precio_total_sin_impuestos": parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4)),
                    "descuento": parseFloat(((det.quantity * det.costEach) * parseFloat((vm.bill.discountPercentage) / 100)).toFixed(4)),
                    "detalles_adicionales": detaAdic
                };

                if (!_.isEmpty(impuestos)) {
                    item.impuestos = impuestos;
                };
                vm.items.push(item);
            });

            var req = creditNoteService.createRequirement(vm.clientSelected, vm.bill, $localStorage.user, vm.impuestosTotales, vm.items);
            
            creditNoteService.getClaveDeAcceso(req, vm.companyData.userClient.id).then(function(resp) {
                //var obj = JSON.parse(resp.data);
                var obj = {clave_acceso: '1234560', id:'id12345'};
                if (obj.errors === undefined) {
                    vm.claveDeAcceso = obj.clave_acceso;
                    vm.bill.claveDeAcceso = obj.clave_acceso;
                    vm.bill.idSri = obj.id;
                    vm.bill.stringSeq = vm.seqNumber;
                    vm.bill.billSeq = vm.idBill;
                    creditNoteService.create(vm.bill).then(function(respBill) {
                        vm.billed = true;
                        vm.userCashier.creditNoteNumberSeq = vm.bill.creditSeq;
                        vm.success = 'Nota de Crédito creada';
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.loading = false;
                    vm.error = "Error al obtener Clave de Acceso: " + JSON.stringify(obj.errors);
                };
            });
        };

        vm.removeDetail = function(index) {
            vm.bill.details.splice(index,1);
            _getTotales();
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
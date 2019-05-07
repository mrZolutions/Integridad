'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CreditNoteCtrl
 * @description
 * # CreditNoteCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('CreditNoteCtrl', function( _, $rootScope, $location, utilStringService, $localStorage,
                                            clientService, billService, creditService, utilSeqService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.clientList = undefined;
        vm.idBill = undefined;

        function _activate() {
            vm.clientSelected = undefined;
            vm.billList = undefined;
            vm.bill = undefined;
            vm.claveDeAcceso = undefined;
            vm.error = undefined;
            vm.success = undefined;

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

            vm.user = $localStorage.user;
            clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function(response) {
                vm.clientList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt($localStorage.user.cashier.creditNoteNumberSeq) + 1;
            vm.seqNumberFirstPart = $localStorage.user.subsidiary.threeCode + '-' + $localStorage.user.cashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
        };

        function _getTotales() {
            vm.bill.subTotal = 0.0;
            vm.bill.iva = 0.0;
            vm.bill.ivaZero = 0.0;
            vm.bill.ice = 0.0;
            vm.bill.baseTaxes = 0.0;
            vm.bill.baseNoTaxes = 0.0;
            var subtotal = 0.0;
            var descuento = 0.0;
            var baseZero = 0.0;
            var baseDoce = 0.0;
            var iva = 0.0;
            var total = 0.0;
            _.each(vm.bill.details, function(detail) {
                subtotal = parseFloat((subtotal + (detail.quantity * detail.costEach)).toFixed(4));
                if (detail.product.iva) {
                    baseDoce = parseFloat((baseDoce + (detail.quantity * detail.costEach)).toFixed(4));
                } else {
                    baseZero = parseFloat((baseZero + (detail.quantity * detail.costEach)).toFixed(4));
                };
            });

            descuento = parseFloat(((vm.bill.discount * subtotal)/100).toFixed(4));
            baseDoce = baseDoce - descuento;
            iva = baseDoce * parseFloat(0.12);

            total = parseFloat((baseDoce + baseZero + iva).toFixed(4));

            vm.bill.subTotal = parseFloat((subtotal).toFixed(4));
            vm.bill.discount = parseFloat((descuento).toFixed(4));
            vm.bill.baseNoTaxes = parseFloat((baseZero).toFixed(4));
            vm.bill.baseTaxes = parseFloat((baseDoce).toFixed(4));
            vm.bill.iva = parseFloat((iva).toFixed(4));
            vm.bill.total = parseFloat((total).toFixed(4));

            vm.impuestoICE.base_imponible = parseFloat((vm.bill.subTotal).toFixed(4));
            vm.impuestoIVA.base_imponible = parseFloat((vm.bill.baseTaxes).toFixed(4));
            vm.impuestoIVAZero.base_imponible = parseFloat((vm.bill.baseNoTaxes).toFixed(4));
            vm.impuestoICE.valor = vm.bill.ice;
            vm.impuestoIVA.valor = vm.bill.iva;
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
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.1200).toFixed(4));
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

            var req = creditService.createRequirement(vm.clientSelected, vm.bill, $localStorage.user, vm.impuestosTotales, vm.items);
            
            creditService.getClaveDeAcceso(req, vm.companyData.userClient.id).then(function(resp) {
                var obj = JSON.parse(resp.data);
                //var obj = {clave_acceso: '1234560', id:'id12345'};
                if (obj.errors === undefined) {
                    vm.claveDeAcceso = obj.clave_acceso;
                    vm.bill.claveDeAcceso = obj.clave_acceso;
                    vm.bill.idSri = obj.id;
                    vm.bill.stringSeq = vm.seqNumber;
                    vm.bill.billSeq = vm.idBill;
                    creditService.create(vm.bill).then(function(respBill) {
                        vm.billed = true;
                        $localStorage.user.cashier.creditNoteNumberSeq = vm.bill.creditSeq;
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
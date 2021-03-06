'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:QuotationCtrl
 * @description
 * # QuotationCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('QuotationCtrl', function( _,$location, holderService, clientService, productService, billService,
                                          utilSeqService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;

        vm.loading = false;
        vm.clientList = undefined;
        vm.isEmp = true;
        vm.prices = [
            { name: 'EFECTIVO', cod: 'cashPercentage'}, { name: 'MAYORISTA', cod: 'majorPercentage'},
            { name: 'CREDITO', cod: 'creditPercentage'}, { name: 'TARJETA', cod: 'cardPercentage'}
        ];
        vm.seqChanged = false;

        function _activate() {
            vm.billed = false;
            vm.clientSelected = JSON.parse(localStorage.getItem("client"));
            vm.user = holderService.get();
            vm.companyData = vm.user.subsidiary;
            vm.dateBill = new Date();
            vm.seqNumber = undefined;
            vm.productList = undefined;
            vm.productToAdd = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            vm.loading = true;
            vm.indexDetail = undefined;
            vm.priceType = vm.prices[0];
            vm.seqChanged = false;

            vm.impuestosTotales = [];
            vm.items = [];
            vm.impuestoICE = {
                "base_imponible":0.0,
                "valor":0.0,
                "codigo":"3",
                "codigo_porcentaje":2
            };
            vm.impuestoIVA = {
                "base_imponible":0.0,
                "valor":0.0,
                "codigo":"2",
                "codigo_porcentaje":2
            };
            vm.impuestoIVAZero = {
                "base_imponible":0.0,
                "valor":0.0,
                "codigo":"0",
                "codigo_porcentaje":0
            };
            vm.medio={};
            clientService.getLazyByUserClientId(vm.user.subsidiary.userClient.id).then(function(response) {
                vm.clientList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
            _initializeBill();
            var today = new Date();
            $('#pickerBillDate').data("DateTimePicker").date(today);
            vm.newBill = true;
            _getSeqNumber();
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt(vm.user.cashier.quotationNumberSeq) + 1;
            vm.seqNumberFirstPart = vm.user.subsidiary.threeCode + '-'
              + vm.user.cashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-'
              + vm.seqNumberSecondPart;
        };

        function _initializeBill() {
            vm.bill = {
                client: vm.clientSelected,
                userIntegridad: vm.user,
                subsidiary: vm.user.subsidiary,
                dateCreated: vm.dateBill.getTime(),
                discount: 0,
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                details: [],
                pagos:[]
            };
        };

        function _getTotalSubtotal() {
            vm.bill.subTotal = 0;
            vm.bill.iva = 0;
            vm.bill.ivaZero = 0;
            vm.bill.ice = 0;
            vm.bill.baseTaxes = 0;
            vm.bill.baseNoTaxes = 0;
            var discountWithIva = 0;
            var discountWithNoIva = 0;
            _.each(vm.bill.details, function(detail) {
              vm.bill.subTotal = (parseFloat(vm.bill.subTotal) + parseFloat(detail.total)).toFixed(2);
              var tot = detail.total;
              if (vm.bill.discountPercentage) {
                tot = (parseFloat(detail.total) - (parseInt(vm.bill.discountPercentage)/100) * (parseFloat(detail.total))).toFixed(2);
              };
              if (detail.product.iva) {
                vm.bill.baseTaxes += parseFloat(detail.total);
                vm.bill.iva = (parseFloat(vm.bill.iva) + (parseFloat(tot) * 0.12)).toFixed(2);
                if (vm.bill.discountPercentage) {
                  discountWithIva = (parseFloat(discountWithIva) + ((parseInt(vm.bill.discountPercentage)/100) * detail.total)).toFixed(2);
                };
              } else {
                vm.bill.baseNoTaxes += parseFloat(detail.total);
                vm.bill.ivaZero = (parseFloat(vm.bill.ivaZero) + parseFloat(tot)).toFixed(2);
                if (vm.bill.discountPercentage) {
                  discountWithNoIva = (parseFloat(discountWithNoIva) + ((parseInt(vm.bill.discountPercentage)/100) * detail.total)).toFixed(2);
                };
              };
              if (detail.product.ice) {
                vm.bill.ice = (parseFloat(vm.bill.ice) + (parseFloat(tot) * 0.10)).toFixed(2);
              };
            });
            if (vm.bill.discountPercentage) {
              vm.bill.discount = ((parseInt(vm.bill.discountPercentage)/100) * vm.bill.subTotal).toFixed(2);
            } else {
              vm.bill.discount = 0;
            };
            vm.impuestoICE.base_imponible = vm.bill.subTotal;
            vm.impuestoIVA.base_imponible = vm.bill.baseTaxes;
            vm.impuestoIVAZero.base_imponible = vm.bill.baseNoTaxes;
            vm.impuestoICE.valor = vm.bill.ice;
            vm.impuestoIVA.valor = vm.bill.iva;
            vm.impuestoIVAZero.valor = vm.bill.ivaZero;
            vm.bill.baseTaxes = (vm.bill.baseTaxes - discountWithIva).toFixed(2);
            vm.bill.baseNoTaxes = (vm.bill.baseNoTaxes - discountWithNoIva).toFixed(2);
            vm.bill.total = (parseFloat(vm.bill.baseTaxes)
              +  parseFloat(vm.bill.baseNoTaxes)
              +  parseFloat(vm.bill.iva)
              +  parseFloat(vm.bill.ice)
            ).toFixed(2);
        };

        function _addDays(date, days) {
            var result = new Date(date);
            result.setDate(result.getDate() + days);
            return result.getTime();
        };

        vm.reCalculateTotal = function() {
            _.map(vm.bill.details, function(detail) {
                if (vm.bill.discountPercentage) {
                    detail.discount = vm.bill.discountPercentage;
                } else {
                    detail.discount = 0;
                }
                var costEachCalculated = vm.getCost(detail.product[vm.priceType.cod], detail.product.averageCost);
                detail.costEach = costEachCalculated;
                detail.total = (parseFloat(detail.quantity) * parseFloat(detail.costEach)).toFixed(2);
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
            productService.getLazyBySusidiaryId(vm.user.subsidiary.id, vm.page, busqueda, undefined).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.bill.details, function(detail) {
                      return detail.product.id === response.content[i].id;
                    });
                    if (productFound === undefined) {
                        var sub = _.find(response.content[i].productBySubsidiaries, function(s) {
                            return (s.subsidiary.id === vm.user.subsidiary.id && s.active === true);
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
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            }
            vm.productToAdd = angular.copy(productSelect);
            var costEachCalculated = vm.getCost(productSelect[vm.priceType.cod], productSelect.averageCost);
            vm.productToAdd.costEachCalculated = costEachCalculated;
            vm.quantity = 1;
        };

        vm.acceptProduct = function(closeModal) {
            vm.errorQuantity = undefined;
            var detail = {
                discount: vm.bill.discountPercentage? vm.bill.discountPercentage : 0,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: (parseFloat(vm.quantity) * parseFloat(vm.productToAdd.costEachCalculated)).toFixed(2),
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
                var newProductList = _.filter(vm.productList, function (prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
        };

        vm.getCost = function(cashPercen, averageCost) {
            var aC = 1 + ((cashPercen) / 100);
            var cost = aC * averageCost;
            return (cost).toFixed(4);
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

        vm.getFechaCobro = function() {
            var d = new Date();
            vm.medio.fechaCobro = _addDays(d, parseInt(vm.medio.chequeDiasPlazo));
        };

        vm.getTotalPago = function() {
            vm.varPago=0;
            if (vm.bill) {
                vm.getCambio = 0;
                _.each(vm.pagos, function(med) {
                    vm.varPago=parseFloat(parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2);
                });
                vm.getCambio = (vm.varPago - vm.bill.total).toFixed(2);
            };
            return vm.varPago;
        };

        vm.cancelBill = function() {
            $location.path('/clients');
        };

        vm.saveQuotation = function() {
            vm.loading = true;
            vm.impuestosTotales = [];
            if (vm.impuestoIVA.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVA);
            };
            if (vm.impuestoIVAZero.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVAZero);
            };
            vm.bill.quotationSeq = vm.numberAddedOne;
            if (vm.bill.discountPercentage === undefined) {
                vm.bill.discountPercentage = 0;
            };
            _.each(vm.bill.details, function(det) {
                var costWithIva = parseFloat((det.total * 1.12).toFixed(4));
                var costWithIce = parseFloat((det.total * 1.10).toFixed(4));
                var impuestos = [];
                var impuesto = {};
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
                    impuesto.base_imponible = det.costEach;
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
                    "precio_unitario": det.costEach,
                    "descripcion": det.product.name,
                    "precio_total_sin_impuestos": ((parseFloat(det.costEach) - (parseFloat(det.costEach) * (parseInt(vm.bill.discountPercentage)/100))) * parseFloat(det.quantity)).toFixed(2),
                    "descuento": (parseFloat(det.costEach) * (parseInt(vm.bill.discountPercentage)/100)).toFixed(2),
                    "unidad_medida": det.product.unitOfMeasurementFull
                };
                if (!_.isEmpty(impuestos)) {
                    item.impuestos = impuestos;
                }
                vm.items.push(item);
            });
            vm.bill.idSri;
            vm.bill.stringQuotationSeq = vm.seqNumber;
            vm.bill.priceType = vm.priceType.name;
            // 0 is typeDocument Bill **************!!!
            billService.create(vm.bill, 0).then(function(respBill) {
                vm.billed = true;
                vm.user.cashier.quotationNumberSeq = vm.bill.quotationSeq;
                holderService.set(vm.user)
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        (function initController() {
            _activate();
        })();
});
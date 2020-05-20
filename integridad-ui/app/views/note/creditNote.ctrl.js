'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:CreditNoteCtrl
 * @description
 * # CreditNoteCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('CreditNoteCtrl', function(_, $location, providerService, holderService, debtsToPayService, cellarService, productService,
                                           clientService, billService, creditNoteService, utilSeqService, creditNoteCellarService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.idBill = undefined;
        vm.creditNoteOption = undefined;

        vm.typeCreditNote = [
            {code: '1', type: 'NOTA DE CRÉDITO - FACTURAS DE VENTAS'},
            {code: '2', type: 'NOTA DE CRÉDITO - FACTURAS DE COMPRAS'},
            {code: '3', type: 'NOTA DE CRÉDITO - FACTURAS OFFLINE'}
        ];

        function _activate() {
            vm.user = holderService.get();
            vm.userClientId = vm.user.subsidiary.userClient.id;
            vm.userCashier = vm.user.cashier;
            vm.userSubsidiary = vm.user.subsidiary;
            vm.subOnlineActive = vm.user.subsidiary.online;
            vm.subCxPActive = vm.user.subsidiary.cxp;
            vm.newCNCellar = undefined;

            vm.clientList = undefined;
            vm.clientSelected = undefined;
            vm.clientName = undefined;
            vm.creditNote = undefined;
            vm.creditNoteList = undefined;
            vm.billList = undefined;
            vm.bill = undefined;
            vm.claveDeAcceso = undefined;
            vm.error = undefined;
            vm.success = undefined;
            vm.creditNoteType = undefined;
            vm.selectedTypeCreditNote = undefined;
            vm.providerList = undefined;
            vm.providerSelected = undefined;
            vm.providerName = undefined;
            vm.cellar = undefined;
            vm.cellarList = undefined;
            vm.billOffline = undefined;

            switch(true) {
                case $location.path().includes('/note/credit'): 
                    vm.creditNoteOption = 'PURCH';
                    vm.loadTypeCreditNote('2');
                    break;
                case $location.path().includes('/creditNoteSale'): 
                    vm.creditNoteOption = 'SALE';
                    vm.loadTypeCreditNote('1');
                    break;
                case $location.path().includes('/creditNoteManual'): 
                    vm.creditNoteOption = 'SALEM';
                    vm.loadTypeCreditNote('3');
                    break;
            }

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

// Sección Nota de Cŕedito de Facturas de Venta

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

            vm.impuestoIVA.base_imponible = vm.bill.baseTaxes;
            vm.impuestoIVAZero.base_imponible = vm.bill.baseNoTaxes;
            vm.impuestoIVA.valor = vm.bill.iva;
            vm.impuestoIVAZero.valor = 0.0;
        };

        vm.clientSelect = function(client) {
            vm.error = undefined;
            vm.success = undefined;
            vm.companyData = vm.user.subsidiary;
            vm.clientSelected = client;
            billService.getAllBillsByClientIdAndNoCN(vm.clientSelected.id).then(function(response) {
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
                vm.bill.userIntegridad= vm.user;
                vm.bill.subsidiary= vm.user.subsidiary;
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

        vm.removeDetail = function(index) {
            vm.bill.details.splice(index,1);
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

            var req = creditNoteService.createRequirement(vm.clientSelected, vm.bill, vm.user, vm.impuestosTotales, vm.items);
            
            creditNoteService.getClaveDeAcceso(req, vm.companyData.userClient.id).then(function(resp) {
                var obj = JSON.parse(resp.data);
                //var obj = {clave_acceso: '1234560', id:'id12345'};
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

        vm.creditsNoteByClient = function(client) {
            vm.error = undefined;
            vm.success = undefined;
            vm.companyData = vm.user.subsidiary;
            vm.clientName = client.name;
            creditNoteService.getCreditsNoteByClientId(client.id).then(function(response) {
                vm.creditNoteList = response;
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.creditNoteSelect = function(creditNote) {
            vm.loading = true;
            vm.error = undefined;
            vm.success = undefined;
            creditNoteService.getCreditNoteById(creditNote.id).then(function(response) {
                vm.creditNote = response;
                vm.loading = false;
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

// Sección Nota de Crédito de Facturas de Compra

        function _getSeqNumbCredNotCellar() {
            vm.numberAddedO = parseInt(vm.userCashier.creditNoteCellarNumberSeq) + 1;
            vm.seqNumberFirstP = vm.userSubsidiary.threeCode + '-' + vm.userCashier.threeCode;
            vm.seqNumberSecondP = utilSeqService._pad_with_zeroes(vm.numberAddedO, 9);
            vm.seqNumbCredNotCellar =  vm.seqNumberFirstP + '-' + vm.seqNumberSecondP;
        };

        function _getCellarTotal() {
            vm.cellar.subTotal = 0;
            vm.cellar.iva = 0;
            vm.cellar.ivaZero = 0;
            vm.cellar.ice = 0;
            vm.cellar.baseTaxes = 0;
            vm.cellar.baseNoTaxes = 0;
            var subtotal = 0;
            var baseZero = 0;
            var baseDoce = 0;
            var iva = 0;
            var ivaZero = 0;
            var total = 0;
            _.each(vm.cellar.detailsCellar, function(detail) {
                subtotal = subtotal + (detail.quantity * detail.costEach);
                if (detail.product.iva) {
                    baseDoce = baseDoce + (detail.quantity * detail.costEach);
                    iva = baseDoce * 0.12;
                } else {
                    baseZero = baseZero + (detail.quantity * detail.costEach);
                    ivaZero = 0;
                };
                
            });
            total = baseDoce + baseZero + iva;
            vm.cellar.subTotal = subtotal;
            vm.cellar.iva = iva;
            vm.cellar.ivaZero = ivaZero;
            vm.cellar.ice = 0;
            vm.cellar.baseTaxes = baseDoce;
            vm.cellar.baseNoTaxes = baseZero;
            vm.cellar.total = total;
        };

        vm.providerSelect = function(provider) {
            vm.error = undefined;
            vm.success = undefined;
            vm.companyData = vm.user.subsidiary;
            vm.providerSelected = provider;
            cellarService.getCellarsByProviderIdAndNoCN(vm.providerSelected.id).then(function(response) {
                vm.cellarList = response;
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cellarSelect = function(cellar) {
            vm.items = [];
            vm.newCNCellar = true;
            cellarService.getAllCellarById(cellar.id).then(function(response) {
                vm.cellar = angular.copy(response);
                vm.cellar.detailsCellar = [];
                vm.idCellar = cellar.id;
                vm.cellar.provider = vm.providerSelected;
                vm.cellar.userIntegridad= vm.user;
                vm.cellar.subsidiary= vm.user.subsidiary;
                _.each(response.detailsCellar, function(detail) {
                    detail.id = undefined;
                    vm.cellar.detailsCellar.push(detail);
                });
                vm.cellar.documentStringSeq = response.billNumber;
                _getSeqNumbCredNotCellar();
                var today = new Date();
                $('#pickerCreditNoteCellarDate').data("DateTimePicker").date(today);
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.editDetailCellar = function(index) {
            vm.indexDetailCellar = index;
            vm.detailCellar = angular.copy(vm.cellar.detailsCellar[index]);
        };

        vm.acceptProdEdited = function() {
            vm.detailCellar.total = parseFloat((parseFloat(vm.detailCellar.quantity) * parseFloat(vm.detailCellar.costEach)).toFixed(4));
            vm.cellar.detailsCellar[vm.indexDetailCellar] = angular.copy(vm.detailCellar);
            vm.indexDetailCellar = undefined;
            vm.detailCellar = undefined;
            _getCellarTotal();
        };

        vm.removeDetailCellar = function(index) {
            vm.cellar.detailsCellar.splice(index,1);
            _getCellarTotal();
        };

        vm.addProductServices = function() {
            vm.indexDetailCellar = undefined;
            vm.loading = true;
            vm.errorQuantity = undefined;
            vm.page = 0;
            vm.searchText = undefined;
            setTimeout(function() {
                document.getElementById("prod011").focus();
            }, 200);
            _filterProductServices();
        };

        function _filterProductServices() {
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
                vm.productServicesList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.cellar.detailsCellar, function(detail) {
                        return detail.product.id === response.content[i].id;
                    });
                    if (productFound === undefined) {
                        var sub = _.find(response.content[i].productBySubsidiaries, function(s) {
                            return (s.subsidiary.id === vm.user.subsidiary.id && s.active === true);
                        });
                        if (sub) {
                            response.content[i].quantity = sub.quantity
                            vm.productServicesList.push(response.content[i]);
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
                _filterProductServices();
            };
        };

        vm.filter = function() {
            vm.page = 0;
            _filterProductServices();
        };
    
        vm.paginate = function(page) {
            vm.page = page;
            _filterProductServices();
        };
    
        vm.getActiveClass = function(index) {
            var classActive = vm.page === index? 'active' : '';
            return classActive;
        };
    
        vm.range = function() {
            return new Array(vm.totalPages);
        };

        vm.selectProductServicesToAdd = function(productSelect) {
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            };
            vm.productToAdd = angular.copy(productSelect);
            var costEachCalculated = productSelect.averageCost;
            vm.productToAdd.costEachCalculated = costEachCalculated;
            vm.quantity = 1;
        };

        vm.acceptProductServices = function(closeModal) {
            vm.errorQuantity = undefined;
            var detail = {
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                total: parseFloat((vm.quantity * vm.productToAdd.costEachCalculated).toFixed(2)),
                adicional: vm.adicional
            };
            if (vm.indexDetailCellar !== undefined) {
                vm.cellar.detailsCellar[vm.indexDetailCellar] = detail;
            } else {
                vm.cellar.detailsCellar.push(detail);
            };
            vm.productToAdd = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            _getCellarTotal();
            if (closeModal) {
                $('#modalAddProductServices').modal('hide');
            } else {
                var newProductList = _.filter(vm.productServicesList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productServicesList = newProductList;
            };
        };

        vm.saveCreditNoteCellar = function() {
            vm.loading = true;
            debtsToPayService.getDebtsToPayByProdiverIdAndBillNumber(vm.providerSelected.id, vm.cellar.documentStringSeq).then(function(response) {
                if (response.length === 0) {
                    vm.error = 'NO se puede guardar la Nota de Crédito, debibo que la Factura de Compra NO Existe en Cuentas por Pagar';
                    vm.loading = false;
                } else {
                    vm.cellar.creditSeq = vm.numberAddedO;
                    vm.cellar.cellarSeq = vm.idCellar;
                    vm.cellar.stringSeq = vm.seqNumbCredNotCellar;
                    creditNoteCellarService.create(vm.cellar).then(function(response) {
                        vm.newCNCellar = false;
                        vm.userCashier.creditNoteCellarNumberSeq = vm.cellar.creditSeq;
                        vm.success = 'Nota de Crédito creada';
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.creditsNoteByProvider = function(provider) {
            vm.error = undefined;
            vm.success = undefined;
            vm.providerName = provider.name;
            creditNoteCellarService.getCreditsNoteCellarByProviderId(provider.id).then(function(response) {
                vm.creditNoteCellarList = response;
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.creditNoteCellarSelect = function(creditNote) {
            vm.loading = true;
            vm.error = undefined;
            vm.success = undefined;
            vm.companyData = vm.user.subsidiary;
            creditNoteCellarService.getCreditNoteCellarById(creditNote.id).then(function(response) {
                vm.cellar = response;
                vm.providerSelected = response.provider;
                vm.loading = false;
            }).catch(function (error) {
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
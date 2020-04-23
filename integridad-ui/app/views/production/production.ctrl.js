'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProductionCtrl
 * @description
 * # ProductionCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('ProductionCtrl', function(_, $localStorage, providerService, productService, warehouseService, validatorService, subsidiaryService, $routeParams,
                                           productTypeService, messurementListService, clientService, cellarService, consumptionService, utilSeqService, $location,
                                           cuentaContableService,brandService, lineService, groupService, subgroupService, utilStringService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.userData = $localStorage.user;
        vm.clientList = undefined;
        vm.catedral = '1e2049c3-a3bc-4231-a0de-dded8020dc1b';
        vm.maxCode = undefined;

        vm.prices = [
            { name: 'EFECTIVO', cod: 'cashPercentage'}, { name: 'MAYORISTA', cod: 'majorPercentage'},
            { name: 'CREDITO', cod: 'creditPercentage'}, { name: 'TARJETA', cod: 'cardPercentage'}
        ];

        vm.providerType = [
            'PROVEEDORES LOCALES O NACIONALES 01',
            'PROVEEDORES DEL EXTERIOR 02',
        ];
    
        vm.purchaseType = [
            {code: 'BIEN', name: 'BIENES'},
            {code: 'SERV', name: 'SERVICIOS'},
            {code: 'MATP', name: 'MATERIA PRIMA'},
            {code: 'CONS', name: 'CONSUMIBLES'},
            {code: 'RMBG', name: 'REEMBOLSO DE GASTOS'},
            {code: 'TKAE', name: 'TIKETS AEREOS'}
        ];

        function _activate() {
            vm.newCellar = undefined;
            vm.errorCalc = false;
            vm.celled = false;
            vm.cellarList = undefined;
            vm.cellarSavedList = undefined;
            vm.cellarPendingSelected = undefined;
            vm.cellSeqNumber = undefined;
            vm.dateCellar = undefined;
            vm.detailCellarList = undefined;
            vm.clientList = undefined;
            vm.clientSelected = undefined;
            vm.consumption = undefined;
            vm.newConsumption = undefined;
            vm.consumptioned = false;
            vm.csmNumberSeq = undefined;
            vm.dateConsumption = undefined;
            vm.providerList = undefined;
            vm.providerSelected = undefined;
            vm.warehouse = undefined;
            vm.warehouseName = undefined;
            vm.warehouseSubsidiaryName = undefined;
            vm.warehouseSelected = undefined;
            vm.warehouseList = undefined;
            vm.aux = undefined;
            vm.loading = true;
            vm.csmObservation = undefined;
            vm.priceType = vm.prices[0];
            vm.success = undefined;
            vm.error = undefined;
            vm.prodKarId = undefined;
            vm.prodKarName = undefined;
            vm.prodSet = undefined;
            vm.prodSetId = undefined;
            vm.prodSetName = undefined;
            vm.prodSetCostEach = undefined;
            vm.productBarCode = undefined;
            vm.setObserva = undefined;
            vm.setDetalle = undefined;
            vm.selectedKardex = undefined;
            vm.selectedForSetting = undefined;
            vm.reportList = undefined;
            vm.isProductReportList = undefined;
            vm.dateOne = undefined;
            vm.dateTwo = undefined;
            vm.maxCode = undefined;
            vm.impuestosTotales = [];
            vm.impuestoICE = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "3",
                "codigo_porcentaje": 2
            };
            vm.impuestoIVA = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "2",
                "codigo_porcentaje": 2
            };
            vm.impuestoIVAZero = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "2",
                "codigo_porcentaje": 0
            };
            vm.usrCliId = $localStorage.user.subsidiary.userClient.id;
            vm.subsidiaryId = $localStorage.user.subsidiary.id;
            vm.userCode = $localStorage.user.userType.code;
            vm.userId = $localStorage.user.id;
            vm.provider = undefined;
            vm.messurements = messurementListService.getMessurementList();

            warehouseService.getAllWarehouseByUserClientId(vm.usrCliId).then(function(response) {
                vm.warehouseList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });

            productTypeService.getproductTypesLazy().then(function(response) {
                vm.productTypes = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });

            cuentaContableService.getCuentaContableByType($localStorage.user.subsidiary.userClient.id, 'INVT').then(function(response) {
                vm.cuentaContableList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        //Warehouse Code
        vm.selectWarehouse = function(warehouse) {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;
            vm.warehouse = warehouse;
            vm.warehouseSelected = warehouse;
            vm.cellarList = undefined;
            vm.clientList = undefined;
            vm.warehouseName = warehouse.nameNumber;
            vm.warehouseSubsidiaryName = warehouse.subsidiary.name;
            providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                vm.providerList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelSelectWarehouse = function() {
            vm.providerList = undefined;
            vm.warehouseSelected = undefined;
        };

        //Cellar Code
        function _getCellarSeqNumber() {
            vm.numberAddedOne = parseInt($localStorage.user.cashier.whNumberSeq) + 1;
            vm.cellSeqNumber = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
        };

        function _initializeCellar() {
            vm.cellar = {
                warehouse: vm.warehouse,
                provider: vm.providerSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                dateEnterCellar: vm.dateCellar.getTime(),
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                baseNoTaxes: 0,
                baseTaxes: 0,
                detailsCellar: [],
                descuento: 0,
            };
        };

        vm.providerSelectToEnterCellar = function(provider) {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;
            vm.dateCellar = new Date();
            vm.providerList = undefined;
            vm.clientList = undefined;
            vm.providerSelected = provider;
            _getCellarSeqNumber();
            _initializeCellar();
            var today = new Date();
            $('#pickerDateEnterCellar').data("DateTimePicker").date(today);
            vm.loading = false;
            vm.newCellar = true;
        };

        vm.consultSavedCellar = function(provider) {
            vm.loading = true;
            vm.providerList = undefined;
            cellarService.getCellarsByProviderId(provider.id).then(function(response) {
                vm.cellarSavedList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getDetailsOfCellars = function() {
            vm.loading = true;
            cellarService.getDetailsOfCellarsByUserClientId(vm.usrCliId).then(function(response) {
                vm.detailCellarList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cellarSelect = function(cellar, isView) {
            vm.loading = true;
            if(isView) vm.cellarSavedList = undefined;
            cellarService.getAllCellarById(cellar.id).then(function(response) {
                vm.cellar = response;
                vm.cellSeqNumber = response.whNumberSeq;
                var dateToShow = new Date(response.dateEnterCellar);
                $('#pickerDateEnterCellar').data("DateTimePicker").date(dateToShow);
                var dateBillToShow = new Date(response.dateBill);
                $('#pickerDateBill').data("DateTimePicker").date(dateBillToShow);
                vm.newCellar = false;
                vm.providerSelected = isView ? response.provider : undefined;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.inactivateCellar = function (){
            vm.loading = true;
            cellarService.inactivateCellar(vm.cellar).then(function(response) {
                _.each(vm.cellarSavedList, function(item) {
                    if(item.id === vm.cellar.id){
                        item.statusIngreso = 'ANULADO';
                        item.active = false;
                    }
                });

                vm.cellar = undefined;
                vm.loading = false;
            }).catch(function(error){
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelConsultSavedCellar = function() {
            vm.cellarSavedList = undefined;
            vm.warehouseSelected = undefined;
        };

        vm.printToCartAndCancel = function(printMatrixCellarId) {
            var innerContents = document.getElementById(printMatrixCellarId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixCellarId', 'width=300,height=400');
            popupWinindow.document.write('<html><head><title>printMatrixCellarId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate();
        };

        vm.getDateToPrint = function() {
            if (vm.cellar != undefined) {
                return $('#pickerDateEnterCellar').data("DateTimePicker").date().toDate();
            };
        };

        vm.findCellarPending = function(warehouse) {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;
            vm.clientList = undefined;
            vm.warehouseSelected = warehouse;
            vm.warehouseName = warehouse.nameNumber;
            vm.warehouseSubsidiaryName = warehouse.subsidiary.name;
            cellarService.getAllCellarsPendingOfWarehouse(warehouse.id).then(function(response) {
                vm.cellarList = response;
                vm.cellarPendingSelected = response.id;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelFindCellars = function() {
            vm.warehouseSelected = undefined;
        };

        function _getCellarTotalSubtotal() {
            vm.cellar.subTotal = 0;
            vm.cellar.iva = 0;
            vm.cellar.ivaZero = 0;
            vm.cellar.ice = 0;
            vm.cellar.baseTaxes = 0;
            vm.cellar.baseNoTaxes = 0;
            var ivac = 0;
            var discountWithIva = 0;
            var discountWithNoIva = 0;
            if (!vm.cellar.descuento || vm.cellar.descuento.trim() === '') {
                vm.cellar.descuento = 0;
            };
            _.each(vm.cellar.detailsCellar, function(detail) {
                vm.cellar.subTotal = (parseFloat(vm.cellar.subTotal) + parseFloat(detail.total)).toFixed(2);
                var tot = detail.total;
                if (detail.product.iva) {
                    vm.cellar.baseTaxes += parseFloat(detail.total);
                    ivac = (vm.cellar.baseTaxes - parseFloat(vm.cellar.descuento)) * 0.12;
                    vm.cellar.iva = ivac.toFixed(2);
                    if (vm.cellar.discountPercentage) {
                        discountWithIva = (parseFloat(discountWithIva) + ((parseInt(vm.cellar.discountPercentage) / 100) * detail.total)).toFixed(2);
                    };
                } else {
                    vm.cellar.baseNoTaxes += parseFloat(detail.total);
                    vm.cellar.ivaZero = (parseFloat(vm.cellar.ivaZero) + parseFloat(tot)).toFixed(2);
                    if (vm.cellar.discountPercentage) {
                        discountWithNoIva = (parseFloat(discountWithNoIva) + ((parseInt(vm.cellar.discountPercentage) / 100) * detail.total)).toFixed(2);
                    };
                };
                if (detail.product.ice) {
                    vm.cellar.ice = (parseFloat(vm.cellar.ice) + (parseFloat(tot) * 0.10)).toFixed(2);
                };
            });
            vm.impuestoICE.base_imponible = vm.cellar.subTotal;
            vm.impuestoIVA.base_imponible = vm.cellar.baseTaxes;
            vm.impuestoIVAZero.base_imponible = vm.cellar.baseNoTaxes;
            vm.impuestoICE.valor = vm.cellar.ice;
            vm.impuestoIVA.valor = vm.cellar.iva;
            vm.impuestoIVAZero.valor = 0;
            vm.cellar.baseTaxes = (parseFloat(vm.cellar.baseTaxes - discountWithIva) - parseFloat(vm.cellar.descuento)).toFixed(2);
            vm.cellar.baseNoTaxes = (vm.cellar.baseNoTaxes - discountWithNoIva).toFixed(2);
            vm.cellar.total = (parseFloat(vm.cellar.baseTaxes)
                + parseFloat(vm.cellar.baseNoTaxes)
                + parseFloat(vm.cellar.iva)
                + parseFloat(vm.cellar.ice)).toFixed(2);
        };

        vm.recalculateTotalCellar = function() {
            if (vm.cellar.descuento == null || vm.cellar.descuento == undefined) {
                vm.cellar.descuento = 0;
            };
            _getCellarTotalSubtotal();
        };

        vm.selectProductToAdd = function(productSelect) {
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            };
            vm.productToAdd = angular.copy(productSelect);
            vm.productToAdd.costCellarPersisted = productSelect.costCellar;
            vm.productToAdd.quantityCellarPersisted = productSelect.quantityCellar;
            if (vm.productToAdd.costEach === null) {
                var costEachCalculated = vm.getCost(productSelect.cashPercentage, productSelect.averageCost);
                vm.productToAdd.costEach = costEachCalculated;
                vm.productToAdd.averageCostSuggested = vm.productToAdd.averageCost
            };
            vm.quantity = 1;
        };

        vm.calcAvg = function() {
            vm.errorCalc = false;
            var costCellar = 0;
            var quantityCellar = parseInt(vm.productToAdd.quantityCellarPersisted) + parseInt(vm.quantity);
            costCellar = parseFloat((vm.quantity * vm.productToAdd.costEach).toFixed(4));

            if (quantityCellar <= 0) {
                quantityCellar = parseInt(vm.quantity);
            };

            vm.productToAdd.averageCostSuggested = ((vm.productToAdd.costCellarPersisted + costCellar) / quantityCellar).toFixed(4);
            if (isNaN(vm.productToAdd.averageCostSuggested)) {
                vm.errorCalc = true;
            };
        };

        vm.acceptCellarProduct = function(closeModal) {
            vm.productToAdd.quantityCellar = parseInt(vm.productToAdd.quantityCellar) + parseInt(vm.quantity);
            vm.productToAdd.costCellar = vm.productToAdd.costCellar + (vm.productToAdd.costEach * vm.quantity);

            var detail = {
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEach,
                total: (parseFloat(vm.quantity) * parseFloat(vm.productToAdd.costEach)).toFixed(4),
                adicional: vm.adicional
            };

            if (vm.indexDetail !== undefined) {
                vm.cellar.detailsCellar[vm.indexDetail] = detail;
            } else {
                vm.cellar.detailsCellar.push(detail);
            };

            vm.quantity = undefined;
            vm.adicional = undefined;
            _getCellarTotalSubtotal();
            if (closeModal) {
                $('#modalAddProductCellar').modal('hide');
            } else {
                var newProductList = _.filter(vm.productList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
        };

        vm.removeDetail = function(index) {
            vm.cellar.detailsCellar.splice(index,1);
            _getCellarTotalSubtotal();
        };

        vm.disableSave = function(){
            return vm.cellar === undefined ? true : vm.cellar.total <= 0 || $('#pickerDateBill').data("DateTimePicker").date() === null || vm.cellar.billNumber === undefined;
        }

        vm.saveCellar = function(cellar) {
            vm.loading = true;
            vm.newCellar = false;
            vm.cellar.dateBill = $('#pickerDateBill').data("DateTimePicker").date().toDate().getTime();
            vm.cellar.dateEnterCellar = $('#pickerDateEnterCellar').data("DateTimePicker").date().toDate().getTime();
            vm.cellar.cellarSeq = parseInt(vm.numberAddedOne);
            vm.cellar.whNumberSeq = vm.cellSeqNumber;
            vm.cellar.detailsKardex = [];
            if (vm.userCode === 'EMP') {
                vm.cellar.statusIngreso = 'PENDIENTE';
            } else {
                vm.cellar.statusIngreso = 'INGRESADO';
            };
            _.each(vm.cellar.detailsCellar, function(item) {
                var kardex = {
                    cellar: cellar.id,
                    product: item.product,
                    codeWarehouse: vm.warehouse.codeWarehouse,
                    dateRegister: $('#pickerDateEnterCellar').data("DateTimePicker").date().toDate().getTime(),
                    details: 'INGRESO A BODEGA Nro. ' + vm.cellSeqNumber + ', Fact. ' + vm.cellar.billNumber,
                    observation: 'INGRESO',
                    detalle: '--',
                    prodCostEach: item.costEach,
                    prodName: item.product.name,
                    prodQuantity: item.quantity,
                    prodTotal: item.total,
                    subsidiaryId: vm.subsidiaryId,
                    userClientId: vm.usrCliId,
                    userId: vm.userId
                };
                vm.cellar.detailsKardex.push(kardex);
            });
            cellarService.getByUserClientIdAndBillNumberActive(vm.usrCliId, vm.cellar.billNumber).then(function(response) {
                if (response.length === 0) {
                    cellarService.create(vm.cellar).then(function(respCellar) {
                        vm.celled = true;
                        vm.cellarCreated = respCellar;
                        $localStorage.user.cashier.whNumberSeq = vm.numberAddedOne;
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.error = 'El Nro. de Documento (Factura) Ya Existe y no puede repetirse';
                    vm.loading = false;
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelCellar = function() {
            vm.warehouseSelected = undefined;
            vm.providerSelected = undefined;
            vm.error = undefined;
        };

        vm.cellarSelected = function(cellar) {
            vm.loading = true;
            vm.cellarPendingSelected = cellar.id;
            cellarService.getAllCellarById(cellar.id).then(function(response) {
                vm.cellar = response;
                vm.cellarDetails = response.detailsCellar;
                vm.cellarProviderSelected = response.provider;
                vm.billNumber = response.billNumber;
                vm.cellSeqNumber = response.whNumberSeq;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelCellarSelected = function() {
            vm.cellarPendingSelected = undefined;
        };

        vm.validateEnterCellar = function() {
            vm.loading = true;
            vm.error = undefined;
            vm.success = undefined;
            cellarService.validateCellar(vm.validateCellar).then(function(response) {
                var index = vm.cellarList.indexOf(vm.validateCellar);
                if (index > -1) {
                    vm.cellarList.splice(index, 1);
                };
                vm.validateCellar = undefined;
                vm.loading = false;
            }).catch(function(error) {
                vm.success = 'Ingreso a Bodega realizado con exito';
                vm.loading = false;
                vm.error = error.data;
            });
        };

        //Ajuste por Kardex
        function _initializeKardex() {
            vm.kardex = {
                codeWarehouse: vm.warehouseSelected.codeWarehouse,
                product: vm.prodSet,
                subsidiaryId: vm.subsidiaryId,
                userClientId: vm.usrCliId,
                userId: vm.userId,
                prodCostEach: vm.prodSetCostEach,
                prodQuantity: 0,
                prodName: vm.prodSetName
            };
        };

        vm.settingForKardex = function(warehouse) {
            vm.warehouseSelected = warehouse;
            vm.searchTextSet = undefined;
            vm.success = undefined;
            vm.loading = true;
            productTypeService.getproductTypesLazy().then(function(response) {
                vm.selectedForSetting = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.error = error.data;
                vm.loading = false;
            });
            setTimeout(function() {
                document.getElementById("prod013").focus();
            }, 200);            
            vm.pageSet = 0;
            _filterSet();
        };

        function _filterSet() {
            vm.loading = true;
            vm.totalPagesSet = 0;
            vm.productListSet = [];
            var variableSet = vm.searchTextSet? vm.searchTextSet : null;
            if (variableSet == null) {
                var busquedaSet = variableSet;
            } else {
                var busquedaSet = variableSet.toUpperCase();
            };
            if ($routeParams.subsidiaryId) {
                productService.getLazyBySusidiaryId($routeParams.subsidiaryId, vm.pageSet, busquedaSet).then(function(response) {
                    vm.totalPagesSet = response.totalPages;
                    vm.totalElementsSet = response.totalElements;
                    _getProductQuantitiesSet(response.content);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.error = error.data;
                    vm.loading = false;
                });
            } else {
                productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.pageSet, busquedaSet).then(function(response) {
                    vm.totalElementsSet = response.totalElements;
                    vm.totalPagesSet = response.totalPages;
                    _getProductQuantitiesSet(response.content);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.error = error.data;
                    vm.loading = false;
                });
            };
        };

        vm.filterEventSet = function(event) {
            vm.pageSet = 0;
            if (event.keyCode === 13 || event.charCode === 13) {
                _filterSet();
            };
        };

        vm.filterSet = function() {
            vm.pageSet = 0;
            _filterSet();
        };

        function _getProductQuantitiesSet(listResponse) {
            for (var i = 0; i < listResponse.length; i++) {
                var sub = _.find(listResponse[i].productBySubsidiaries, function(s) {
                    return (s.subsidiary.id === $localStorage.user.subsidiary.id && s.active === true);
                });
                if (sub) {
                    listResponse[i].quantity = sub.quantity
                    vm.productListSet.push(listResponse[i]);
                };
            };
        };
        
        vm.paginateSet = function(page) {
            vm.pageSet = page;
            _filterSet();
        };

        vm.rangeSet = function() {
            return new Array(vm.totalPagesSet);
        };

        vm.getActiveClassSet = function(index) {
            var classActiveSet = vm.pageSet === index? 'active' : '';
            return classActiveSet;
        };

        vm.cancelSetting = function() {
            _activate();
        };

        vm.selectProductForSetting = function(productSet) {
            vm.prodSet = productSet;
            vm.prodSetId = productSet.id;
            vm.prodSetName = productSet.name;
            vm.prodSetCostEach = productSet.costEach;
            _initializeKardex();
        };

        vm.saveKardex = function() {
            vm.loading = true;
            if (parseInt(vm.kardex.prodQuantity) <= 0 ) {
                vm.error = 'La cantidad ingresada NO puede ser menor o igual a 0';
                vm.loading = false;
            } else {
                vm.kardex.dateRegister = $('#pickerSettingKardexDate').data("DateTimePicker").date().toDate().getTime();
                vm.kardex.prodCostEach = vm.prodSetCostEach;
                vm.kardex.prodTotal = parseFloat((vm.prodSetCostEach * vm.kardex.prodQuantity).toFixed(2));
                vm.kardex.observation = vm.setObserva;
                vm.kardex.detalle = vm.setDetalle;
                vm.kardex.details = vm.setObserva + ' - ' + vm.setDetalle;
                productService.createKardex(vm.kardex).then(function(respKar) {
                    vm.kardexCreated = respKar;
                    vm.success = 'Ajuste Realizado';
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
                _activate();
            };
        };
        //Fin Ajuste Kardex

        //Reporte Kardex
        vm.reportKardex = function(warehouse) {
            vm.warehouseSelected = warehouse;
            vm.searchTextKar = undefined;
            vm.success = undefined;
            vm.loading = true;
            productTypeService.getproductTypesLazy().then(function(response) {
                vm.selectedKardex = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.error = error.data;
                vm.loading = false;
            });
            vm.pageKar = 0;
            _filterKar();
        };

        vm.selectProductForKardex = function(productKar) {
            vm.productListKar = undefined;
            vm.prodKarId = productKar.id;
            vm.prodKarName = productKar.name;
        };
        
        vm.getKardexReportForProduct = function() {
            vm.isProductReportList = '1';
            vm.reportList = undefined;
            vm.loading = true;
            vm.dateOne = $('#pickerKardexDateOne').data("DateTimePicker").date().toDate().getTime();
            vm.dateTwo = $('#pickerKardexDateTwo').data("DateTimePicker").date().toDate().getTime();
            vm.dateTwo += 86399000;
        
            productService.getKardexActivesByUserClientIdAndProductIdAndDates(vm.usrCliId, vm.prodKarId, vm.dateOne, vm.dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelKardex = function() {
            _activate();
        };

        vm.exportExcel = function() {
            var dataReport = [];
            switch (vm.isProductReportList) {
                case '1':
                    _.each(vm.reportList, function(kardex) {
                        var data = {
                            FECHA: kardex.fecha,
                            PRODUCTO: kardex.prodName,
                            DESCRIPCION: kardex.detalle,
                            ENTRA: parseFloat(kardex.entrada.toFixed(2)),
                            SALE: parseFloat(kardex.salida.toFixed(2)),
                            SALDOS: parseFloat(kardex.saldo.toFixed(2)),
                            COST_COMP: parseFloat(kardex.compra.toFixed(2)),
                            COST_PROM: parseFloat(kardex.promedio.toFixed(2))
                        };
            
                        dataReport.push(data);
                    });
                break;
            };
            var ws = XLSX.utils.json_to_sheet(dataReport);
            /* add to workbook */
            var wb = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(wb, ws, "Kardex_Producto");
            /* write workbook and force a download */
            XLSX.writeFile(wb, 'Reporte_Kardex_'.concat(vm.prodKarName, '.xlsx'));
        };

        function _filterKar() {
            vm.loading = true;
            vm.totalPagesKar = 0;
            vm.productListKar = [];
            var variableKar = vm.searchTextKar? vm.searchTextKar : null;
            if (variableKar == null) {
                var busquedaKar = variableKar;
            } else {
                var busquedaKar = variableKar.toUpperCase();
            };
            if ($routeParams.subsidiaryId) {
                productService.getLazyBySusidiaryId($routeParams.subsidiaryId, vm.pageKar, busquedaKar).then(function(response) {
                    vm.totalPagesKar = response.totalPages;
                    vm.totalElementsKar = response.totalElements;
                    _getProductQuantitiesKar(response.content);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.error = error.data;
                    vm.loading = false;
                });
            } else {
                productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.pageKar, busquedaKar).then(function(response) {
                    vm.totalElementsKar = response.totalElements;
                    vm.totalPagesKar = response.totalPages;
                    _getProductQuantitiesKar(response.content);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.error = error.data;
                    vm.loading = false;
                });
            };
        };

        vm.filterEventKar = function(event) {
            vm.pageKar = 0;
            if (event.keyCode === 13 || event.charCode === 13) {
                _filterKar();
            };
        };

        vm.filterKar = function() {
            vm.pageKar = 0;
            _filterKar();
        };
        
        function _getProductQuantitiesKar(listResponse) {
            for (var i = 0; i < listResponse.length; i++) {
                var sub = _.find(listResponse[i].productBySubsidiaries, function(s) {
                    return (s.subsidiary.id === $localStorage.user.subsidiary.id && s.active === true);
                });
                if (sub) {
                    listResponse[i].quantity = sub.quantity
                    vm.productListKar.push(listResponse[i]);
                };
            };
        };
                
        vm.paginateKar = function(page) {
            vm.pageKar = page;
            _filterKar();
        };
        
        vm.rangeKar = function() {
            return new Array(vm.totalPagesKar);
        };
        
        vm.getActiveClassKar = function(index) {
            var classActiveKar = vm.pageKar === index? 'active' : '';
            return classActiveKar;
        };
        //Fin Reporte Kardex

        //Consumption Code
        function _getCsmSeqNumber() {
            vm.numberCsmAddedOne = parseInt($localStorage.user.cashier.csmNumberSeq) + 1;
            vm.csmSeqNumber = utilSeqService._pad_with_zeroes(vm.numberCsmAddedOne, 6);
        };

        function _initializeConsumption() {
            vm.consumption = {
                client: vm.clientSelected,
                warehouse: vm.warehouse,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                dateConsumption: vm.dateConsumption.getTime(),
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                baseNoTaxes: 0,
                baseTaxes: 0,
                detailsConsumption: []
            };
        };

        vm.consumptionFromWarehouse = function(warehouse) {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;
            vm.providerList = undefined;
            vm.providerSelected = undefined;
            vm.cellarList = undefined;
            vm.warehouse = warehouse;
            vm.warehouseSelected = warehouse;
            vm.dateConsumption = new Date();
            vm.warehouseName = warehouse.nameNumber;
            vm.warehouseSubsidiaryName = warehouse.subsidiary.name;
            vm.consumptionList = undefined;
            clientService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                vm.clientList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientSelect = function(client) {
            vm.companyData = $localStorage.user.subsidiary;
            vm.dateConsumption = new Date();
            vm.clientList = undefined;
            vm.warehouseSelected = true;
            vm.cellarList = undefined;
            vm.providerList = undefined;
            vm.clientSelected = client;
            _getCsmSeqNumber();
            _initializeConsumption();
            var today = new Date();
            $('#pickerDateConsumption').data("DateTimePicker").date(today);
            vm.newConsumption = true;
        };

        vm.clientConsult = function(client) {
            vm.loading = true;
            vm.clientList = undefined;
            vm.warehouseSelected = true;
            vm.cellarList = undefined;
            vm.providerList = undefined;
            consumptionService.getAllConsumptionByClientId(client.id).then(function(response) {
                vm.consumptionList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelClientConsult = function() {
            vm.warehouseSelected = undefined;
            vm.consumptionList = undefined;
        };

        function _getConsupmtionTotalSubtotal() {
            vm.consumption.subTotal = 0;
            vm.consumption.iva = 0;
            vm.consumption.ivaZero = 0;
            vm.consumption.ice = 0;
            vm.consumption.baseTaxes = 0;
            vm.consumption.baseNoTaxes = 0;
            var discountWithIva = 0;
            var discountWithNoIva = 0;
            _.each(vm.consumption.detailsConsumption, function(detail) {
                vm.consumption.subTotal = (parseFloat(vm.consumption.subTotal) + parseFloat(detail.total)).toFixed(4);
                var tot = detail.total;
                if (vm.consumption.discountPercentage) {
                    tot = (parseFloat(detail.total) - (parseInt(vm.consumption.discountPercentage) / 100) * (parseFloat(detail.total))).toFixed(4);
                };
                if (detail.product.iva) {
                    vm.consumption.baseTaxes += parseFloat(detail.total);
                    if (vm.consumption.discountPercentage) {
                        discountWithIva = (parseFloat(discountWithIva) + ((parseInt(vm.consumption.discountPercentage) / 100) * detail.total)).toFixed(4);
                    };
                } else {
                    vm.consumption.baseNoTaxes += parseFloat(detail.total);
                    if (vm.consumption.discountPercentage) {
                        discountWithNoIva = (parseFloat(discountWithNoIva) + ((parseInt(vm.consumption.discountPercentage) / 100) * detail.total)).toFixed(4);
                    };
                };
                if (detail.product.ice) {
                    vm.consumption = (parseFloat(vm.consumption) + (parseFloat(tot) * 0.10)).toFixed(4);
                };
            });
            if (vm.consumption.discountPercentage) {
                vm.consumption.discount = ((parseInt(vm.consumption.discountPercentage) / 100) * vm.consumption.subTotal).toFixed(4);
            } else {
                vm.consumption.discount = 0;
            };
            vm.impuestoICE.base_imponible = vm.consumption.subTotal;
            vm.impuestoIVA.base_imponible = vm.consumption.baseTaxes;
            vm.impuestoIVAZero.base_imponible = vm.consumption.baseNoTaxes;
            vm.impuestoICE.valor = vm.consumption.ice;
            vm.impuestoIVA.valor = vm.consumption.iva;
            vm.impuestoIVAZero.valor = 0;
            vm.consumption.baseTaxes = (vm.consumption.baseTaxes - discountWithIva).toFixed(4);
            vm.consumption.baseNoTaxes = (vm.consumption.baseNoTaxes - discountWithNoIva).toFixed(4);
            vm.consumption.total = (parseFloat(vm.consumption.baseTaxes)
                + parseFloat(vm.consumption.baseNoTaxes)
                + parseFloat(vm.consumption.iva)
                + parseFloat(vm.consumption.ice)).toFixed(4);
        };

        vm.acceptConsupmtionProduct = function(closeModal) {
            var detail = {
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEach,
                averageCostSuggested: vm.productToAdd.averageCostSuggested,
                total: (parseFloat(vm.quantity) * parseFloat(vm.productToAdd.averageCostSuggested)).toFixed(4)
            };
            if (vm.indexDetail !== undefined) {
                vm.consumption.detailsConsumption[vm.indexDetail] = detail;
            } else {
                vm.consumption.detailsConsumption.push(detail);
            };
            vm.productToAdd = undefined;
            vm.quantity = undefined;
            _getConsupmtionTotalSubtotal();
            if (closeModal) {
                $('#modalAddProductConsump').modal('hide');
            } else {
                var newProductList = _.filter(vm.productList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
        };

        vm.saveConsumption = function(consumption) {
            vm.loading = true;
            vm.newConsumption = false;
            vm.consumption.dateConsumption = $('#pickerDateConsumption').data("DateTimePicker").date().toDate().getTime();
            vm.consumption.csmSeq = parseInt(vm.numberCsmAddedOne);
            vm.consumption.csmNumberSeq = vm.csmSeqNumber;
            vm.consumption.clientName = vm.clientSelected.name;
            vm.consumption.codeWarehouse = vm.warehouse.codeWarehouse;
            vm.consumption.observation = 'EGRESO -- ' + vm.csmObservation;
            vm.consumption.detailsKardex = [];
            _.each(vm.consumption.detailsConsumption, function(item) {
                var kardex = {
                    consumption: consumption.id,
                    product: item.product,
                    codeWarehouse: vm.warehouse.codeWarehouse,
                    dateRegister: $('#pickerDateConsumption').data("DateTimePicker").date().toDate().getTime(),
                    details: 'EGRESO INTERNO Nro. ' + vm.csmSeqNumber,
                    observation: 'EGRESO',
                    detalle: vm.consumption.observation,
                    prodCostEach: item.costEach,
                    prodName: item.product.name,
                    prodQuantity: item.quantity,
                    prodTotal: item.total,
                    subsidiaryId: vm.subsidiaryId,
                    userClientId: vm.usrCliId,
                    userId: vm.userId
                };
                vm.consumption.detailsKardex.push(kardex);
            });
            consumptionService.create(consumption).then(function(respConsumption) {
                $localStorage.user.cashier.csmNumberSeq = vm.numberCsmAddedOne;
                vm.consumptionCreated = respConsumption;
                vm.consumptioned = true;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelConsumption = function() {
            _activate();
        };

        vm.findConsumptionByClient = function(consumption) {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;
            vm.clientList = undefined;
            vm.warehouseSelected = true;
            vm.cellarList = undefined;
            vm.providerList = undefined;
            vm.consumptionList = undefined;
            vm.consumptionSelected = consumption.id;
            consumptionService.getAllConsumptionById(consumption.id).then(function(response) {
                vm.consumption = response;
                vm.clientSelected = response.client;
                vm.consumptionDetails = response.detailsConsumption;
                vm.consumptionClientSelected = response.client;
                vm.csmSeqNumber = response.csmNumberSeq;
                var dateToShowCsm = new Date(response.dateConsumption);
                $('#pickerDateConsumption').data("DateTimePicker").date(dateToShowCsm);
                vm.newConsumption = false;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelFindConsumption = function() {
            vm.warehouseSelected = undefined;
            vm.consumptionSelected = undefined;
        };

        function _filterProductCsm() {
            vm.totalPages = 0;
            var variable = vm.searchText? vm.searchText : null;
            if (variable == null) {
                var busquedaCsm = variable;
            } else {
                var busquedaCsm = variable.toUpperCase();
            };
            productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, busquedaCsm).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.consumption.detailsConsumption, function(detail) {
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

        vm.filterEventCsm = function(event) {
            vm.page = 0;
            if (event.keyCode === 13 || event.charCode === 13) {
                _filterProductCsm();
            };
        };

        vm.filterCsm = function() {
            vm.page = 0;
            _filterProductCsm();
        };

        vm.printToCartAndCancelCsm = function(printMatrixConsumptionId) {
            var innerContents = document.getElementById(printMatrixConsumptionId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixConsumptionId', 'width=300,height=400');
            popupWinindow.document.write('<html><head><title>printMatrixConsumptionId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate();
        };

        vm.printBillThermal = function(billPrint) {
            var innerContents = document.getElementById(billPrint).innerHTML;
            var popupWinindow = window.open('', 'billPrint', 'width=300,height=400');
            popupWinindow.document.write('<html><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
        };

        vm.getDateToPrintCsm = function() {
            if (vm.consumption != undefined) {
                return $('#pickerDateConsumption').data("DateTimePicker").date().toDate();
            };
        };

        vm.addProductCsm = function() {
            vm.indexDetail = undefined;
            vm.loading = true;
            vm.errorQuantity = undefined;
            vm.page = 0;
            vm.searchText = undefined;
            setTimeout(function() {
                document.getElementById("prod012").focus();
            }, 200);
            _filterProductCsm();
        };

        vm.getCostCsm = function(cashPercen, averageCost) {
            var aC = 1 + ((cashPercen) / 100);
            var cost = aC * averageCost;
            return (cost).toFixed(4);
        };

        vm.selectProductToAddCsm = function(productSelect) {
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            };
            vm.productToAdd = angular.copy(productSelect);
            vm.quantity = 1;
        };

        vm.editDetailCsm = function(detail, index) {
            vm.indexDetail = index;
            vm.productToAdd= detail.product;
            vm.quantity= detail.quantity
        };

        vm.removeDetailCsm = function(index) {
            vm.consumption.detailsConsumption.splice(index,1);
        };

        vm.rangeCsm = function() {
            return new Array(vm.totalPages);
        };

        vm.paginateCsm = function(page) {
            vm.page = page;
            _filterProductCsm();
        };

        vm.getActiveClassCsm = function(index) {
            var classActive = vm.page === index? 'active' : '';
            return classActive;
        };

        //Product Code
        function _filterProduct() {
            vm.totalPages = 0;
            var variableCellar = vm.searchText? vm.searchText : null;
            if (variableCellar == null) {
                var busquedaCellar = variableCellar;
            } else {
                var busquedaCellar = variableCellar.toUpperCase();
            };
            productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, busquedaCellar).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.cellar.detailsCellar, function(detail) {
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

        vm.addProduct = function() {
            vm.productToAdd = undefined;
            vm.selectedGroup = undefined;
            vm.selectedLine = undefined;
            vm.wizard = undefined;
            vm.error = undefined;

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

        vm.getCost = function(cashPercen, averageCost) {
            var aC = 1 + ((cashPercen) / 100);
            var cost = aC * averageCost;
            return (cost).toFixed(4);
        };

        vm.editDetail = function(detail, index) {
            vm.indexDetail = index;
            vm.productToAdd= detail.product;
            vm.quantity= detail.quantity;
            vm.adicional = detail.adicional;
        };

        vm.range = function() {
            return new Array(vm.totalPages);
        };

        vm.paginate = function(page) {
            vm.page = page;
            _filterProduct();
        };

        vm.getActiveClass = function(index) {
            var classActive = vm.page === index? 'active' : '';
            return classActive;
        };

        vm.exit = function() {
            $location.path('/home');
        };

        // NEW PROVIDER ---------------------
        vm.providerCreate = function() {
            vm.error = undefined;
            vm.success = undefined;
            vm.provider = {
                codeIntegridad: vm.providerList.length + 1,
                active: true,
                userClient: vm.userData.subsidiary.userClient
            };
            vm.providerList = undefined;
        };

        vm.cancelProvider = function() {
            vm.error = undefined;
            vm.success = undefined;
            vm.provider = undefined;
            providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                vm.providerList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function createProvider() {
            providerService.getProviderByUserClientIdAndRuc(vm.usrCliId, vm.provider.ruc).then(function(response) {
                if (response.length === 0) {
                    providerService.create(vm.provider).then(function(responseProv) {
                        providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                            vm.provider = undefined;
                            vm.providerList = response;
                            vm.loading = false;
                        }).catch(function(error) {
                            vm.loading = false;
                            vm.error = error.data;
                        });
                        vm.error = undefined;
                        vm.success = 'Registro realizado con exito';
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.error = 'El Proveedor Ya Existe';
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.registerProvider = function() {
            var idValid = true;
            if (vm.provider.rucType === 'CED') {
                idValid = validatorService.isCedulaValid(vm.provider.ruc);
            } else if (vm.provider.rucType === 'RUC') {
                idValid = validatorService.isRucValid(vm.provider.ruc);
            } else if (vm.provider.rucType === 'IEX') {
                idValid = true;
            };

            if (!idValid) {
                vm.error = 'Identificacion invalida';
            } else {
                createProvider();
            };
        };

        // NEW PROVIDER --------------------- END

        // NEW PRODUCT ----------------------

        function _getSubsidiaries() {
            subsidiaryService.getByProjectId($localStorage.user.subsidiary.userClient.id).then(function(response) {
                vm.subsidiaries = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getBrands() {
            brandService.getBrandsLazy($localStorage.user.subsidiary.userClient.id).then(function(response) {
                vm.brands = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getLines() {
            lineService.getLinesLazy($localStorage.user.subsidiary.userClient.id).then(function(response) {
                vm.lineas = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.fillBarCode = function(event){
            if (event.keyCode === 32 || event.charCode === 32) {
                if (vm.productBarCode.length < 13) {
                    vm.productBarCodeFixed = vm.productBarCode;
                    for (var i = vm.productBarCode.length; i < 13; i++) {
                        vm.productBarCodeFixed = vm.productBarCodeFixed.concat('0');
                    };
                    vm.productBarCode = vm.productBarCodeFixed.trim();
                };
            };
        };

        function createProduct() {
            var productBySubsidiary = {
                dateCreated: new Date().getTime(),
                quantity: 0,
                subsidiary: vm.warehouseSelected.subsidiary,
                active: true
            };
            vm.productBySubsidiaries.push(productBySubsidiary);
            vm.product.productBySubsidiaries = vm.productBySubsidiaries;
            vm.product.cuentaContableByProducts = [];
            _.each(vm.cuentasContablesForProductSale, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'VENTA'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductConsume, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'CONSUMO'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductFinished, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'PRODUCTO_TERMINADO'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductCost, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'COSTO_DE_VENTA'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            vm.product.barCode = vm.productBarCode;
            vm.product.costEach = 0;
            vm.product.costCellar = 0;
            vm.product.quantityCellar = 0;
            vm.product.averageCostSuggested = 0;
            productService.create(vm.product).then(function(response) {
                vm.product = undefined;
                vm.selectedGroup = undefined;
                vm.selectedLine = undefined;
                vm.wizard = undefined;
                vm.error = undefined;
                vm.success = 'Registro realizado con exito';
                vm.loading = false;
                vm.selectProductToAdd(response)
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.registerProduct = function() {
            _getSubsidiaries();
            vm.success = undefined;
            vm.error = undefined
            vm.productBySubsidiaries = [];
            vm.cuentasContablesForProductSale = [];
            vm.cuentasContablesForProductConsume = [];
            vm.cuentasContablesForProductFinished = [];
            vm.cuentasContablesForProductCost = [];
            vm.wizard = 1;

            productService.getLastCodeByUserClientIdActive(vm.usrCliId).then(
                function(response){
                    vm.maxCode = response || '';
                }
            );

            vm.product = {
                userClient: $localStorage.user.subsidiary.userClient,
                productBySubsidiaries: []
            };
        };

        vm.cancelProduct = function() {
            vm.success = undefined;
            vm.error = undefined
            vm.wizard = undefined;
        };

        vm.wiz2 = function() {
            vm.productBySubsidiaries = [];
            if (vm.product.productType.code !== 'SER') {
                vm.product.unitOfMeasurementAbbr = vm.messurementSelected.shortName;
                vm.product.unitOfMeasurementFull = vm.messurementSelected.name;
            };
            _.each(vm.subsidiaries, function(sub) {
                if (sub.selected) {
                    var productBySubsidiary = {
                        dateCreated: new Date().getTime(),
                        quantity: sub.cantidad,
                        subsidiary: sub,
                        active: true
                    };
                    vm.productBySubsidiaries.push(productBySubsidiary);
                };
            });
            _getBrands();
            _getLines();
            vm.wizard = 2;
        };

        vm.wiz3 = function() {
            vm.wizard = 3;
        };

        vm.addCuentaContableSale = function(){
            if (vm.cuentaContableSale !== undefined && vm.cuentaContableSale.id !== undefined) {
                vm.cuentasContablesForProductSale.push(vm.cuentaContableSale);
            };
        };

        vm.removeCCSale = function(cc){
            vm.cuentasContablesForProductSale = _.filter(vm.cuentasContablesForProductSale, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.addCuentaContableConsume = function(){
            if (vm.cuentaContableConsume !== undefined && vm.cuentaContableConsume.id !== undefined) {
                vm.cuentasContablesForProductConsume.push(vm.cuentaContableConsume);
            };
        };

        vm.removeCCConsume = function(cc){
            vm.cuentasContablesForProductConsume = _.filter(vm.cuentasContablesForProductConsume, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.addCuentaContableFinished = function(){
            if (vm.cuentaContableFinished !== undefined && vm.cuentaContableFinished.id !== undefined) {
                vm.cuentasContablesForProductFinished.push(vm.cuentaContableFinished);
            };
        };

        vm.removeCCFinished = function(cc){
            vm.cuentasContablesForProductFinished = _.filter(vm.cuentasContablesForProductFinished, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.addCuentaContableCost = function(){
            if (vm.cuentaContableCost !== undefined && vm.cuentaContableCost.id !== undefined) {
                vm.cuentasContablesForProductCost.push(vm.cuentaContableCost);
            };
        };

        vm.removeCCCost = function(cc){
            vm.cuentasContablesForProductCost = _.filter(vm.cuentasContablesForProductCost, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.getGroups = function() {
            if (vm.selectedLine !== null && vm.selectedLine !== undefined) {
                groupService.getGroupsByLineLazy(vm.selectedLine.id).then(function(response) {
                    vm.groups = response;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            };
        };

        vm.getSubGroups = function() {
            if (vm.selectedGroup !== null && vm.selectedGroup !== undefined) {
                subgroupService.getSubGroupsByGroupLazy(vm.selectedGroup.id).then(function(response) {
                    vm.subGroups = response;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            };
        };

        vm.createBrand = function() {
            vm.newBrand = {
                userClient: $localStorage.user.subsidiary.userClient,
                code: vm.brands.length + 1,
                active: true
            };
        };

        vm.createLine = function() {
            vm.newLine = {
                userClient: $localStorage.user.subsidiary.userClient,
                code: vm.lineas.length + 1,
                active: true,
                groupLines:[]
            };
        };

        vm.createGroup = function() {
            vm.newGroup = {
                line: vm.selectedLine,
                code: vm.groups.length + 1,
                active: true,
                products: []
            };
        };

        vm.createSubGroup = function() {
            vm.newSubGroup = {
                groupLine: vm.selectedGroup,
                code: vm.subGroups.length + 1,
                active: true,
                subGroups: []
            };
        };

        vm.saveNewBrand = function() {
            brandService.create(vm.newBrand).then(function(response) {
                vm.brands.push(response);
                vm.product.brand = response;
                vm.newBrand = undefined;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveNewLine = function() {
            lineService.create(vm.newLine).then(function(response) {
                vm.lineas.push(response);
                vm.selectedLine = response;
                vm.newLine = undefined;
                vm.groups = [];
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveNewGroup = function() {
            groupService.create(vm.newGroup).then(function(response) {
                vm.groups.push(response);
                vm.selectedGroup = response;
                vm.newGroup = undefined;
                vm.subGroups = [];
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveNewSubGroup = function() {
            subgroupService.create(vm.newSubGroup).then(function(response) {
                vm.subGroups.push(response);
                vm.product.subgroup = response;
                vm.newSubGroup = undefined;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveProduct = function() {
            var validationError = utilStringService.isAnyInArrayStringEmpty([vm.product.name]);
            if (validationError) {
                vm.error = 'Debe ingresar Nombre del producto';
            } else {
                vm.loading = true;
                createProduct();
            };
        };

        // NEW PRODUCT -------------------- END

        //Init Controler Code
        (function initController() {
            _activate();
        })();
});
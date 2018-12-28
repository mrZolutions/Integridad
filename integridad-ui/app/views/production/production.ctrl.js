'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # ProductionCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('ProductionCtrl', function( _, $rootScope, $location, utilStringService, $localStorage,
                                            providerService, productService, warehouseService, authService,
                                            cashierService, cellarService, utilSeqService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.seqNumber = undefined;
    vm.warehouse = undefined;
    vm.providerSelected = undefined;
    vm.warehouseName = undefined;
    vm.warehouseSubsidiaryName = undefined;

    vm.prices = [
        { name: 'EFECTIVO', cod: 'cashPercentage'}, { name: 'MAYORISTA', cod: 'majorPercentage'},
        { name: 'CREDITO', cod: 'creditPercentage'}, { name: 'TARJETA', cod: 'cardPercentage'}
    ];
    
    function _activate() {
        vm.warehouseList = undefined;
        vm.dateCellar = undefined;
        vm.aux = undefined;
        vm.loading = true;
        vm.priceType = vm.prices[0];
        vm.success = undefined;
        vm.error = undefined;
        vm.impuestosTotales = [];
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
        vm.usrCliId = $localStorage.user.subsidiary.userClient.id
        warehouseService.getAllWarehouseByUserClientId(vm.usrCliId).then(function(response) {
            vm.warehouseList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    function _getSeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.whNumberSeq) + 1;
        vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
        vm.seqNumber = vm.seqNumberSecondPart;
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
            items: []
        };
    };

    vm.findProviders = function(warehouse) {
        vm.loading = true;
        vm.success = undefined;
        vm.error = undefined;
        vm.warehouse = warehouse;
        vm.warehouseSelected = true;
        vm.warehouseName = warehouse.nameNumber;
        vm.warehouseSubsidiaryName = warehouse.subsidiary.name;
        vm.providerList = undefined;
        providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
            vm.providerList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.cancelFindProviders = function() {
        vm.warehouseSelected = undefined;
    };

    vm.providerSelect = function(provider) {
        vm.loading = true;
        vm.success = undefined;
        vm.error = undefined;
        vm.dateCellar = new Date();
        vm.warehouseSelected = true;
        vm.providerSelected = provider;
        _getSeqNumber();
        _initializeCellar();
        vm.loading = false;
        var today = new Date();
        $('#pickerDateEnterCellar').data("DateTimePicker").date(today);
    };

    function _filterProduct() {
        vm.totalPages = 0;
        var variable = vm.searchText? vm.searchText : null;
        productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, variable).then(function(response) {
            vm.loading = false;
            vm.totalPages = response.totalPages;
            vm.productList = [];
            for (var i = 0; i < response.content.length; i++) {
                var productFound = _.find(vm.cellar.items, function(detail) {
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

    vm.addProduct = function() {
        vm.indexDetail = undefined;
        vm.loading = true;
        vm.errorQuantity = undefined;
        vm.page = 0;
        vm.searchText = undefined;
        _filterProduct();
    };

    vm.getCost = function(textCost, averageCost) {
        var aC = parseFloat(textCost)
        var cost = aC * averageCost;
        return (cost).toFixed(4);
    };

    vm.selectProductToAdd = function(productSelect) {
        if (productSelect.productType.code === 'SER') {
            productSelect.quantity = 1;
        };
        vm.productToAdd = angular.copy(productSelect);
        var costEachCalculated = vm.getCost('1.'+ productSelect[vm.priceType.cod], productSelect.averageCost);
        vm.productToAdd.costEachCalculated = costEachCalculated;
        vm.quantity = 1;
    };

    function _getTotalSubtotal() {
        vm.cellar.subTotal = 0;
        vm.cellar.iva = 0;
        vm.cellar.ivaZero = 0;
        vm.cellar.ice = 0;
        vm.cellar.baseTaxes = 0;
        vm.cellar.baseNoTaxes = 0;
        var discountWithIva = 0;
        var discountWithNoIva = 0;
        _.each(vm.cellar.items, function(detail) {
            vm.cellar.subTotal = (parseFloat(vm.cellar.subTotal) + parseFloat(detail.total)).toFixed(4);
            var tot = detail.total;
            if (vm.cellar.discountPercentage) {
                tot = (parseFloat(detail.total) - (parseInt(vm.cellar.discountPercentage)/100)*(parseFloat(detail.total))).toFixed(4);
            };
            if (detail.product.iva) {
                vm.cellar.baseTaxes += parseFloat(detail.total);
                vm.cellar.iva = (parseFloat(vm.cellar.iva) + (parseFloat(tot) * 0.12)).toFixed(4);
                if (vm.cellar.discountPercentage) {
                    discountWithIva = (parseFloat(discountWithIva) + ((parseInt(vm.cellar.discountPercentage)/100)*detail.total)).toFixed(4);
                };
            } else {
                vm.cellar.baseNoTaxes += parseFloat(detail.total);
                vm.cellar.ivaZero = (parseFloat(vm.cellar.ivaZero) + parseFloat(tot)).toFixed(4);
                if (vm.cellar.discountPercentage) {
                    discountWithNoIva = (parseFloat(discountWithNoIva) + ((parseInt(vm.cellar.discountPercentage)/100)*detail.total)).toFixed(4);
                };
            };
            if (detail.product.ice) {
                vm.cellar.ice = (parseFloat(vm.cellar.ice) + (parseFloat(tot) * 0.10)).toFixed(4);
            };
        });
        if (vm.cellar.discountPercentage) {
            vm.cellar.discount = ((parseInt(vm.cellar.discountPercentage)/100)*vm.cellar.subTotal).toFixed(4);
        } else {
            vm.cellar.discount = 0;
        };
        vm.impuestoICE.base_imponible = vm.cellar.subTotal;
        vm.impuestoIVA.base_imponible = vm.cellar.baseTaxes;
        vm.impuestoIVAZero.base_imponible = vm.cellar.baseNoTaxes;
        vm.impuestoICE.valor = vm.cellar.ice;
        vm.impuestoIVA.valor = vm.cellar.iva;
        vm.impuestoIVAZero.valor = 0;
        vm.cellar.baseTaxes = (vm.cellar.baseTaxes - discountWithIva).toFixed(4);
        vm.cellar.baseNoTaxes = (vm.cellar.baseNoTaxes - discountWithNoIva).toFixed(4);
        vm.cellar.total = (parseFloat(vm.cellar.baseTaxes) 
            + parseFloat(vm.cellar.baseNoTaxes)
            + parseFloat(vm.cellar.iva)
            + parseFloat(vm.cellar.ice)).toFixed(4);
    };

    vm.acceptProduct = function(closeModal) {
        var detail = {
            product: angular.copy(vm.productToAdd),
            quantity: vm.quantity,
            costEach: vm.productToAdd.costEachCalculated,
            total: (parseFloat(vm.quantity) * parseFloat(vm.productToAdd.costEachCalculated)).toFixed(4)
        };
        if (vm.indexDetail !== undefined) {
            vm.cellar.items[vm.indexDetail] = detail;
        } else {
            vm.cellar.items.push(detail);
        };
        vm.productToAdd = undefined;
        vm.quantity = undefined;
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
        vm.productToAdd= detail.product;
        vm.quantity= detail.quantity
    };
  
    vm.removeDetail = function(index) {
        vm.cellar.items.splice(index,1);
    };

    vm.range = function() {
        return new Array(vm.totalPages);
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

    vm.saveToCellar = function(cellar) {
        vm.loading = true;
        vm.userValid = $localStorage.user.userType.code;
        vm.cellar.dateBill = $('#pickerDateBill').data("DateTimePicker").date().toDate().getTime();
        vm.cellar.dateCellar = $('#pickerDateEnterCellar').data("DateTimePicker").date().toDate().getTime();
        vm.cellar.whNumberSeq = vm.seqNumber;
        vm.cellar.detailsCellar = [];
        vm.cellar.detailsKardex = [];
        if (vm.userValid === 'EMP') {
            vm.cellar.statusIngreso = 'PENDIENTE';
        } else {
            vm.cellar.statusIngreso = 'INGRESADO';
        };
        _.each(vm.cellar.items, function(item) {
            var detail = {
                product: item.product, 
                quantity: item.quantity,
                costEach: item.costEach,
                total: item.total
            };
            var kardex = {
                cellar: cellar.id,
                product: item.product,
                codeWarehouse: vm.warehouse.codeWarehouse,
                dateRegister: $('#pickerDateEnterCellar').data("DateTimePicker").date().toDate().getTime(),
                details: 'INGRESO A BODEGA',
                observation: 'INGRESO',
                prodCostEach: item.costEach,
                prodName: item.product.name,
                prodQuantity: item.quantity,
                prodTotal: item.total
            };
            vm.cellar.detailsCellar.push(detail);
            vm.cellar.detailsKardex.push(kardex);
        });
        cellarService.create(cellar).then(function(respCellar) {
            vm.cellar = respCellar;
            $localStorage.user.cashier.whNumberSeq = vm.cellar.whNumberSeq;
            vm.cellarCreated = true;
            vm.success = 'Productos Ingresados con Exito' + vm.cellar.statusIngreso;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
        _activate();
    };

    vm.cancelCellar = function() {
        vm.warehouseSelected = undefined;
        vm.providerSelected = undefined;
    };

    (function initController() {
        _activate();
    })();
});
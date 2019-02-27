'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ContableCtrl
 * @description
 * # ContableCtrl
 * Controller of the template contable.tpl.html
 */
angular.module('integridadUiApp')
    .controller('ContableCtrl', function(_, $localStorage, cuentaContableService, debtsToPayService, 
                                            utilSeqService, creditsDebtsService, contableService, $location) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.total = undefined;
    vm.cuenta = undefined;
    vm.dailybookCgNew = undefined;
    vm.dailyCgSeq = undefined;
    vm.dailyCgStringSeq = undefined;
    vm.cuentaContableList = undefined;
    vm.aux = undefined;
    vm.subIva = undefined;
    vm.subTotalDoce = undefined;
    vm.subTotalCero = undefined;

    vm.typeBookList = [
        {code: '1', identify: 'CG', type: 'CONTABILIDAD GENERAL'},
        {code: '2', identify: 'EG', type: 'COMPROBANTE DE EGRESO'},
        {code: '3', identify: 'IN', type: 'COMPROBANTE DE INGRESO'},
        {code: '4', identify: 'CC', type: 'CUENTA POR COBRAR'},
        {code: '5', identify: 'CP', type: 'CUENTA POR PAGAR'},
        {code: '6', identify: 'FV', type: 'FACTURACIÓN - VENTAS'},
        {code: '7', identify: 'RT', type: 'RETENCIONES'},
        {code: '8', identify: 'NC', type: 'NOTA DE CRÉDITO'},
        {code: '9', identify: 'ND', type: 'NOTA DE DÉBITO'},
        {code: '10', identify: 'IV', type: 'INVENTARIO'},
        {code: '11', identify: 'TR', type: 'TRANSFERENCIA'}
    ];

    function _activate() {
        vm.today = new Date();
        vm.contable = undefined;
        vm.dailybookCgList = undefined;
        vm.cuentaContableList = undefined;
        vm.usrCliId = $localStorage.user.subsidiary.userClient.id;
        vm.contableList = [];
        vm.loading = true;
        vm.success = undefined;
        vm.selectedTypeBook = undefined;
        vm.error = undefined;
        vm.loading = false;
    };

    function _getDailyCgSeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.dailyCgNumberSeq) + 1;
        vm.dailyCgSeq = vm.numberAddedOne;
        vm.dailyCgStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    function _initializeDailybookCg() {
        vm.dailybookCg = {
            userClient: vm.usrCliId,
            userIntegridad: $localStorage.user,
            subsidiary: $localStorage.user.subsidiary,
            items: []
        };
    };

    vm.loadTypeDailybook = function() {
        switch (vm.dailybookCg.codeTypeContab) {
            case '1':
                vm.selectedTypeBook = '1';
                contableService.getDailybookCgByUserClientId(vm.usrCliId).then(function(response) {
                    vm.dailybookCgList = response;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
                break;
        };
    };

    vm.addDailybookCg = function() {
        vm.dailybookCgNew = true;
        vm.success = undefined;
        vm.loading = true;
        _getDailyCgSeqNumber();
        _initializeDailybookCg();
        vm.loading = false;
        var today = new Date();
        $('#pickerDateRecordBook').data("DateTimePicker").date(today);
    };

    vm.getCuentasContables = function() {
        if (vm.cuenta === true) {
            cuentaContableService.getCuentaContableByUserClient(vm.usrCliId).then(function(response) {
                vm.cuentaContableList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        } else {
            vm.cuentaContableList = undefined;
        };
    };

    vm.selectCuentas = function(cuenta) {
        vm.item = undefined;
        vm.item = {
            cta_contable: cuenta.id,
            codigo_contable: cuenta.code,
            desc_contable: cuenta.description,
            nomb_contable: cuenta.name
        };
    };

    vm.addItem = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCg.items.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        vm.dailybookCg.items.push(vm.item);
        vm.item = undefined;
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
    };

    vm.editItemTaxes = function(index) {
        vm.item = angular.copy(vm.dailybookCg.items[index]);
        vm.indexEdit = index;
    };
  
    vm.deleteItemTaxes = function(index) {
        vm.dailybookCg.items.splice(index, 1);
    };

    vm.getTotalDebito = function() {
        var totalDebito = 0;
        if (vm.dailybookCg) {
            _.each (vm.dailybookCg.items, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebito = (parseFloat(totalDebito) + parseFloat(detail.valor)).toFixed(2);
                };
            });
        };
        vm.saldoDebito = totalDebito;
        return totalDebito;
    };
  
    vm.getTotalCredito = function() {
        var totalCredito = 0;
        if (vm.dailybookCg) {
            _.each(vm.dailybookCg.items, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCredito = (parseFloat(totalCredito) + parseFloat(detail.valor)).toFixed(2);
                };
            });
        };
        vm.saldoCredito = totalCredito;
        return totalCredito;
    };
  
    vm.getTotalSaldo = function() {
        var totalSaldo = 0;
        totalSaldo = (parseFloat(vm.saldoCredito) - parseFloat(vm.saldoDebito)).toFixed(2);
        return totalSaldo;
    };

    vm.exit = function() {
        $location.path('/home');
    };

    (function initController() {
        _activate();
    })();
});
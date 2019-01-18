'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebtsToPayCtrl
 * @description
 * # ContableCtrl
 * Controller of the template contable.tpl.html
 */
angular.module('integridadUiApp')
    .controller('ContableCtrl', function(_, $localStorage, providerService, cuentaContableService, debtsToPayService, 
                                            utilSeqService, creditsDebtsService, $location) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;

    function _activate() {
        vm.today = new Date();
        vm.contable = undefined;
        vm.dairyBookList = undefined;
        vm.contableList = [];
        cuentaContableService.getAll().then(function(response) {
            vm.cuentaContableList = response;
        });
    };

    function _getDailySeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.dailyNumberSeq) + 1;
        vm.dailySeqNumber = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    vm.exit = function() {
        $location.path('/home');
    };

    (function initController() {
        _activate();
    })();
});
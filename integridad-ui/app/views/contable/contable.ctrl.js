'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebtsToPayCtrl
 * @description
 * # ContableCtrl
 * Controller of the template contable.tpl.html
 */
angular.module('integridadUiApp')
    .controller('contableCtrl', function(_, $localStorage, providerService, cuentaContableService, debtsToPayService, 
                                            utilSeqService, creditsDebtsService, paymentDebtsService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;

    (function initController() {
        _activate();
    })();
});
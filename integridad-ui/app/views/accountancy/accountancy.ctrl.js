'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebtsToPayCtrl
 * @description
 * # AccountancyCtrl
 * Controller of the template accountancy.tpl.html
 */
angular.module('integridadUiApp')
    .controller('accountancyCtrl', function(_, $localStorage, providerService, cuentaContableService, debtsToPayService, 
                                            utilSeqService, creditsDebtsService, paymentDebtsService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;

    (function initController() {
        _activate();
    })();
});
'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasPagarCtrl
 * @description
 * # CuentasPagarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasPagarCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage,
                                                cuentasService, productService, authService, billService, $window, cashierService, creditService, utilSeqService){
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
});

'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasContablesCtrl
 * @description
 * # CreditNoteCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasContablesCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage,
                                                cuentasService, productService, authService, billService, $window, cashierService, creditService, utilSeqService){
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.cuentasContablesList = undefined;
    vm.cuetaSelected = undefined;

    vm.accountType = [
      'B', 'S', 'MP', 'CM', 'RG', 'TAE',
    ];

    function _activate(){
      vm.loading = true;
      vm.cuentaSelected = undefined;
      vm.user = $localStorage.user;

      cuentasService.getLazyByUserClientId(vm.user.subsidiary.userClient.id).then(function (response) {
        vm.cuentasContablesList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function _initializeCuenta(){
      vm.cuentaSelected = {
        userClient: vm.user.subsidiary.userClient
      };
    }

    function create(){
      vm.loading = true;
      cuentasService.create(vm.cuentaSelected).then(function (response) {
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.cuentaCreate = function(){
      _initializeCuenta();
    };

    vm.save = function(){
      create();
    };

    vm.cancel = function(){
      _activate();
    };

    (function initController() {
      _activate();
    })();
});

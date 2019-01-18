'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasContablesCtrl
 * @description
 * # CuentasContablesCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasContablesCtrl', function( _, $location, $localStorage, cuentaContableService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.cuentasContablesList = undefined;
    vm.cuetaSelected = undefined;

    function _activate() {
      vm.loading = true;
      vm.error = undefined;
      vm.success = undefined;
      vm.cuentaSelected = undefined;
      vm.user = $localStorage.user;
      cuentaContableService.getLazyByUserClientId(vm.user.subsidiary.userClient.id).then(function(response) {
        vm.cuentasContablesList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function _initializeCuenta() {
      vm.cuentaSelected = {
        userClient: vm.user.subsidiary.userClient
      };
    };

    function create() {
      vm.loading = true;
      cuentaContableService.create(vm.cuentaSelected).then(function(response) {
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function update() {
      cuentaContableService.update(vm.cuentaSelected).then(function(response) {
        _activate();
        if (vm.cuentaSelected.active) {
          vm.success = 'Registro actualizado con exito';
        } else {
          vm.success = 'Registro eliminado con exito';
        };
        vm.error = undefined;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cuentaEdit = function(cuenta) {
      vm.error = undefined;
      vm.success = undefined;
      vm.cuentaSelected = cuenta;
    };

    vm.remove = function() {
      vm.cuentaSelected.active = false;
      update();
    };

    vm.cuentaCreate = function() {
      _initializeCuenta();
    };

    vm.save = function() {
      if (vm.cuentaSelected.id) {
        update();
      } else {
        create();
      };
    };

    vm.cancel = function() {
      _activate();
    };

    vm.exit = function() {
      $location.path('/home');
    };

    (function initController() {
      _activate();
    })();
});

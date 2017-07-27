'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ClientsCtrl
 * @description
 * # ClientsCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('ClientsCtrl', function (utilStringService, utilValidationService, clientService) {
    var vm = this;

    vm.loading = false;
    vm.client={};

    function _activate(){
    }

    vm.save = function(){
      var validationError = utilValidationService.isAnyInArrayStringEmpty([
        vm.client.name, vm.client.identification, vm.client.codConta
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos, una identificacion y el Codigo de Contabilidad';
      } else {
        vm.loading = true;
        clientService.create(vm.client).then(function (response) {
          vm.client = {};
          vm.loading = false;
          vm.error = undefined;
          vm.success = 'Resgistro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta';
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      }

    };

    vm.cancel=function(){
      vm.client = {};
      vm.success=undefined;
      vm.error=undefined
    };

    (function initController() {
      _activate();
    })();

  });

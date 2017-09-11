'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ClientsCtrl
 * @description
 * # ClientsCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('ClientsCtrl', function (utilStringService, utilValidationService, countryListService, clientService) {
    var vm = this;

    vm.loading = false;
    vm.client=undefined;
    vm.countryList = countryListService.getCountryList();
    vm.citiesList = countryListService.getCitiesEcuador();

    vm.clientList = undefined;

    function _activate(){
      vm.loading = true;
      clientService.getLazy().then(function (response) {
        vm.clientList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function create(){
      clientService.create(vm.client).then(function (response) {
        vm.client=undefined;
        _activate();
        vm.error = undefined;
        vm.success = 'Resgistro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function update(isRemove){
      clientService.update(vm.client).then(function (response) {
        vm.client=undefined;
        _activate();
        vm.error = undefined;
        if(isRemove){
          vm.success = 'Resgistro eliminado con exito';
        } else {
          vm.success = 'Resgistro actualizado con exito';
        }
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.clientCreate = function(){
      vm.success=undefined;
      vm.error=undefined
      vm.client={country:'Ecuador', city:'Quito'};
    };

    vm.clientEdit = function(client){
      vm.success=undefined;
      vm.error=undefined
      vm.client=angular.copy(client);
    };

    vm.validateEcuador = function(){
      if(vm.client.country !== 'Ecuador'){vm.client.city = undefined}
    };

    vm.save = function(){
      var validationError = utilValidationService.isAnyInArrayStringEmpty([
        vm.client.name, vm.client.identification, vm.client.codConta
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos, una identificacion y el Codigo de Contabilidad';
      } else {
        vm.loading = true;
        if(vm.client.id === undefined){
          create();
        }else{
          update(false);
        }
      }

    };

    vm.remove = function(){
      vm.client.active = false;
      update(true);
    };

    vm.cancel=function(){
      vm.client=undefined;
      vm.success=undefined;
      vm.error=undefined
    };

    (function initController() {
      _activate();
    })();

  });

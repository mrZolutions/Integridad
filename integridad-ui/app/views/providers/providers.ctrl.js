'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProvidersCtrl', function (_, $localStorage, $location, providerService, utilStringService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.userData = $localStorage.user;
    vm.provider = undefined;
    vm.providerList = undefined;
    vm.providerType = [
      'PROVEEDOR TIPO 1',
      'PROVEEDOR TIPO 2',
      'PROVEEDOR TIPO 3',
      'PROVEEDOR TIPO 4',
    ];

    function _activate(){
      vm.provider = undefined;
      vm.providerList = [];
      providerService.getLazyByUserClientId(vm.userData.subsidiary.userClient.id).then(function(response){
        vm.providerList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function create(){
      providerService.create(vm.provider).then(function (response) {
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function update(){
      providerService.update(vm.provider).then(function (response) {
        if(vm.provider.active){
          vm.success = 'Registro actualizado con exito';
        } else {
          vm.success = 'Registro eliminado con exito';
        }
        _activate();
        vm.error = undefined;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.providerCreate = function(){
      vm.error = undefined;
      vm.success = undefined;
      vm.provider = {
        codeIntegridad: vm.providerList.length + 1,
        active: true,
        userClient: vm.userData.subsidiary.userClient
      };
    };

    vm.editProvider = function(providerEdit){
      vm.error = undefined;
      vm.success = undefined;
      vm.provider = providerEdit;
    };

    vm.disableSave = function(){
      if(vm.provider){
        return utilStringService.isAnyInArrayStringEmpty([
          vm.provider.codeIntegridad,
          vm.provider.ruc,
          vm.provider.name,
          vm.provider.razonSocial,
          vm.provider.country,
          vm.provider.city,
          vm.provider.address1,
          vm.provider.contact,
          vm.provider.providerType
        ]);
      }
    };

    vm.cancel = function(){
      vm.provider = undefined;
    };

    vm.register = function(){
      if(vm.provider.id){
        update();
      } else {
        create();
      }
    };

    vm.remove = function(){
      vm.provider.active=false;
      update();
    };

    (function initController() {
      _activate();
    })();
  });

'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProductsCtrl', function ($localStorage, $location, productService, utilStringService, projectService, $routeParams) {
    var vm = this;

    vm.loading = false;
    vm.product = undefined;
    vm.productList = undefined;
    vm.subsidiaryId = undefined;

    function _activate(){
      vm.loading = true;
      if($routeParams.subsidiaryId){
        productService.getLazyBySusidiaryId($routeParams.subsidiaryId).then(function (response) {
          vm.productList = response;
          vm.loading = false;
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      } else {
        productService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function (response) {
          vm.productList = response;
          vm.loading = false;
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      }
    }

    function create(){
      productService.create(vm.product).then(function (response) {
        vm.product=undefined;
        _activate();
        vm.error = undefined;
        vm.success = 'Resgistro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function update(isRemove){
      productService.update(vm.product).then(function (response) {
        vm.product=undefined;
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

    vm.productCreate = function(){
      projectService.getById($localStorage.user.subsidiary.userClient.id).then(function (response) {
        vm.subsidiaries = response.subsidiaries;
        vm.success=undefined;
        vm.error=undefined
        vm.product={
          userClient: $localStorage.user.subsidiary.userClient
        };
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.save = function(){
      var validationError = utilStringService.isAnyInArrayStringEmpty([
        vm.product.name
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombre del producto';
      } else {
        vm.loading = true;
        if(vm.product.id === undefined){
          create();
        }else{
          update(false);
        }
      }
    };

    vm.remove = function(){
      vm.product.active = false;
      update(true);
    };

    vm.editProduct = function(productEdit){
      vm.loading = true;
      productService.getById(productEdit.id).then(function (response) {
        vm.loading = false;
        vm.product = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancel=function(){
      vm.product=undefined;
      vm.success=undefined;
      vm.error=undefined;
    };

    (function initController() {
      _activate();
    })();
  });

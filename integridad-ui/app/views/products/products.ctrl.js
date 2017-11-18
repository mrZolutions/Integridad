'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProductsCtrl', function (_, $localStorage, $location, productService, utilStringService, projectService,
    subsidiaryService, productTypeService, messurementListService, $routeParams) {
    var vm = this;

    vm.loading = false;
    vm.product = undefined;
    vm.productList = undefined;
    vm.subsidiaryId = undefined;
    vm.productTypes = undefined;
    vm.messurements = undefined;
    vm.subsidiaries = undefined;
    vm.wizard = 0;

    function _activate(){
      vm.loading = true;
      vm.messurements = messurementListService.getMessurementList();
      productTypeService.getproductTypes().then(function(response){
        vm.productTypes = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });

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

    function _getSubsidiaries(){
      subsidiaryService.getByProjectId($localStorage.user.subsidiary.userClient.id).then(function (response) {
        vm.subsidiaries = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
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

    function _getSubsidiarie(){
      if($routeParams.subsidiaryId){
        subsidiaryService.getById($routeParams.subsidiaryId).then(function(response){
          vm.subsidiaries = [response];
          vm.success=undefined;
          vm.error=undefined
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      } else {
        projectService.getById($localStorage.user.subsidiary.userClient.id).then(function (response) {
          vm.subsidiaries = response.subsidiaries;
          vm.success=undefined;
          vm.error=undefined
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      }
    }

    vm.productCreate = function(){
      _getSubsidiaries();
      vm.success=undefined;
      vm.error=undefined
      vm.wizard = 1;
      vm.product={
        productBySubsidiaries: [],
        // userClient: response.userClient
      };
      // if($routeParams.subsidiaryId){
      //   subsidiaryService.getById($routeParams.subsidiaryId).then(function(response){
      //     vm.subsidiaries = [response];
      //     vm.success=undefined;
      //     vm.error=undefined
      //     vm.product={
      //       userClient: response.userClient
      //     };
      //   }).catch(function (error) {
      //     vm.loading = false;
      //     vm.error = error.data;
      //   });
      // } else {
      //   projectService.getById($localStorage.user.subsidiary.userClient.id).then(function (response) {
      //     vm.subsidiaries = response.subsidiaries;
      //     vm.success=undefined;
      //     vm.error=undefined
      //     vm.product={
      //       userClient: $localStorage.user.subsidiary.userClient
      //     };
      //   }).catch(function (error) {
      //     vm.loading = false;
      //     vm.error = error.data;
      //   });
      // }
    };

    vm.changeSub = function(subsidiary){
      if(subsidiary.selected){
        subsidiary.cantidad = 0;
      } else {
        subsidiary.cantidad = undefined;
      }
    };

    vm.wiz2 = function(){
      _.each(vm.subsidiaries, function(sub){
        if(sub.selected){
          var productBySubsidiary = {
            dateCreated: new Date().getTime(),
            quantity: sub.cantidad,
            subsidiary: sub
          };

          vm.product.productBySubsidiaries.push(productBySubsidiary);

        }
      });

      console.log(vm.product);
      vm.wizard = 2;
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
      _getSubsidiarie();
      productService.getById(productEdit.id).then(function (response) {
        vm.loading = false;
        vm.product = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancel=function(){
      vm.wizard = 0;
      vm.product=undefined;
      vm.success=undefined;
      vm.error=undefined;
    };

    (function initController() {
      _activate();
    })();
  });

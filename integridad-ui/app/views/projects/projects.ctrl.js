'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProjectsCtrl', function ($localStorage, $location, projectService, utilStringService) {
    var vm = this;

    vm.loading = false;
    vm.project = undefined;
    vm.projectList = undefined;

    function _activate(){
      vm.loading = true;
      projectService.getLazy().then(function (response) {
        vm.projectList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function create(){
      projectService.create(vm.project).then(function (response) {
        vm.project=undefined;
        _activate();
        vm.error = undefined;
        vm.success = 'Resgistro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function update(isRemove){
      projectService.update(vm.project).then(function (response) {
        vm.project=undefined;
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

    vm.projectCreate = function(){
      vm.success=undefined;
      vm.error=undefined
      vm.project={};
    };

    vm.save = function(){
      var validationError = utilStringService.isAnyInArrayStringEmpty([
        vm.project.name, vm.project.threeCode, vm.project.codeIntegridad, vm.project.ruc
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos, ruc y sus Codigos';
      } else {
        vm.loading = true;
        if(vm.project.id === undefined){
          create();
        }else{
          update(false);
        }
      }

    };

    vm.remove = function(){
      vm.project.active = false;
      update(true);
    };

    vm.cancel=function(){
      vm.project=undefined;
      vm.success=undefined;
      vm.error=undefined
    };

    (function initController() {
      _activate();
    })();
  });

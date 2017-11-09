'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProjectsCtrl', function ($localStorage, $location, projectService, utilStringService, validatorService) {
    var vm = this;

    vm.loading = false;
    vm.project = undefined;
    vm.projectList = undefined;
    vm.subsidiary = undefined;
    vm.cashier = undefined;

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
      vm.project={subsidiaries:[]};
      vm.project.initialActivityDate = $('#pickerInitialDate').data("DateTimePicker").date().toDate().getTime();
    };

    vm.save = function(){
      vm.project.initialActivityDate = $('#pickerInitialDate').data("DateTimePicker").date().toDate().getTime();
      var validationError = utilStringService.isAnyInArrayStringEmpty([
        vm.project.name, vm.project.threeCode, vm.project.codeIntegridad, vm.project.ruc
      ]);

      var idValid = validatorService.isRucValid(vm.project.ruc);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos, ruc y sus Codigos';
      } else if(idValid){
        vm.loading = true;
        if(vm.project.id === undefined){
          create();
        }else{
          update(false);
        }
      } else {
        vm.error = 'RUC invalido';
      }

    };

    vm.remove = function(){
      vm.project.active = false;
      update(true);
    };

    vm.addSubsidiary = function(){
      vm.subsidiary={
        cashiers: []
      }
    };

    vm.removeCashier = function(){
      vm.cashier.active = false;
    };

    vm.removeSubsidiary = function(){
      vm.subsidiary.active = false;
      // update(true);
    };

    vm.editProject = function(projectEdit){
      projectService.getById(projectEdit.id).then(function (response) {
        vm.project = response;
        $('#pickerInitialDate').data("DateTimePicker").date(new Date(vm.project.initialActivityDate));
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.saveSubsidiary = function(){
      if(vm.subsidiary.id === undefined && vm.subsidiary.name !== undefined){
        vm.subsidiary.warehouses = ['bodega 1', 'bodega 2']
        vm.subsidiary.active = true;
        vm.subsidiary.dateCreated = new Date().getTime();
        console.log('********************',vm.subsidiary);
        vm.project.subsidiaries.push(vm.subsidiary);
      }
      vm.subsidiary = undefined;
      vm.updSubsi = false;
    };

    vm.saveCashier = function(){
      vm.cashier.active = true;
      vm.cashier.dateCreated = new Date().getTime();
      vm.cashier.threeCode = '006';
      vm.cashier.billNumberSeq = 1;

      vm.subsidiary.cashiers.push(vm.cashier);
      // vm.cashier = undefined;
      console.log('----',vm.subsidiary)
    };

    vm.cancel=function(){
      vm.updSubsi = false;
      vm.project=undefined;
      vm.success=undefined;
      vm.error=undefined;
    };

    vm.cancelSubisdiary=function(){
      vm.updSubsi = false;
      vm.subsidiary=undefined;
      vm.success=undefined;
      vm.error=undefined;
    };

    vm.products=function(subsidiary){
      $location.path('/products/products/'+subsidiary.id);
    };

    (function initController() {
      _activate();
    })();
  });

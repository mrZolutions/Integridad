'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:UsersCtrl
 * @description
 * # UsersCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('UsersCtrl', function (utilStringService, userTypeService, authService, projectService, subsidiaryService, validatorService)  {
    var vm = this;

    vm.loading = false;
    vm.userIntegridad = undefined;
    vm.project = undefined;

    function _activate(){
      vm.loading = true;
      authService.getLazy().then(function (response) {
        vm.userList = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });

      getProjects();
    }

    function getProjects(){
      projectService.getLazy().then(function (response) {
        vm.projectList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function create(){
      vm.loading = true;
      authService.registerUser(vm.userIntegridad).then(function (response) {
        vm.loading = false;
        vm.error = undefined;
        vm.success = 'Resgistro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta';
        vm.userIntegridad=undefined;
        _activate();
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function update(){
      vm.loading = true;
      authService.updateUser(vm.userIntegridad).then(function (response) {
        vm.loading = false;
        vm.error = undefined;
        vm.success = 'Resgistro actualizado con exito';
        vm.userIntegridad=undefined;
        _activate();
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.getSubsidiaries = function(){
      subsidiaryService.getByProjectId(vm.project.id).then(function (response) {
        vm.subsidiaryList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.userCreate = function(){
      vm.success=undefined;
      vm.error=undefined;
      vm.userIntegridad = {};
    };

    vm.userEdit = function(user){
      vm.success=undefined;
      vm.error=undefined
      vm.userIntegridad=angular.copy(user);

      if(vm.userIntegridad.cedula !== undefined && vm.userIntegridad.cedula !== null){
        vm.typeId = 'Cedula';
      } else {
        vm.typeId = 'Ruc';
      }

      getProjects();
      vm.project = vm.userIntegridad.subsidiary.userClient;
      vm.getSubsidiaries();

      $('#pickerBirthday').data("DateTimePicker").date(new Date(vm.userIntegridad.birthDay));
    };

    vm.register = function(){
      vm.userIntegridad.birthDay = $('#pickerBirthday').data("DateTimePicker").date().toDate().getTime();
      vm.userIntegridad.email = vm.userIntegridad.email.trim();
      vm.userIntegridad.password = utilStringService.randomString();

      var validationError = utilStringService.isAnyInArrayStringEmpty([
        vm.userIntegridad.email, vm.userIntegridad.password, vm.userIntegridad.firstName,
        vm.userIntegridad.lastName
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos y un email';
      } else {
        validationError = utilStringService.isStringEmpty(vm.userIntegridad.cedula) && utilStringService.isStringEmpty(vm.userIntegridad.ruc);

        if(validationError){
          vm.error = 'Debe ingresar un numero de cedula o ruc';
        }
      }

      var idValid = true;
      if(vm.typeId === 'Cedula'){
        idValid = validatorService.isCedulaValid(vm.userIntegridad.cedula);
      } else if(vm.typeId === 'Ruc'){
        idValid = validatorService.isRucValid(vm.userIntegridad.ruc);
      }

      if(!idValid){
        vm.error = 'Identificacion invalida';
      }else if(!validationError){
        if(vm.userIntegridad.id === undefined){
          create();
        }else{
          vm.userIntegridad.password = '';
          update(false);
        }
      }
    };

    vm.cancel=function(){
      vm.userIntegridad =undefined;
      vm.success=undefined;
      vm.error=undefined
    };

    (function initController() {
      vm.loading = true;
      userTypeService.getUserTypes().then(function (response) {
        vm.userTypes =  response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
      _activate();
    })();

  });

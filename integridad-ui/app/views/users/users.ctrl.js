'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:UsersCtrl
 * @description
 * # UsersCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('UsersCtrl', function (utilStringService, utilValidationService, userTypeService, authService) {
    var vm = this;

    vm.loading = false;
    vm.userIntegridad = {};

    function _activate(){
      vm.loading = true;
      userTypeService.getUserTypes().then(function (response) {
        vm.userTypes =  response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.register = function(){
      vm.userIntegridad.birthDay = $('#pickerBirthday').data("DateTimePicker").date().toDate().getTime();
      vm.userIntegridad.email = vm.userIntegridad.email.trim();
      vm.userIntegridad.password = utilStringService.randomString();

      var validationError = utilValidationService.isAnyInArrayStringEmpty([
        vm.userIntegridad.email, vm.userIntegridad.password, vm.userIntegridad.firstName,
        vm.userIntegridad.lastName
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos y un email';
      } else {
        validationError = utilValidationService.isStringEmpty(vm.userIntegridad.cedula) && utilValidationService.isStringEmpty(vm.userIntegridad.ruc);

        if(validationError){
          vm.error = 'Debe ingresar un numero de cedula o ruc';
        }
      }

      if(!validationError){
        vm.loading = true;
        authService.registerUser(vm.userIntegridad).then(function (response) {
          vm.loading = false;
          vm.error = undefined;
          vm.success = 'Resgistro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta';
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      }
    };

    (function initController() {
      _activate();
    })();

  });

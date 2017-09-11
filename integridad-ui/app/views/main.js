'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('MainCtrl', function ($rootScope, $location, authService, utilValidationService, permissionService, $localStorage) {
    var vm = this;
    vm.loading = false;

    vm.init = true;
    vm.recover = false;
    vm.userIntegridad = {};
    // vm.idTypes=[];

    vm.error = undefined;
    vm.success = undefined;

    function getPermissions(){
      permissionService.getPermissions($localStorage.user.userType).then(function (respnse) {
        $localStorage.permissions = respnse;
        $rootScope.updateMenu();
        vm.loading = false;
        $location.path('/home');
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.login = function(){
      vm.loading = true;
      var user = {email: vm.email, password: vm.password};
      authService.authUser(user).then(function (response) {
        $localStorage.user = response;
        var d = new Date();
        $localStorage.timeloged = d.getTime();
        getPermissions();
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.register = function(){
      vm.userIntegridad.birthDay = $('#pickerBirthday').data("DateTimePicker").date().toDate().getTime();
      vm.userIntegridad.email = vm.userIntegridad.email.trim();
      vm.userIntegridad.password = vm.userIntegridad.password.trim();

      var validationError = utilValidationService.isAnyInArrayStringEmpty([
        vm.userIntegridad.email, vm.userIntegridad.password, vm.userIntegridad.firstName,
        vm.userIntegridad.lastName
      ]);

      if(validationError){
        vm.error = 'Debe ingresar Nombres completos, email y password';
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
          vm.init = true;
          vm.success = 'Resgistro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta';
        }).catch(function (error) {
          vm.loading = false;
          vm.error = error.data;
        });
      }
    };

    vm.recoverPass = function(){
      vm.loading = true;
      var user = {email: vm.email};
      authService.recoverUser(user).then(function (response) {
        vm.loading = false;
        vm.recover = false;
        vm.success = 'Se envio un email a la cuenta registrada con un nuevo Password para ingresar al sistema.';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

  });

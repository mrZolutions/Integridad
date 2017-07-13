'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('MainCtrl', function ($location, authService) {
    var vm = this;
    vm.loading = false;

    vm.init = true;
    vm.userIntegridad = {};

    vm.error = undefined;
    vm.success = undefined;

    vm.login = function(){
      vm.loading = true;
      var user = {email: vm.email, password: vm.password};
      authService.authUser(user).then(function (response) {
        vm.loading = false;
        $location.path('/home');
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.register = function(){
      vm.userIntegridad.email = vm.userIntegridad.email.trim();
      vm.userIntegridad.password = vm.userIntegridad.password.trim();

      if(vm.userIntegridad.email === '' || vm.userIntegridad.password === ''){
        vm.error = 'Debe ingresar un Email y Password';
      } else {
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
    }

  });

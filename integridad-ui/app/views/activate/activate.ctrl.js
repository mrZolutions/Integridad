'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ActivateCtrl
 * @description
 * # ActivateCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('ActivateCtrl', function ($routeParams, $location, authService, permissionService, $rootScope, $localStorage) {
    var vm = this;

    vm.errorActive = false;
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

    authService.activeUser($routeParams.idUSer, $routeParams.validate).then(function (response) {
      $localStorage.user = response;
      console.log($localStorage.user.tempPass)
      if($localStorage.user.tempPass){
        vm.loading = false;
        vm.passwordNotMatch = false;
        vm.userIntegridad = angular.copy($localStorage.user);
        $('#modalChangePassword').modal('show');
      } else {
        var d = new Date();
        $localStorage.timeloged = d.getTime();
        getPermissions();
      }
    }).catch(function (error) {
      vm.errorActive = true;
      vm.error = error.data;
    });

    vm.validatePassword = function () {
      vm.passwordNotMatch = vm.newPass !== vm.passConfirmation;
    };

    vm.update = function(){
      vm.loading = true;
      vm.userIntegridad.password = vm.newPass;
      vm.userIntegridad.tempPass = false;
      authService.updateUser(vm.userIntegridad).then(function (response) {
        vm.error = undefined;
        vm.success = 'Perfil actualizado con exito';
        $localStorage.user = response;
        var d = new Date();
        $localStorage.timeloged = d.getTime();
        getPermissions();
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }


  });

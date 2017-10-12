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

    }

    authService.activeUser($routeParams.idUSer, $routeParams.validate).then(function (response) {
      $localStorage.user = response;
      var d = new Date();
      $localStorage.timeloged = d.getTime();
      permissionService.getPermissions($localStorage.user.userType).then(function (respnse) {
        $localStorage.permissions = respnse;
        $rootScope.updateMenu();
        vm.loading = false;
        $location.path('/home');
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }).catch(function (error) {
      vm.errorActive = true;
      vm.error = error.data;
    });
  });

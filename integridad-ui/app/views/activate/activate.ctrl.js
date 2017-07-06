'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ActivateCtrl
 * @description
 * # ActivateCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('ActivateCtrl', function ($routeParams, $location, authService) {
    var vm = this;

    vm.errorActive = false;
    authService.activeUser($routeParams.idUSer, $routeParams.validate).then(function (response) {
      $location.path('/home');
    }).catch(function (error) {
      vm.errorActive = true;
      vm.error = error.data;
    });
  });

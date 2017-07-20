'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:MenuCtrl
 * @description
 * # MenuCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('MenuCtrl', function ($rootScope, $scope, $localStorage, $location) {
    $scope.permissions = [];

    $rootScope.updateMenu = function () {
      $scope.permissions = $localStorage.permissions;
    }

    $scope.logout = function () {
      $scope.permissions = [];
      $localStorage.permissions = undefined;
      $localStorage.user = undefined;
      $location.path('/');
    };

    if($localStorage.permissions){
      $rootScope.updateMenu();
    }

  });

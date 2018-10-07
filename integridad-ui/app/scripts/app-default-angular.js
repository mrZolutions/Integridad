'use strict';

/**
 * @ngdoc overview
 * @name integridadUiApp
 * @description
 * # integridadUiApp
 *
 * Main module of the application.
 */
angular
  .module('integridadUiApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl',
        controllerAs: 'main'
      })
      .otherwise({
        redirectTo: '/'
      });
  });

'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('MainCtrl', function (securityService) {
    var vm = this;

    vm.test = function(){
      var user={
        "email": "danielgokuarcos@yahoo.com",
        "password": "12345"
      }
      securityService.post('/user/auth',user).then(function successCallback(response) {
        console.log(JSON.stringify(response, null, 4));
      }).catch(function (e) {
        console.log(JSON.stringify(e, null, 4));
      });
    }
  });

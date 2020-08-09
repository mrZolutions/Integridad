'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:NatCtrl
 * @description
 * # NatCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('GuidesCtrl', function(_, $http, holderService) {
    
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.baseUrl = 'https://guidemrz.herokuapp.com/login/id/';
    //vm.baseUrl = 'http://localhost:3000/login/id/';

    function _verifyUserGuides(){
        vm.user = holderService.get();
        if(vm.user.guias){
            window.open(vm.baseUrl+vm.user.id,'_blank');
        }
    }


    (function initController() {
        _verifyUserGuides();
    })();
});
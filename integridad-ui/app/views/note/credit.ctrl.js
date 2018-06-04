'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CreditNoteCtrl
 * @description
 * # CreditNoteCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CreditNoteCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage,
                                     clientService, productService, authService, billService, $window,
                                     cashierService, requirementService, utilSeqService) {

    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.clientList = undefined;

    function _activate(){
      vm.clientSelected = undefined;
      vm.billList = undefined;
      vm.bill = undefined;

      vm.user = $localStorage.user;
      clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function (response) {
        vm.clientList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    vm.clientSelect = function(client){
      vm.companyData = $localStorage.user.subsidiary;
      vm.clientSelected = client;
      billService.getByClientId(vm.clientSelected.id).then(function(response){
        vm.billList = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });

    };

    vm.billSelect = function(billSelected){
      billService.getById(billSelected.id).then(function(response){
        vm.bill = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    (function initController() {
      _activate();
    })();
});

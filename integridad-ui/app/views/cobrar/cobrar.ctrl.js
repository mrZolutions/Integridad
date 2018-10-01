'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasCobrarCtrl
 * @description
 * # CuentasCobrarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasCobrarCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage, clientService,
                                                cuentasService, creditsbillService, authService, billService, $window, cashierService, creditService, utilSeqService){
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.clientList = undefined;
    vm.creditsbillList = undefined;

    function _activate(){
      vm.error = undefined;
      vm.clientSelected = undefined;
      vm.dateBill = undefined;
      vm.loading = true;
      vm.user = $localStorage.user;
      clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function (response) {
        vm.clientList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.clientSelect = function(client){
      vm.companyData = $localStorage.user.subsidiary;
      vm.dateBill = new Date();
      vm.clientSelected = client;
      vm.pagos=[];
      var today = new Date();
      $('#pickerBillDate').data("DateTimePicker").date(today);
      vm.newBill = true;
    };

    vm.clientConsult = function(client){
      vm.loading = true;
      billService.getCreditsBillsByClientId(client.id).then(function(response) {
        vm.billList = response;
        vm.loading = false;
      }).catch(function(error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.creditsByBill = function(bill){
      vm.loading = true;
      creditsbillService.getAllCreditsOfBillById(bill.id).then(function(response){
        vm.creditsbillList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancel = function(){
      _activate();
    };

    (function initController(){
      _activate();
    })();
});

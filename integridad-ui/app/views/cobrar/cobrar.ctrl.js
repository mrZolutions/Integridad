'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasCobrarCtrl
 * @description
 * # CuentasCobrarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasCobrarCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage, clientService, cuentaContableService,
                                                cuentasService, creditsbillService, authService, billService, $window, cashierService, creditService, utilSeqService){
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.billNumber = undefined;
    vm.clientList = undefined;
    vm.creditsbillList = undefined;
    vm.cuentaContablePrincipal = undefined;
    vm.clientName = undefined;
    vm.clientId = undefined;
    vm.clientCodConta = undefined;

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
      cuentaContableService.getAll().then(function(response) {
        vm.cuentaContableList = response;
      });
    };

    vm.clientConsult = function(client){
      vm.loading = true;
      vm.clientId = client.identification;
      vm.clientName = client.name;
      vm.clientCodConta = client.codConta;
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
      vm.billNumber = bill.stringSeq;
      creditsbillService.getAllCreditsOfBillById(bill.id).then(function(response){
        vm.creditsbillList = response;
        vm.creditValue = response.valor;
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

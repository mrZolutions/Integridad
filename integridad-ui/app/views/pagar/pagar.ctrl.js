'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasPagarCtrl
 * @description
 * # CuentasPagarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasPagarCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage,
                                                cuentasService, productService, authService, billService, $window, cashierService, creditService, utilSeqService){
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.clientList = undefined;

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

    function _initializeBill(){
      vm.bill = {
        client: vm.clientSelected,
        userIntegridad: $localStorage.user,
        subsidiary: $localStorage.user.subsidiary,
        dateCreated: vm.dateBill.getTime(),
        discount: 0,
        total: 0,
        subTotal: 0,
        iva: 0,
        ice: 0,
        details: [],
        pagos:[],
      };
    };

    vm.clientSelect = function(client){
      vm.quotations = [];
      vm.companyData = $localStorage.user.subsidiary;
      vm.dateBill = new Date();
      vm.clientSelected = client;
      vm.pagos=[];
      _initializeBill();
      var today = new Date();
      $('#pickerBillDate').data("DateTimePicker").date(today);
      vm.newBill = true;
    };

    vm.clientConsult = function(client){
      vm.loading = true;
      _initializeBill();
      billService.getBillsByClientId(client.id).then(function (response) {
        vm.billList = response;
        vm.loading = false;
      }).catch(function(error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.billSelect = function(bill){
      vm.loading = true;
      billService.getById(bill.id).then(function(response){
        vm.billList = undefined;
        vm.bill = response;
        vm.companyData = $localStorage.user.subsidiary;
        vm.clientSelected = response.client;
        vm.pagos = response.pagos;
        var dateToShow = new Date(response.dateCreated);
        vm.seqNumber = response.stringSeq;
        $('#pickerBillDate').data("DateTimePicker").date(dateToShow);
        vm.newBill = false;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancel = function () {
      _activate();
    };

    (function initController() {
      _activate();
    })();
});

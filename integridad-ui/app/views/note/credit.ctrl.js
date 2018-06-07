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
      vm.claveDeAcceso = undefined;

      vm.user = $localStorage.user;
      clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function (response) {
        vm.clientList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function _getSeqNumber(){
      vm.numberAddedOne = parseInt($localStorage.user.cashier.creditNoteNumberSeq) + 1;
      vm.seqNumberFirstPart = $localStorage.user.subsidiary.threeCode + '-'
        + $localStorage.user.cashier.threeCode;
      vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 10);
      vm.seqNumber =  vm.seqNumberFirstPart + '-'
        + vm.seqNumberSecondPart;
    }

    function _getTotales(){
      var subtotal = 0;
      var descuento = 0;
      var baseZero = 0;
      var baseDoce = 0;
      var iva = 0;
      var total = 0;
      _.each(vm.bill.details, function(detail){
        subtotal = (parseFloat(subtotal) + (parseFloat(detail.quantity) * parseFloat(detail.costEach))).toFixed(2);
        if(detail.product.iva){
          baseDoce = (parseFloat(baseDoce) + (parseFloat(detail.quantity) * parseFloat(detail.costEach))).toFixed(2);
        } else {
          baseZero = (parseFloat(baseZero) + (parseFloat(detail.quantity) * parseFloat(detail.costEach))).toFixed(2);
        }
      });

      descuento = ((parseFloat(vm.bill.discount) * subtotal)/100).toFixed(2);
      baseDoce = baseDoce - descuento;
      iva = baseDoce * parseFloat(0.12);

      total = (baseDoce + baseZero + iva).toFixed(2);

      vm.bill.subTotal = subtotal;
      vm.bill.discount = descuento;
      vm.bill.baseNoTaxes = baseZero;
      vm.bill.baseTaxes = baseDoce;
      vm.bill.iva = (iva).toFixed(2);
      vm.bill.total = total;
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
        _getSeqNumber();
        var today = new Date();
        $('#pickerCreditNoteDate').data("DateTimePicker").date(today);
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancelCreditNote = function(){
      _activate();
    };

    vm.saveCreditNote = function(){
      
    };

    vm.removeDetail=function(index){
      vm.bill.details.splice(index,1);
      _getTotales();
    };

    (function initController() {
      _activate();
    })();
});

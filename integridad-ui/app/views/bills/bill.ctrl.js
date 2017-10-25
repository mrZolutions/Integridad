'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # BillCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('BillCtrl', function ($rootScope, utilStringService, $localStorage, clientService, productService) {
    var vm = this;

    vm.loading = false;
    vm.clientList = undefined;

    function _activate(){
      vm.clientSelected = undefined;
      vm.dateBill = undefined;
      vm.seqNumber = undefined;
      vm.productList = undefined;
      vm.productToAdd = undefined;
      vm.quantity = undefined;
      vm.loading = true;
      clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function (response) {
        vm.clientList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

    function _pad_with_zeroes(number, length) {
      var my_string = '' + number;
      while (my_string.length < length) {
        my_string = '0' + my_string;
      }

      return my_string;
    }

    function _getSeqNumber(){
      var numberAddedOne = parseInt($localStorage.user.subsidiary.billNumberSeq) + 1;
      vm.seqNumber = $localStorage.user.subsidiary.userClient.threeCode + '-'
        + $localStorage.user.subsidiary.threeCode + '-'
        + _pad_with_zeroes(numberAddedOne, 10);
    }

    function _initializeBill(){
      vm.bill ={
        client: vm.clientSelected,
        userIntegridad: $localStorage.user,
        subsidiary: $localStorage.user.subsidiary,
        dateCreated: vm.dateBill.getTime(),
        total: 0,
        subTotal: 0,
        details: []
      };
    }

    vm.clientSelect = function(client){
      vm.dateBill = new Date();
      vm.clientSelected = client;
      _getSeqNumber();
      _initializeBill();
    };

    vm.addProduct = function(){
      vm.loading = true;
      productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id)
      .then(function(response){
        vm.loading = false;
        vm.productList = response;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.acceptProduct = function(closeModal){
      var detail={
        product: vm.productToAdd,
        quantity: vm.quantity,
        costEach: vm.productToAdd.cost,
        total: parseFloat(vm.quantity) * parseFloat(vm.productToAdd.cost)
      }

      vm.bill.details.push(detail);
      vm.productToAdd = undefined;
      vm.quantity = undefined;

      if(closeModal){
        $('#modalAddProduct').modal('hide');
      } else {
        for (var i = 0; i < vm.productList.length; i++) {
          if(vm.productList[i].id === detail.product.id){
            vm.productList[i].quantity -= detail.quantity
            break;
          }
        }
      }

    };

    (function initController() {
      _activate();
    })();

  });

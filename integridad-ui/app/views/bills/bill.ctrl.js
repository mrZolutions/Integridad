'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # BillCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('BillCtrl', function ( _, $rootScope, utilStringService, $localStorage, clientService, productService) {
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
      vm.errorQuantity = undefined;
      productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id)
      .then(function(response){
        vm.loading = false;
        vm.productList = [];

        for (var i = 0; i < response.length; i++) {
          var productFound = _.find(vm.bill.details, function (detail) {
            return detail.product.id === response[i].id;
          })

          if(productFound === undefined){
            vm.productList.push(response[i]);
          }
        }
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.acceptProduct = function(closeModal){
      if(parseInt(vm.quantity) <= parseInt(vm.productToAdd.quantity)){
        vm.errorQuantity = undefined;
        var detail={
          product: angular.copy(vm.productToAdd),
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
          var newProductList = _.filter(vm.productList, function (prod) {
            return prod.id !== detail.product.id;
          });
          vm.productList = newProductList;
        }
      } else {
        vm.errorQuantity = 'Cantidad disponible insuficiente';
      }

    };

    vm.editDetail=function(detail, index){
      vm.productToAdd= detail.product;
      vm.quantity= detail.quantity
      //TODO editar elemento de la lista de details
    };

    (function initController() {
      _activate();
    })();

  });

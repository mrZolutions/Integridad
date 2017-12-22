'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # BillCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('BillCtrl', function ( _, $rootScope, $location, utilStringService, $localStorage,
                                     clientService, productService, authService, billService) {
    var vm = this;

    vm.loading = false;
    vm.clientList = undefined;
    vm.isEmp = true;
    vm.prices = [
      { name: 'EFECTIVO', cod: 'cashPercentage'}, { name: 'MAYORISTA', cod: 'majorPercentage'},
      { name: 'CREDITO', cod: 'creditPercentage'}, { name: 'TARJETA', cod: 'cardPercentage'}
    ];
    vm.tipyIdCode = {
      RUC : '04',
      CED : '05'
    }


    function _activate(){
      vm.clientSelected = undefined;
      vm.dateBill = undefined;
      vm.seqNumber = undefined;
      vm.productList = undefined;
      vm.productToAdd = undefined;
      vm.quantity = undefined;
      vm.loading = true;
      vm.indexDetail = undefined;
      vm.priceType = vm.prices[0];

      vm.impuestosTotales = [];
      vm.impuestoICE = {
        "base_imponible":0.0,
        "valor":0.0,
        "codigo":"3",
        "codigo_porcentaje":2
      };
      vm.impuestoIVA = {
        "base_imponible":0.0,
        "valor":0.0,
        "codigo":"2",
        "codigo_porcentaje":2
      };

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
      var numberAddedOne = parseInt($localStorage.user.cashier.billNumberSeq) + 1;
      vm.seqNumberFirstPart = $localStorage.user.subsidiary.userClient.threeCode + '-'
        + $localStorage.user.cashier.threeCode;
      vm.seqNumberSecondPart = _pad_with_zeroes(numberAddedOne, 10);
      vm.seqNumber =  vm.seqNumberFirstPart + '-'
        + vm.seqNumberSecondPart;

    }

    function _initializeBill(){
      vm.bill ={
        client: vm.clientSelected,
        userIntegridad: $localStorage.user,
        subsidiary: $localStorage.user.subsidiary,
        dateCreated: vm.dateBill.getTime(),
        total: 0,
        subTotal: 0,
        iva: 0,
        ice: 0,
        details: []
      };
    }

    function _getTotalSubtotal(){
      vm.bill.subTotal = 0;
      vm.bill.iva = 0;
      vm.bill.ice = 0;
      _.each(vm.bill.details, function(detail){
        vm.bill.subTotal = (parseFloat(vm.bill.subTotal) + parseFloat(detail.total)).toFixed(2);
        if(detail.product.iva){
          vm.bill.iva = (parseFloat(vm.bill.iva) + (parseFloat(detail.total) * 0.12)).toFixed(2);
        }
        if(detail.product.ice){
          vm.bill.ice = (parseFloat(vm.bill.ice) + (parseFloat(detail.total) * 0.10)).toFixed(2);
        }

      });

      vm.impuestoICE.base_imponible = vm.bill.subTotal;
      vm.impuestoIVA.base_imponible = vm.bill.subTotal;
      vm.impuestoICE.valor = vm.bill.ice;
      vm.impuestoIVA.valor = vm.bill.iva;
      vm.bill.total = (parseFloat(vm.bill.subTotal)
        +  parseFloat(vm.bill.iva)
        +  parseFloat(vm.bill.ice)
      ).toFixed(2);

    }

    vm.acceptNewSeq = function(){
      vm.seqNumber =  vm.seqNumberFirstPart + '-'
        + vm.seqNumberSecondPart;
    };

    vm.reCalculateTotal = function(){
      _.map(vm.bill.details, function(detail){
        var costEachCalculated = vm.getCost('1.'+ detail.product[vm.priceType.cod], detail.product.averageCost);
        detail.costEach = costEachCalculated;
        detail.total = (parseFloat(detail.quantity) * parseFloat(detail.costEach)).toFixed(2);
      });
      _getTotalSubtotal();
    };

    vm.clientCreate = function(){
      $location.path('/clients/create');
    };

    vm.clientSelect = function(client){
      vm.dateBill = new Date();
      vm.clientSelected = client;
      _getSeqNumber();
      _initializeBill();
    };

    vm.addProduct = function(){
      vm.indexDetail = undefined;
      vm.loading = true;
      vm.errorQuantity = undefined;
      productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id)
      .then(function(response){
        vm.loading = false;
        vm.productList = [];

        for (var i = 0; i < response.length; i++) {
          var productFound = _.find(vm.bill.details, function (detail) {
            return detail.product.id === response[i].id;
          });

          if(productFound === undefined){
            var sub = _.find(response[i].productBySubsidiaries, function (s) {
              return s.subsidiary.id === $localStorage.user.subsidiary.id;
            });
            response[i].quantity = sub.quantity
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
        var costEachCalculated = vm.getCost('1.'+ vm.productToAdd[vm.priceType.cod], vm.productToAdd.averageCost);
        var detail={
          product: angular.copy(vm.productToAdd),
          quantity: vm.quantity,
          costEach: costEachCalculated,
          total: (parseFloat(vm.quantity) * parseFloat(costEachCalculated)).toFixed(2)
        }

        if(vm.indexDetail !== undefined){
          vm.bill.details[vm.indexDetail] = detail;
        } else {
          vm.bill.details.push(detail);
        }


        vm.productToAdd = undefined;
        vm.quantity = undefined;
        _getTotalSubtotal();

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

    vm.getCost = function(textCost, averageCost){
      var aC = parseFloat(textCost)
      var cost = aC * averageCost;
      return (cost).toFixed(2);
    };

    vm.editDetail=function(detail, index){
      vm.indexDetail = index;
      vm.productToAdd= detail.product;
      vm.quantity= detail.quantity
    };

    vm.removeDetail=function(index){
      vm.bill.details.splice(index,1);
    };

    vm.validateAdm = function(){
      vm.errorValidateAdm = undefined;
      var userAdm = $localStorage.user.user;
      userAdm.password = vm.passwordAdm;
      authService.authUser(userAdm)
      .then(function(response){
        vm.loading = false;
        vm.isEmp = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.errorValidateAdm = error.data;
      });
    }

    vm.verifyUser = function(){
      vm.isEmp = $localStorage.user.userType.code === 'EMP';
    };

    vm.getClaveAcceso = function(){
      console.log($localStorage.user.cashier);
      console.log(vm.clientSelected)
      vm.impuestosTotales.push(vm.impuestoICE,vm.impuestoIVA);
      var req = {
        "ambiente": 1,
        "tipo_emision": 1,
        "secuencial": 148,
        "fecha_emision": billService.getIsoDate($('#pickerBillDate').data("DateTimePicker").date().toDate()),
        "emisor":{
          "ruc":$localStorage.user.cashier.subsidiary.userClient.ruc,
          "obligado_contabilidad":true,
          "contribuyente_especial":"",
          "nombre_comercial":$localStorage.user.cashier.subsidiary.userClient.name,
          "razon_social":$localStorage.user.cashier.subsidiary.userClient.name,
          "direccion":$localStorage.user.cashier.subsidiary.userClient.address1,
          "establecimiento":{
            "punto_emision":$localStorage.user.cashier.subsidiary.threeCode,
            "codigo":$localStorage.user.cashier.threeCode,
            "direccion":$localStorage.user.cashier.subsidiary.address1
          }
        },
        "moneda":"USD",
        "informacion_adicional":{},
        "totales":{
          "total_sin_impuestos":vm.bill.subTotal,
          "impuestos":vm.impuestosTotales,
          "importe_total":vm.bill.total,
          "propina":0.0,
          "descuento":0.0
        },
        "comprador":{
          "email":vm.clientSelected.email,
          "identificacion":vm.clientSelected.identification,
          "tipo_identificacion":vm.tipyIdCode[vm.clientSelected.typeId],
          "razon_social":vm.clientSelected.name,
          "direccion":vm.clientSelected.address,
          "telefono":vm.clientSelected.phone
        },
        "items":[
          {
            "cantidad":622.0,
            "codigo_principal": "ZNC",
            "codigo_auxiliar": "050",
            "precio_unitario": 7.01,
            "descripcion": "Zanahoria granel  50 Kg.",
            "precio_total_sin_impuestos": 4360.22,
            "impuestos": [
              {
                "base_imponible":4359.54,
                "valor":523.14,
                "tarifa":12.0,
                "codigo":"2",
                "codigo_porcentaje":"2"
              }
            ],
            "detalles_adicionales": {
              "Peso":"5000.0000"
            },
            "descuento": 0.0,
            "unidad_medida": "Kilos"
          }
        ]
      };


      console.log(req)
      // billService.getClaveDeAcceso();
    };

    (function initController() {
      _activate();
    })();

  });

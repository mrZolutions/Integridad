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
                                     clientService, productService, authService, billService, $window) {
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
    };
    vm.medList = [
      {code: 'efectivo', name: 'Efectivo' },
      {code: 'cheque', name: 'Cheque' },
      {code: 'cheque_posfechado', name: 'Cheque posfechado' },
      {code: 'tarjeta_credito', name: 'Tarjeta de crédito' },
      {code: 'tarjeta_debito', name: 'Tarjeta de débito' },
      {code: 'dinero_electronico_ec', name: 'Dinero electrónico' },
      {code: 'credito', name: 'Crédito' },
    ];
    vm.formList = [
      '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO',
      '15 - COMPENSACION DE DEUDAS',
      '16 - TARJETAS DE DEBITO',
      '17 - DINERO ELECTRONICO',
      '18 - TARJETA PREPAGO',
      '19 - TARJETA DE CREDITO',
      '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
      '21 - ENDOSO DE TITULOS'
    ];
    vm.creditCardList=[
      'DINNERS CLUB',
      'VISA',
      'MASTERCARD',
      'AMERICAN'
    ];


    function _activate(){
      vm.billed = false;
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
      vm.items = [];
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
      vm.medio={};
      vm.pagos=[];

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
      vm.numberAddedOne = parseInt($localStorage.user.cashier.billNumberSeq) + 1;
      vm.seqNumberFirstPart = $localStorage.user.subsidiary.userClient.threeCode + '-'
        + $localStorage.user.cashier.threeCode;
      vm.seqNumberSecondPart = _pad_with_zeroes(vm.numberAddedOne, 10);
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
        details: [],
        pagos:[],
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

    function _addDays(date, days) {
      var result = new Date(date);
      result.setDate(result.getDate() + days);
      return result.getTime();
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
      vm.companyData = $localStorage.user.subsidiary;
      console.log(vm.companyData)
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
              return (s.subsidiary.id === $localStorage.user.subsidiary.id && s.active === true);
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

    vm.selectProductToAdd = function(productSelect){
      if(productSelect.productType.code === 'SER'){
        productSelect.quantity = 1;
      }
      vm.productToAdd = angular.copy(productSelect);
      var costEachCalculated = vm.getCost('1.'+ productSelect[vm.priceType.cod], productSelect.averageCost);
      vm.productToAdd.costEachCalculated = costEachCalculated;
      vm.quantity = 1;
    }

    vm.acceptProduct = function(closeModal){
      if(vm.productToAdd.productType.code !== 'SER'){
          if(parseInt(vm.quantity) > parseInt(vm.productToAdd.quantity)){
            vm.errorQuantity = 'Cantidad disponible insuficiente';
            return;
          }
      }

      vm.errorQuantity = undefined;

      var detail={
        product: angular.copy(vm.productToAdd),
        quantity: vm.quantity,
        costEach: vm.productToAdd.costEachCalculated,
        total: (parseFloat(vm.quantity) * parseFloat(vm.productToAdd.costEachCalculated)).toFixed(2)
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

    vm.loadMedio = function(){
      if(vm.medio.medio === 'efectivo' || vm.medio.medio === 'dinero_electronico_ec'){
        vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
      }
      if(vm.medio.medio === 'credito'){
        vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = vm.bill.total;
      }
      if(vm.medio.medio === 'cheque' || vm.medio.medio === 'cheque_posfechado'){
        vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = vm.bill.total;
      }
      if(vm.medio.medio === 'tarjeta_credito' || vm.medio.medio === 'tarjeta_debito'){
        vm.medio.payForm = '19 - TARJETA DE CREDITO';
        vm.medio.total = vm.bill.total;
      }
    };

    vm.loadCredit = function(){
      var creditArray = [];
      var diasPlazo = parseInt(vm.medio.creditoIntervalos);
      var d = new Date();
      var total = parseFloat(parseFloat(vm.bill.total)/parseFloat(vm.medio.creditoNumeroPagos)).toFixed(2);
      for (var i = 1; i <= parseInt(vm.medio.creditoNumeroPagos); i++) {
        var credito = {
          payNumber: i,
          diasPlazo: diasPlazo,
          fecha: _addDays(d, diasPlazo),
          valor: total
        }
        diasPlazo += parseInt(vm.medio.creditoIntervalos);
        creditArray.push(credito)
      }

      vm.medio.credits = creditArray

    };

    vm.getFechaCobro = function() {
      var d = new Date();
      vm.medio.fechaCobro = _addDays(d, parseInt(vm.medio.chequeDiasPlazo));
    };

    vm.addPago = function(){
      vm.pagos.push(angular.copy(vm.medio));
      vm.medio = {};
    };

    vm.removePago = function(index){
      vm.pagos.splice(index, 1);
    };

    vm.getTotalPago = function(){
      vm.varPago=0;

      if(vm.bill){
        vm.getCambio=0;
        _.each(vm.pagos, function(med){
          vm.varPago=parseFloat(parseFloat(vm.varPago)+parseFloat(med.total)).toFixed(2);
        });

        vm.getCambio = (vm.varPago - vm.bill.total).toFixed(2);
      }

      return vm.varPago;
    };

    vm.getClaveAcceso = function(){
      vm.impuestosTotales.push(vm.impuestoICE,vm.impuestoIVA);
      vm.bill.billSeq = vm.numberAddedOne;

      _.each(vm.bill.details, function(det){
        var costWithIva = (det.costEach*1.12).toFixed(2);
        var costWithIce = (det.costEach*1.10).toFixed(2);
        var impuestos = [];
        var impuesto ={};
        if(det.product.iva){
          impuesto.base_imponible=det.costEach;
          impuesto.valor=costWithIva;
          impuesto.tarifa=12.0;
          impuesto.codigo='2';
          impuesto.codigo_porcentaje='2';

          impuestos.push(impuesto);
        }

        if(det.product.ice){
          impuesto.base_imponible=det.costEach;
          impuesto.valor=costWithIce;
          impuesto.tarifa=10.0;
          impuesto.codigo='3';
          impuesto.codigo_porcentaje='2';

          impuestos.push(impuesto);
        }

        var item={
          "cantidad":det.quantity,
          "codigo_principal": det.product.barCode,
          "codigo_auxiliar": det.product.barCode,
          "precio_unitario": costWithIva,
          "descripcion": det.product.name,
          "precio_total_sin_impuestos": det.costEach,
          "impuestos": impuestos,
          "detalles_adicionales": null,
          "descuento": 0.0,
          "unidad_medida": det.product.unitOfMeasurementFull
        }

        vm.items.push(item);
      });

      var req = {
        "ambiente": 1,
        "tipo_emision": 1,
        "secuencial": vm.bill.billSeq,
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
        "items":vm.items,
        "valor_retenido_iva": 70.40,
        "valor_retenido_renta": 29.60,
        "pagos": vm.pagos
      };

      billService.getClaveDeAcceso(req).then(function(resp){
        vm.bill.pagos = vm.pagos;
        console.log('resp',resp)
        console.log('bill',vm.bill)
        billService.create(vm.bill).then(function(respBill){
          vm.billed = true;
          $localStorage.user.cashier.billNumberSeq = vm.bill.billSeq;
        }).catch(function (error) {
          vm.loading = false;
          vm.errorValidateAdm = error.data;
        });

      }).catch(function (error) {
        vm.loading = false;
        vm.errorValidateAdm = error.data;
      });
    };

    (function initController() {
      _activate();
    })();

  });

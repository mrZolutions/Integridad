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
    vm.error = undefined;
    vm.success = undefined;

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
      vm.newBill = true;
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
        discount: 0,
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
      vm.bill.baseTaxes = 0;
      vm.bill.baseNoTaxes = 0;
      var discountWithIva = 0;
      var discountWithNoIva = 0;
      _.each(vm.bill.details, function(detail){
        vm.bill.subTotal = (parseFloat(vm.bill.subTotal) + parseFloat(detail.total)).toFixed(2);
        var tot = detail.total;
        if(vm.bill.discountPercentage){
          tot = (parseFloat(detail.total) - (parseInt(vm.bill.discountPercentage)/100)*(parseFloat(detail.total))).toFixed(2);
        }
        if(detail.product.iva){
          vm.bill.baseTaxes += parseFloat(detail.total);
          vm.bill.iva = (parseFloat(vm.bill.iva) + (parseFloat(tot) * 0.12)).toFixed(2);
          if(vm.bill.discountPercentage){
            discountWithIva = (parseFloat(discountWithIva) + ((parseInt(vm.bill.discountPercentage)/100)*detail.total)).toFixed(2);
          }
        } else {
          vm.bill.baseNoTaxes += parseFloat(detail.total);
          if(vm.bill.discountPercentage){
            discountWithNoIva = (parseFloat(discountWithNoIva) + ((parseInt(vm.bill.discountPercentage)/100)*detail.total)).toFixed(2);
          }
        }

        if(detail.product.ice){
          vm.bill.ice = (parseFloat(vm.bill.ice) + (parseFloat(tot) * 0.10)).toFixed(2);
        }

      });

      if(vm.bill.discountPercentage){
        vm.bill.discount = ((parseInt(vm.bill.discountPercentage)/100)*vm.bill.subTotal).toFixed(2);
      }else {
        vm.bill.discount = 0;
      }


      vm.impuestoICE.base_imponible = vm.bill.subTotal;
      vm.impuestoIVA.base_imponible = vm.bill.subTotal;
      vm.impuestoICE.valor = vm.bill.ice;
      vm.impuestoIVA.valor = vm.bill.iva;
      vm.bill.baseTaxes = (vm.bill.baseTaxes - discountWithIva).toFixed(2);
      vm.bill.baseNoTaxes = (vm.bill.baseNoTaxes - discountWithNoIva).toFixed(2);
      vm.bill.total = (parseFloat(vm.bill.baseTaxes)
        +  parseFloat(vm.bill.baseNoTaxes)
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
      vm.seqErrorNumber = undefined;
      vm.loading = true;
      var stringSeq =  vm.seqNumberFirstPart + '-'
        + vm.seqNumberSecondPart;

      billService.getByStringSeq(stringSeq).then(function (response) {
        if(response.length === 0){
          vm.seqNumber = stringSeq;
          $('#modalEditBillNumber').modal('hide');
        } else {
          vm.seqErrorNumber = "NUMERO DE FACTURA YA EXISTENTE"
        }
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.reCalculateTotal = function(){
      _.map(vm.bill.details, function(detail){
        if(vm.bill.discountPercentage){
          detail.discount = vm.bill.discountPercentage;
        } else {
          detail.discount = 0;
        }
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
      vm.dateBill = new Date();
      vm.clientSelected = client;
      vm.pagos=[];
      _getSeqNumber();
      _initializeBill();
      var today = new Date();
      $('#pickerBillDate').data("DateTimePicker").date(today);
      vm.newBill = true;
    };

    vm.clientConsult = function(client){
      vm.loading = true;
      billService.getByClientId(client.id).then(function (response) {
        vm.billList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });

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
        discount: vm.bill.discountPercentage? vm.bill.discountPercentage : 0,
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
      var payed = 0;
      _.each(vm.pagos, function(pago){
        payed +=(parseFloat(pago.total));
      });

      vm.pagos
      if(vm.medio.medio === 'efectivo' || vm.medio.medio === 'dinero_electronico_ec'){
        vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
      }
      if(vm.medio.medio === 'credito'){
        vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = (vm.bill.total - payed).toFixed(2);
      }
      if(vm.medio.medio === 'cheque' || vm.medio.medio === 'cheque_posfechado'){
        vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = (vm.bill.total - payed).toFixed(2);
      }
      if(vm.medio.medio === 'tarjeta_credito' || vm.medio.medio === 'tarjeta_debito'){
        vm.medio.payForm = '19 - TARJETA DE CREDITO';
        vm.medio.total = (vm.bill.total - payed).toFixed(2);
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

    vm.billSelect = function(bill){
      vm.loading = true;
      billService.getById(bill.id).then(function (response) {
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

    }

    vm.getClaveAcceso = function(){
      vm.loading = true;
      $('#modalAddPago').modal('hide');
      // vm.impuestosTotales.push(vm.impuestoICE,vm.impuestoIVA);
      vm.impuestosTotales = [];
      vm.impuestosTotales.push(vm.impuestoIVA);
      vm.bill.billSeq = vm.numberAddedOne;

      _.each(vm.bill.details, function(det){
        var costWithIva = (det.costEach*1.12).toFixed(2);
        var costWithIce = (det.costEach*1.10).toFixed(2);
        var impuestos = [];
        var impuesto ={};
        if(det.product.iva){
          impuesto.base_imponible=(parseFloat(det.costEach)*parseFloat(det.quantity)).toFixed(2);
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
          "precio_unitario": det.costEach,
          "descripcion": det.product.name,
          "precio_total_sin_impuestos": (parseFloat(det.costEach)*parseFloat(det.quantity)).toFixed(2),
          "impuestos": impuestos,
          "descuento": vm.bill.discountPercentage,
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
            "punto_emision":$localStorage.user.cashier.threeCode,
            "codigo":$localStorage.user.cashier.subsidiary.threeCode,
            "direccion":$localStorage.user.cashier.subsidiary.address1
          }
        },
        "moneda":"USD",
        "totales":{
          "total_sin_impuestos":vm.bill.subTotal,
          "impuestos":vm.impuestosTotales,
          "importe_total":vm.bill.total,
          "propina":0.0,
          "descuento":vm.bill.discount
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
        "valor_retenido_iva": 0,
        "valor_retenido_renta": 0,
        "pagos": vm.pagos,

      };

      billService.getClaveDeAcceso(req, vm.companyData.userClient.id).then(function(resp){
        vm.bill.pagos = vm.pagos;
        if(vm.bill.discountPercentage === undefined){
          vm.bill.discountPercentage = 0;
        }
        var obj = JSON.parse(resp.data);
        if(obj.errors === undefined){
          vm.bill.claveDeAcceso = obj.clave_acceso;
          vm.bill.idSri = obj.id;
          vm.bill.stringSeq = vm.seqNumber;
          vm.bill.priceType = vm.priceType.name;
          billService.create(vm.bill).then(function(respBill){
            vm.billed = true;
            $localStorage.user.cashier.billNumberSeq = vm.bill.billSeq;
            _activate();
            vm.loading = false;
          }).catch(function (error) {
            vm.loading = false;
            vm.errorValidateAdm = error.data;
          });
        } else {
          vm.loading = false;
          vm.error = "Error al obtener Clave de Acceso: " + JSON.stringify(obj.errors);
        }

      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    (function initController() {
      _activate();
    })();

  });

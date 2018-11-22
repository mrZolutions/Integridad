'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebtsToPayCtrl
 * @description
 * # DebtsToPayCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('DebtsToPayCtrl', function (_, $localStorage, providerService, utilStringService, dateService, 
                                        cuentaContableService, validatorService, debtsToPayService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.usrCliId = undefined;
    vm.provider = undefined;
    vm.providerId = undefined;
    vm.subTotal = undefined;
    vm.subIva = undefined;
    vm.cuentaCtableId = undefined;
    vm.cuentaContableList = undefined;
    vm.debtsToPayCreated = undefined;
    vm.providerSelected = undefined;
    vm.providerList = undefined;
    vm.saldoCredito = undefined;
    vm.saldoDebito = undefined;
    vm.saldo = undefined;

    vm.voucherType = [
      {code: '01', name: '01 - Factura'},
      {code: '02', name: '02 - Nota o boleta de venta'},
      {code: '03', name: '03 - Liquidación de compra de Bienes o Prestación de servicios'},
      {code: '04', name: '04 - Nota de crédito'},
      {code: '05', name: '05 - Nota de débito'},
      {code: '06', name: '06 - Guías de Remisión'},
      {code: '07', name: '07 - Comprobante de Retención'},
      {code: '08', name: '08 - Boletos o entradas a espectáculos públicos'},
      {code: '09', name: '09 - Tiquetes o vales emitidos por máquinas registradoras'},
      {code: '11', name: '11 - Pasajes expedidos por empresas de aviación'},
      {code: '12', name: '12 - Documentos emitidos por instituciones financieras'},
      {code: '15', name: '15 - Comprobante de venta emitido en el Exterior'},
      {code: '16', name: '16 - Formulario Único de Exportación (FUE) o Declaración Aduanera Única (DAU) o Declaración Andina de Valor (DAV)'},
      {code: '18', name: '18 - Documentos autorizados utilizados en ventas excepto N/C N/D'},
      {code: '19', name: '19 - Comprobantes de Pago de Cuotas o Aportes'},
      {code: '20', name: '20 - Documentos por Servicios Administrativos emitidos por Inst. del Estado'},
      {code: '21', name: '21 - Carta de Porte Aéreo'},
      {code: '22', name: '22 - RECAP'},
      {code: '23', name: '23 - Nota de Crédito TC'},
      {code: '24', name: '24 - Nota de Débito TC'},
      {code: '41', name: '41 - Comprobante de venta emitido por reembolso'},
      {code: '42', name: '42 - Documento retención presuntiva y retención emitida por propio vendedor o por intermediario'},
      {code: '43', name: '43 - Liquidación para Explotación y Exploracion de Hidrocarburos'},
      {code: '44', name: '44 - Comprobante de Contribuciones y Aportes'},
      {code: '45', name: '45 - Liquidación por reclamos de aseguradoras'}
    ];

    vm.supportType = [
      {code: '01', name: '01 - Crédito Tributario para declaración de IVA (servicios y bienes distintos de inventarios y activos fijos)'},
      {code: '02', name: '02 - Costo o Gasto para declaración de IR (servicios y bienes distintos de inventarios y activos fijos)'},
      {code: '03', name: '03 - Activo Fijo - Crédito Tributario para declaración de IVA'},
      {code: '04', name: '04 - Activo Fijo - Costo o Gasto para declaración de IR'},
      {code: '05', name: '05 - Liquidación Gastos de Viaje, hospedaje y alimentación Gastos IR (a nombre de empleados y no de la empresa)'},
      {code: '06', name: '06 - Inventario - Crédito Tributario para declaración de IVA'},
      {code: '07', name: '07 - Inventario - Costo o Gasto para declaración de IR'},
      {code: '08', name: '08 - Valor pagado para solicitar Reembolso de Gasto (intermediario)'},
      {code: '09', name: '09 - Reembolso por Siniestros'},
      {code: '10', name: '10 - Distribución de Dividendos, Beneficios o Utilidades'},
      {code: '11', name: '11 - Convenios de débito o recaudación para IFI´s'},
      {code: '12', name: '12 - Impuestos y retenciones presuntivos'},
      {code: '13', name: '13 - Valores reconocidos por entidades del sector público a favor de sujetos pasivos'},
      {code: '14', name: '14 - Valores facturados por socios a operadoras de transporte (que no constituyen gasto de dicha operadora)'},
      {code: '00', name: '15 - Casos especiales cuyo sustento no aplica en las opciones anteriores'}
    ];

    vm.formPayment = [
      {code: '01', name: '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO'},
      {code: '02', name: '02 - CHEQUE PROPIO'},
      {code: '03', name: '03 - CHEQUE CERTIFICADO'},
      {code: '04', name: '04 - CHEQUE DE GERENCIA'},
      {code: '05', name: '05 - CHEQUE DEL EXTERIOR'},
      {code: '06', name: '06 - DÉBITO DE CUENTA'},
      {code: '07', name: '07 - TRANSFERENCIA PROPIO BANCO'},
      {code: '08', name: '08 - TRANSFERENCIA OTRO BANCO NACIONAL'},
      {code: '09', name: '09 - TRANSFERENCIA  BANCO EXTERIOR'},
      {code: '10', name: '10 - TARJETA DE CRÉDITO NACIONAL'},
      {code: '11', name: '11 - TARJETA DE CRÉDITO INTERNACIONAL'},
      {code: '12', name: '12 - GIRO'},
      {code: '13', name: '13 - DEPOSITO EN CUENTA (CORRIENTE/AHORROS)'},
      {code: '14', name: '14 - ENDOSO DE INVERSIÒN'},
      {code: '15', name: '15 - COMPENSACIÓN DE DEUDAS'},
      {code: '16', name: '16 - TARJETA DE DÉBITO'},
      {code: '17', name: '17 - DINERO ELECTRÓNICO'},
      {code: '18', name: '18 - TARJETA PREPAGO'},
      {code: '19', name: '19 - TARJETA DE CRÉDITO'},
      {code: '20', name: '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO'},
      {code: '21', name: '21 - ENDOSO DE TÍTULOS'}
    ];

    vm.purchaseType = [
      {code: 'BIE', name: 'BIENES'},
      {code: 'SER', name: 'SERVICIOS'},
      {code: 'MTP', name: 'MATERIA PRIMA'},
      {code: 'CON', name: 'CONSUMIBLES'},
      {code: 'REG', name: 'REEMBOLSO DE GASTOS'},
      {code: 'TAE', name: 'TIKETS AEREOS'}
    ];

    function _activate(){
      vm.today = new Date();
      vm.provider = undefined;
      vm.providerSelected = undefined;
      vm.providerList = [];
      vm.usrCliId = $localStorage.user.subsidiary.userClient.id
      providerService.getLazyByUserClientId(vm.usrCliId).then(function(response){
        vm.providerList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.selectProvider = function(provider){
      vm.loading = true;
      var today = new Date();
      vm.providerSelected = true;
      vm.debtsToPayCreated = undefined;
      vm.providerId = provider.id;
      vm.providerName = provider.name;
      vm.loading = false;
      vm.debtsToPay = {
        provider: provider,
        typeTaxes: undefined,
        items: [],
        userClientId: vm.usrCliId
      };
      vm.ejercicio = ('0' + (today.getMonth() + 1)).slice(-2) + '/' +today.getFullYear();
      $('#pickerBillDatedebtsToPay').data("DateTimePicker").date(today);
      $('#pickerBillDatedebtsToPay').on("dp.change", function (data) {
        vm.ejercicio = ('0' + ($('#pickerBillDatedebtsToPay').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' +$('#pickerBillDateDocumentdebtsToPay').data("DateTimePicker").date().toDate().getFullYear();
      });
    };

    vm.getPercentageTable = function(){
      if(vm.debtsToPay.typeTaxes === '1'){
        cuentaContableService.getAll().then(function(response){
          vm.cuentaContableList = response;
        });
      };
    };

    vm.selectTaxes = function(q){
      vm.item = undefined;
      vm.item = {
        cta_contable: q.id,
        codigo: parseInt(vm.debtsToPay.typeTaxes),
        fecha_emision_documento: dateService.getIsoDate($('#pickerBillDatedebtsToPay').data("DateTimePicker").date().toDate()),
        codigo_contable: q.code,
        desc_contable: q.description,
        tipo: q.accountType,
        nomb_contable: q.name
      };
      console.log(vm.item);
      vm.subIva = (parseFloat(vm.debtsToPay.total) * 0.12).toFixed(2);
      vm.subTotal = (parseFloat(vm.debtsToPay.total) - vm.subIva).toFixed(2);
    };

    vm.addItem = function() {
      if (vm.indexEdit !== undefined) {
        vm.debtsToPay.items.splice(vm.indexEdit, 1);
        vm.indexEdit = undefined;
      };
      vm.debtsToPay.items.push(vm.item);
      vm.item = undefined;
      vm.debtsToPay.typeTaxes = undefined;
      vm.cuentaContableList = undefined;
    };

    vm.addIvaAndProvider = function() {
      if (vm.indexEdit !== undefined) {
        vm.debtsToPay.items.splice(vm.indexEdit, 1);
        vm.indexEdit = undefined;
      };
      vm.itemIva = {
        cta_contable: q.id,
        codigo: parseInt(vm.debtsToPay.typeTaxes),
        fecha_emision_documento: dateService.getIsoDate($('#pickerBillDatedebtsToPay').data("DateTimePicker").date().toDate()),
        codigo_contable: q.code,
        desc_contable: q.description,
        tipo: q.accountType,
        nomb_contable: q.name
      };
      vm.itemProvider = {
        cta_contable: q.id,
        codigo: parseInt(vm.debtsToPay.typeTaxes),
        fecha_emision_documento: dateService.getIsoDate($('#pickerBillDatedebtsToPay').data("DateTimePicker").date().toDate()),
        codigo_contable: q.code,
        desc_contable: q.description,
        tipo: q.accountType,
        nomb_contable: q.name
      };
      vm.debtsToPay.items.push(vm.itemIva);
      vm.debtsToPay.items.push(vm.itemProvider);
      vm.debtsToPay.typeTaxes = undefined;
    };

    vm.editItemTaxes = function(index) {
      vm.item = angular.copy(vm.debtsToPay.items[index]);
      vm.indexEdit = index;
    };

    vm.deleteItemTaxes = function(index) {
      vm.debtsToPay.items.splice(index, 1);
    };

    vm.getTotalCredito = function() {
      var totalCredito = 0;
      if (vm.debtsToPay) {
        _.each(vm.debtsToPay.items, function(detail) {
          if (detail.tipo === 'CREDITO (C)') {
            totalCredito = (parseFloat(totalCredito) + parseFloat(detail.base_imponible)).toFixed(2);
          };
        });
      };
      vm.saldoCredito = totalCredito;
      return totalCredito;
    };

    vm.getTotalDebito = function() {
      var totalDebito = 0;
      if (vm.debtsToPay) {
        _.each (vm.debtsToPay.items, function(detail) {
          if (detail.tipo === 'DEBITO (D)') {
            totalDebito = (parseFloat(totalDebito) + parseFloat(detail.base_imponible)).toFixed(2);
          };
        });
      };
      vm.saldoDebito = totalDebito;
      return totalDebito;
    };

    vm.getTotalSaldo = function() {
      var totalSaldo = 0;
      totalSaldo = (parseFloat(vm.saldoCredito) - parseFloat(vm.saldoDebito)).toFixed(2);
      return totalSaldo;
    };

    vm.saveDebtsToPay = function(debtsToPay) {
      vm.loading = true;
      vm.debtsToPay.date = $('#pickerBillDatedebtsToPay').data("DateTimePicker").date().toDate().getTime();
      vm.debtsToPay.billNumber = vm.debtsToPay.cashierNumber +'-'+ vm.debtsToPay.sequentialNumber +'-'+ vm.debtsToPay.establishmentNumber;
      vm.debtsToPay.providerId = vm.providerId;
      vm.debtsToPay.userClientId = vm.usrCliId;
      vm.debtsToPay.detailDebtsToPay = [];
      _.each (vm.debtsToPay.items, function(item) {
        var detail = {
          taxType: vm.debtsToPay.typeTaxes,
          codeConta: item.codigo_contable,
          baseImponible: item.base_imponible,
          name: item.nomb_contable,
          cuentaContableId: item.cta_contable
        };
        vm.debtsToPay.detailDebtsToPay.push(detail);
      });
      debtsToPayService.create(debtsToPay).then(function(respDebtsToPay) {
        vm.totalDebtsToPay = 0;
        vm.debtsToPay = respDebtsToPay;
        _.each (vm.debtsToPay.detailDebtsToPay, function(detail) {
          vm.totalDebtsToPay = (parseFloat(vm.totalDebtsToPay) + parseFloat(detail.baseImponible)).toFixed(2);
        });
        vm.debtsToPayCreated = true;
        vm.success = 'Factura almacenada con exito';
        vm.loading = false;
      }).catch (function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancel = function() {
      vm.debtsToPay = undefined;
      vm.success = undefined;
      vm.error = undefined;
      vm.providerSelected = undefined;
      vm.cuentaContableList = undefined;
    };

    (function initController() {
      _activate();
    })();
});
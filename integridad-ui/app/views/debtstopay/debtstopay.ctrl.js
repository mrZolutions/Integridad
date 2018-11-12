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
                                        cuentaContableService, validatorService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.usrCliId = undefined;
    vm.provider = undefined;
    vm.providerId = undefined;
    vm.subTotal = undefined;
    vm.cuentaCtableId = undefined;
    vm.cuentaContableList = undefined;
    vm.debstopayCreated = undefined;
    vm.providerSelected = undefined;
    vm.providerList = undefined;
    vm.saldoCredito = undefined;
    vm.saldoDebito = undefined;
    vm.saldo = undefined;

    vm.voucherType = [
      {code: '01', name: 'Factura'},
      {code: '02', name: 'Nota o boleta de venta'},
      {code: '03', name: 'Liquidación de compra de Bienes o Prestación de servicios'},
      {code: '04', name: 'Nota de crédito'},
      {code: '05', name: 'Nota de débito'},
      {code: '06', name: 'Guías de Remisión'},
      {code: '07', name: 'Comprobante de Retención'},
      {code: '08', name: 'Boletos o entradas a espectáculos públicos'},
      {code: '09', name: 'Tiquetes o vales emitidos por máquinas registradoras'},
      {code: '11', name: 'Pasajes expedidos por empresas de aviación'},
      {code: '12', name: 'Documentos emitidos por instituciones financieras'},
      {code: '15', name: 'Comprobante de venta emitido en el Exterior'},
      {code: '16', name: 'Formulario Único de Exportación (FUE) o Declaración Aduanera Única (DAU) o Declaración Andina de Valor (DAV)'},
      {code: '18', name: 'Documentos autorizados utilizados en ventas excepto N/C N/D'},
      {code: '19', name: 'Comprobantes de Pago de Cuotas o Aportes'},
      {code: '20', name: 'Documentos por Servicios Administrativos emitidos por Inst. del Estado'},
      {code: '21', name: 'Carta de Porte Aéreo'},
      {code: '22', name: 'RECAP'},
      {code: '23', name: 'Nota de Crédito TC'},
      {code: '24', name: 'Nota de Débito TC'},
      {code: '41', name: 'Comprobante de venta emitido por reembolso'},
      {code: '42', name: 'Documento retención presuntiva y retención emitida por propio vendedor o por intermediario'},
      {code: '43', name: 'Liquidación para Explotación y Exploracion de Hidrocarburos'},
      {code: '44', name: 'Comprobante de Contribuciones y Aportes'},
      {code: '45', name: 'Liquidación por reclamos de aseguradoras'}
    ];

    vm.supportType = [
      {code: '01', name: 'Crédito Tributario para declaración de IVA (servicios y bienes distintos de inventarios y activos fijos)'},
      {code: '02', name: 'Costo o Gasto para declaración de IR (servicios y bienes distintos de inventarios y activos fijos)'},
      {code: '03', name: 'Activo Fijo - Crédito Tributario para declaración de IVA'},
      {code: '04', name: 'Activo Fijo - Costo o Gasto para declaración de IR'},
      {code: '05', name: 'Liquidación Gastos de Viaje, hospedaje y alimentación Gastos IR (a nombre de empleados y no de la empresa)'},
      {code: '06', name: 'Inventario - Crédito Tributario para declaración de IVA'},
      {code: '07', name: 'Inventario - Costo o Gasto para declaración de IR'},
      {code: '08', name: 'Valor pagado para solicitar Reembolso de Gasto (intermediario)'},
      {code: '09', name: 'Reembolso por Siniestros'},
      {code: '10', name: 'Distribución de Dividendos, Beneficios o Utilidades'},
      {code: '11', name: 'Convenios de débito o recaudación para IFI´s'},
      {code: '12', name: 'Impuestos y retenciones presuntivos'},
      {code: '13', name: 'Valores reconocidos por entidades del sector público a favor de sujetos pasivos'},
      {code: '14', name: 'Valores facturados por socios a operadoras de transporte (que no constituyen gasto de dicha operadora)'},
      {code: '00', name: 'Casos especiales cuyo sustento no aplica en las opciones anteriores'}
    ];

    vm.formPayment = [
      {code: '01', name: 'SIN UTILIZACION DEL SISTEMA FINANCIERO'},
      {code: '02', name: 'CHEQUE PROPIO'},
      {code: '03', name: 'CHEQUE CERTIFICADO'},
      {code: '04', name: 'CHEQUE DE GERENCIA'},
      {code: '05', name: 'CHEQUE DEL EXTERIOR'},
      {code: '06', name: 'DÉBITO DE CUENTA'},
      {code: '07', name: 'TRANSFERENCIA PROPIO BANCO'},
      {code: '08', name: 'TRANSFERENCIA OTRO BANCO NACIONAL'},
      {code: '09', name: 'TRANSFERENCIA  BANCO EXTERIOR'},
      {code: '10', name: 'TARJETA DE CRÉDITO NACIONAL'},
      {code: '11', name: 'TARJETA DE CRÉDITO INTERNACIONAL'},
      {code: '12', name: 'GIRO'},
      {code: '13', name: 'DEPOSITO EN CUENTA (CORRIENTE/AHORROS)'},
      {code: '14', name: 'ENDOSO DE INVERSIÒN'},
      {code: '15', name: 'COMPENSACIÓN DE DEUDAS'},
      {code: '16', name: 'TARJETA DE DÉBITO'},
      {code: '17', name: 'DINERO ELECTRÓNICO'},
      {code: '18', name: 'TARJETA PREPAGO'},
      {code: '19', name: 'TARJETA DE CRÉDITO'},
      {code: '20', name: 'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO'},
      {code: '21', name: 'ENDOSO DE TÍTULOS'}
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
      vm.debstopayCreated = undefined;
      vm.providerId = provider.id;
      vm.providerName = provider.name;
      vm.loading = false;
      vm.debstopay = {
        provider: provider,
        typeTaxes: undefined,
        items: [],
        userClientId: vm.usrCliId
      };
      vm.ejercicio = ('0' + (today.getMonth() + 1)).slice(-2) + '/' +today.getFullYear();
      $('#pickerBillDateDocumentDebsToPay').data("DateTimePicker").date(today);
      $('#pickerBillDateDocumentDebsToPay').on("dp.change", function (data) {
        vm.ejercicio = ('0' + ($('#pickerBillDateDocumentDebsToPay').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' +$('#pickerBillDateDocumentDebsToPay').data("DateTimePicker").date().toDate().getFullYear();
      });
    };

    vm.getPercentageTable = function(){
      if(vm.debstopay.typeTaxes === '1'){
        cuentaContableService.getAll().then(function(response){
          vm.cuentaContableList = response;
        });
      };
    };

    vm.selectTaxes = function(q){
      vm.item = undefined;
      vm.cuentaCtableId = q.id;
      vm.item = {
        codigo: parseInt(vm.debstopay.typeTaxes),
        codigo_contable: q.code,
        desc_contable: q.description,
        tipo: q.accountType,
        nomb_contable: q.name
      };
      vm.item.base_imponible = (parseFloat(vm.debstopay.total) - (parseFloat(vm.debstopay.total) * 0.12)).toFixed(2);
      vm.subTotal = vm.item.base_imponible;
    };

    vm.addItem = function(){
      if(vm.indexEdit !== undefined){
        vm.debstopay.items.splice(vm.indexEdit, 1);
        vm.indexEdit = undefined;
      };
      vm.debstopay.items.push(vm.item);
      vm.item = undefined;
      vm.debstopay.typeTaxes = undefined;
      vm.cuentaContableList = undefined;
    };

    vm.editItemTaxes = function(index){
      vm.item = angular.copy(vm.debstopay.items[index]);
      vm.indexEdit = index;
    };

    vm.deleteItemTaxes = function(index){
      vm.debstopay.items.splice(index, 1);
    };

    vm.getTotalCredito = function(){
      var totalCredito = 0;
      if(vm.debstopay){
        _.each(vm.debstopay.items, function(detail){
          if (detail.tipo === 'CREDITO (C)'){
            totalCredito = (parseFloat(totalCredito) + parseFloat(detail.base_imponible)).toFixed(2);
          };
        });
      };
      vm.saldoCredito = totalCredito;
      return totalCredito;
    };

    vm.getTotalDebito = function(){
      var totalDebito = 0;
      if(vm.debstopay){
        _.each(vm.debstopay.items, function(detail){
          if (detail.tipo === 'DEBITO (D)'){
            totalDebito = (parseFloat(totalDebito) + parseFloat(detail.base_imponible)).toFixed(2);
          };
        });
      };
      vm.saldoDebito = totalDebito;
      return totalDebito;
    };

    vm.getTotalSaldo = function(){
      var totalSaldo = 0;
      totalSaldo = (parseFloat(vm.saldoCredito) - parseFloat(vm.saldoDebito)).toFixed(2);
      return totalSaldo;
    };

    vm.createDetailFactura = function(prov){
      
      vm.debstopayCreated = false;
    };

    (function initController(){
      _activate();
    })();
});
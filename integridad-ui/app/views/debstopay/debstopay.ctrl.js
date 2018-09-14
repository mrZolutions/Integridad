'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebsToPayCtrl
 * @description
 * # DebsToPayCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('DebsToPayCtrl', function (_, $localStorage, debstopayService,providerService, utilStringService, dateService, utilSeqService, validatorService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.userData = $localStorage.user;
    vm.provider = undefined;
    vm.providerToUse = undefined;
    vm.providerList = undefined;
    vm.providerType = [
      'PROVEEDORES LOCALES O NACIONALES 01',
      'PROVEEDORES DEL EXTERIOR 02',
    ];

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
      vm.providerToUse = undefined;
      vm.providerList = [];
      providerService.getLazyByUserClientId(vm.userData.subsidiary.userClient.id).then(function(response){
        vm.providerList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function create(){
      providerService.create(vm.provider).then(function (response) {
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function update(){
      providerService.update(vm.provider).then(function (response) {
        if(vm.provider.active){
          vm.success = 'Registro actualizado con exito';
        } else {
          vm.success = 'Registro eliminado con exito';
        }
        _activate();
        vm.error = undefined;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.register = function(){
      var idValid = true;
      if(vm.provider.ruc.length > 10){
        idValid = validatorService.isRucValid(vm.provider.ruc);
      } else {
        idValid = validatorService.isCedulaValid(vm.provider.ruc);
      }
      if(!idValid){
        vm.error = 'IDENTIFICACION INVALIDA';
      } else if(vm.provider.id){
        update();
      } else {
        create();
      }
    };

    vm.providerCreate = function(){
      vm.error = undefined;
      vm.success = undefined;
      vm.provider = {
        codeIntegridad: vm.providerList.length + 1,
        active: true,
        userClient: vm.userData.subsidiary.userClient
      };
    };

    vm.cancel = function(){
      vm.provider = undefined;
    };

    vm.disableSave = function(){
      if(vm.provider){
        return utilStringService.isAnyInArrayStringEmpty([
          vm.provider.codeIntegridad,
          vm.provider.ruc,
          vm.provider.name,
          vm.provider.razonSocial,
          vm.provider.country,
          vm.provider.city,
          vm.provider.address1,
          vm.provider.contact,
          vm.provider.providerType
        ]);
      }
    };

    vm.getTaxesTable = function(){
      vm.taxesTable = undefined;
      if(vm.debsToPay.typeTaxes === '1'){
        vm.taxesTable = vm.ivaTipo;
      };
      if(vm.debsToPay.typeTaxes === '2'){
        vm.taxesTable = vm.fuenteTipo;
      };
    };

});
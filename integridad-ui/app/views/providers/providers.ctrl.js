'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProvidersCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProvidersCtrl', function (_, $localStorage, $location, providerService, utilStringService, dateService, eretentionService, utilSeqService, validatorService) {
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

    vm.documentType = [
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
      {code: 'B', name: 'BIENES'},
      {code: 'S', name: 'SERVICIOS'},
      {code: 'M', name: 'MATERIA PRIMA'},
      {code: 'C', name: 'CONSUMIBLES'},
      {code: 'R', name: 'REEMBOLSO DE GASTOS'},
      {code: 'TAE', name: 'TIKETS AEREOS'}
    ];

    vm.fuenteTipo = [
      {name:'Honorarios profesionales y demás pagos por servicios relacionados con el título profesional' ,percentage:10, codigo: '303', codigoDatil:'303'},
      {name:'Servicios predomina el intelecto no relacionados con el título profesional' ,percentage:8, codigo: '304', codigoDatil:'304'},
      {name:'Comisiones y demás pagos por servicios predomina intelecto no relacionados con el título profesional' ,percentage:8, codigo: '304A', codigoDatil:'304A'},
      {name:'Pagos a notarios y registradores de la propiedad y mercantil por sus actividades ejercidas como tales' ,percentage:8, codigo: '304B', codigoDatil:'304B'},
      {name:'Pagos a deportistas, entrenadores, árbitros, miembros del cuerpo técnico por sus actividades ejercidas como tales' ,percentage:8, codigo: '304C', codigoDatil:'304C'},
      {name:'Pagos a artistas por sus actividades ejercidas como tales' ,percentage:8, codigo: '304D', codigoDatil:'304D'},
      {name:'Honorarios y demás pagos por servicios de docencia' ,percentage:8, codigo: '304E', codigoDatil:'304E'},
      {name:'Servicios predomina la mano de obra' ,percentage:2, codigo: '307', codigoDatil:'307'},
      {name:'Utilización o aprovechamiento de la imagen o renombre' ,percentage:10, codigo: '308', codigoDatil:'308'},
      {name:'Servicios prestados por medios de comunicación y agencias de publicidad' ,percentage:1, codigo: '309', codigoDatil:'309'},
      {name:'Servicio de transporte privado de pasajeros o transporte público o privado de carga' ,percentage:1, codigo: '310', codigoDatil:'310'},
      {name:'Por pagos a través de liquidación de compra (nivel cultural o rusticidad) **' ,percentage:2, codigo: '311', codigoDatil:'311'},
      {name:'Transferencia de bienes muebles de naturaleza corporal' ,percentage:1, codigo: '312', codigoDatil:'312'},
      {name:'Compra de bienes de origen agrícola, avícola, pecuario, apícola, cunícula, bioacuático, y forestal' ,percentage:1, codigo: '312A', codigoDatil:'312A'},
      {name:'Regalías por concepto de franquicias de acuerdo a Ley de Propiedad Intelectual - pago a personas naturales' ,percentage:8, codigo: '314A', codigoDatil:'314A'},
      {name:'Cánones, derechos de autor,  marcas, patentes y similares de acuerdo a Ley de Propiedad Intelectual – pago a personas naturales' ,percentage:8, codigo: '314B', codigoDatil:'314B'},
      {name:'Regalías por concepto de franquicias de acuerdo a Ley de Propiedad Intelectual  - pago a sociedades' ,percentage:8, codigo: '314C', codigoDatil:'314C'},
      {name:'Cánones, derechos de autor,  marcas, patentes y similares de acuerdo a Ley de Propiedad Intelectual – pago a sociedades' ,percentage:8, codigo: '314D', codigoDatil:'314D'},
      {name:'Cuotas de arrendamiento mercantil, inclusive la de opción de compra' ,percentage:1, codigo: '319', codigoDatil:'319'},
      {name:'Por arrendamiento bienes inmuebles' ,percentage:8, codigo: '320', codigoDatil:'320'},
      {name:'Seguros y reaseguros (primas y cesiones)' ,percentage:1, codigo: '322', codigoDatil:'322'},
      {name:'Por rendimientos financieros pagados a naturales y sociedades  (No a IFIs)' ,percentage:2, codigo: '323', codigoDatil:'323'},
      {name:'Por RF: depósitos Cta. Corriente' ,percentage:2, codigo: '323A', codigoDatil:'323A'},
      {name:'Por RF:  depósitos Cta. Ahorros Sociedades' ,percentage:2, codigo: '323B1', codigoDatil:'323B1'},
      {name:'Por RF: depósito a plazo fijo  gravados' ,percentage:2, codigo: '323E', codigoDatil:'323E'},
      {name:'Por RF: depósito a plazo fijo exentos ***' ,percentage:0, codigo: '32300', codigoDatil:'32300'},
      {name:'Por rendimientos financieros: operaciones de reporto - repos' ,percentage:2, codigo: '323F', codigoDatil:'323F'},
      {name:'Por RF: inversiones (captaciones) rendimientos distintos de aquellos pagados a IFIs' ,percentage:2, codigo: '323G', codigoDatil:'323G'},
      {name:'Por RF: obligaciones' ,percentage:2, codigo: '323H', codigoDatil:'323H'},
      {name:'Por RF: bonos convertible en acciones' ,percentage:2, codigo: '323I', codigoDatil:'323I'},
      {name:'Por RF: Inversiones en títulos valores en renta fija gravados ' ,percentage:2, codigo: '323M', codigoDatil:'323M'},
      {name:'Por RF: Inversiones en títulos valores en renta fija exentos' ,percentage:0, codigo: '323N', codigoDatil:'323N'},
      {name:'Por RF: Intereses pagados a bancos y otras entidades sometidas al control de la Superintendencia de Bancos y de la Economía Popular y Solidaria' ,percentage:0, codigo: '323O', codigoDatil:'323O'},
      {name:'Por RF: Intereses pagados por entidades del sector público a favor de sujetos pasivos' ,percentage:2, codigo: '323P', codigoDatil:'323P'},
      {name:'Por RF: Otros intereses y rendimientos financieros gravados ' ,percentage:2, codigo: '323Q', codigoDatil:'323Q'},
      {name:'Por RF: Otros intereses y rendimientos financieros exentos' ,percentage:0, codigo: '323R', codigoDatil:'323R'},
      {name:'Por RF: Intereses y comisiones en operaciones de crédito entre instituciones del sistema financiero y entidades economía popular y solidaria.' ,percentage:1, codigo: '324A', codigoDatil:'324A'},
      {name:'Por RF: Por inversiones entre instituciones del sistema financiero y entidades economía popular y solidaria, incluso cuando el BCE actúe como intermediario.' ,percentage:1, codigo: '324B', codigoDatil:'324B'},
      {name:'Anticipo dividendos a residentes o establecidos en el Ecuador' ,percentage:22, codigo: '325', codigoDatil:'325'},
      {name:'Dividendos anticipados préstamos accionistas, beneficiarios o partìcipes a residentes o establecidos en el Ecuador' ,percentage:22, codigo: '325A', codigoDatil:'325A'},
      {name:'Dividendos distribuidos a sociedades residentes' ,percentage:0, codigo: '328', codigoDatil:'328'},
      {name:'Dividendos distribuidos a fideicomisos residentes' ,percentage:0, codigo: '329', codigoDatil:'329'},
      {name:'Dividendos exentos distribuidos en acciones (reinversión de utilidades con derecho a reducción tarifa IR) ' ,percentage:0, codigo: '331', codigoDatil:'331'},
      {name:'Otras compras de bienes y servicios no sujetas a retención' ,percentage:0, codigo: '332', codigoDatil:'332'},
      {name:'Enajenación de derechos representativos de capital y otros derechos exentos (mayo 2016)' ,percentage:0, codigo: '332A', codigoDatil:'332A'},
      {name:'Compra de bienes inmuebles' ,percentage:0, codigo: '332B', codigoDatil:'332B'},
      {name:'Transporte público de pasajeros' ,percentage:0, codigo: '332C', codigoDatil:'332C'},
      {name:'Pagos en el país por transporte de pasajeros o transporte internacional de carga, a compañías nacionales o extranjeras de aviación o marítimas' ,percentage:0, codigo: '332D', codigoDatil:'332D'},
      {name:'Valores entregados por las cooperativas de transporte a sus socios' ,percentage:0, codigo: '332E', codigoDatil:'332E'},
      {name:'Compraventa de divisas distintas al dólar de los Estados Unidos de América' ,percentage:0, codigo: '332F', codigoDatil:'332F'},
      {name:'Pago al exterior tarjeta de crédito reportada por la Emisora de tarjeta de crédito, solo RECAP' ,percentage:0, codigo: '332H', codigoDatil:'332H'},
      {name:'Enajenación de derechos representativos de capital y otros derechos cotizados en bolsa ecuatoriana' ,percentage:0.002, codigo: '333', codigoDatil:'333'},
      {name:'Enajenación de derechos representativos de capital y otros derechos no cotizados en bolsa ecuatoriana' ,percentage:1, codigo: '334', codigoDatil:'334'},
      {name:'Por loterías, rifas, apuestas y similares' ,percentage:15, codigo: '335', codigoDatil:'335'},
      {name:'Por energía eléctrica' ,percentage:1, codigo: '343A', codigoDatil:'343A'},
      {name:'Por actividades de construcción de obra material inmueble, urbanización, lotización o actividades similares' ,percentage:1, codigo: '343B', codigoDatil:'343B'},
      {name:'Otras retenciones aplicables el 2%' ,percentage:2, codigo: '344', codigoDatil:'344'},
      {name:'Pago local tarjeta de crédito reportada por la Emisora de tarjeta de crédito, solo RECAP' ,percentage:2, codigo: '344A', codigoDatil:'344A'},
      {name:'Donaciones en dinero -Impuesto a la donaciones ' ,percentage:2, codigo: '347', codigoDatil:'347'},
      {name:'Pago al exterior - Rentas Inmobiliarias' ,percentage:22, codigo: '500', codigoDatil:'500'},
      {name:'Pago al exterior - Beneficios Empresariales' ,percentage:22, codigo: '501', codigoDatil:'501'},
      {name:'Pago al exterior - Servicios Empresariales' ,percentage:22, codigo: '502', codigoDatil:'502'},
      {name:'Pago al exterior - Navegación Marítima y/o aérea' ,percentage:22, codigo: '503', codigoDatil:'503'},
      {name:'Pago al exterior- Dividendos distribuidos a personas naturales' ,percentage:0, codigo: '504', codigoDatil:'504'}
    ];
    // vm.ivaTipo = [
    //   {name:'RETENCION DEL 10%' ,percentage:10, codigo:'721'},
    //   {name:'RETENCION DEL 20%' ,percentage:20, codigo:'723'},
    //   {name:'RETENCION DEL 30%' ,percentage:30, codigo:'725'},
    //   {name:'RETENCION DEL 50%' ,percentage:50, codigo:'727'},
    //   {name:'RETENCION DEL 70%' ,percentage:70, codigo:'729'},
    //   {name:'RETENCION DEL 100%' ,percentage:100, codigo:'731'}
    // ];
    vm.ivaTipo = [
      {name:'RETENCION DEL 10%' ,percentage:10, codigo:'721', codigoDatil:'9'},
      {name:'RETENCION DEL 20%' ,percentage:20, codigo:'723', codigoDatil:'10'},
      {name:'RETENCION DEL 30%' ,percentage:30, codigo:'725', codigoDatil:'1'},
      {name:'RETENCION DEL 50%' ,percentage:50, codigo:'727', codigoDatil:'9'},
      {name:'RETENCION DEL 70%' ,percentage:70, codigo:'729', codigoDatil:'2'},
      {name:'RETENCION DEL 100%' ,percentage:100, codigo:'731', codigoDatil:'3'}
    ];

    vm.ctasctables = [
      {code: '1.01.05.01.01', desc: 'IVA EN COMPRAS', tipo:'DEBITO (D)', name: 'DEFINIDA PARA TODAS LAS COMPRAS'},
      {code: '2.01.03.01.01', desc: 'PROVEEDORES LOCALES', tipo:'CREDITO (C)', name: 'DEFINIDA PARA TODOS LOS PROVEEDORES'},
      {code: '6.1.03.03', desc: 'SUMINISTROS MATERIALES', tipo:'DEBITO (D)', name: 'COMPRAS PARA INVENTARIO'},
      {code: '6.2.04.04', desc: 'SUMINISTROS DE OFICINA QUITO', tipo:'DEBITO (D)', name: 'SUMINISTROS DE PAPELERIA-COMPUTACION- OTROS'},
      {code: '6.1.03.17', desc: 'SERVICIOS BASICOS COCA', tipo:'DEBITO (D)', name: 'PAGOS BASE COCA - AGUA - LUZ- TELEFONO - INTERNET - OTROS'},
      {code: '6.1.03.18', desc: 'MATERIALES PARA OBRAS', tipo:'DEBITO (D)', name: 'COMPRA DE MATERIALES Y HERRAMIENTAS PARA TRABAJOS- INCLUYE FERRETERIA'},
      {code: '6.03.100.3', desc: 'COMPRAS DE INVENTARIOS', tipo:'DEBITO (D)', name: 'RESORTES - VALVULAS - ACOPLES'}
    ];

    function _activate(){
      vm.today = new Date();
      vm.provider = undefined;
      vm.providerToUse = undefined;
      vm.providerList = [];
      providerService.getLazyByUserClientId(vm.userData.subsidiary.userClient.id).then(function(response){
        vm.providerList = response;
        vm.loading = false;
      }).catch(function (error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function create(){
      providerService.create(vm.provider).then(function(response) {
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
      }).catch(function (error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function update(){
      providerService.update(vm.provider).then(function(response) {
        if(vm.provider.active){
          vm.success = 'Registro actualizado con exito';
        } else {
          vm.success = 'Registro eliminado con exito';
        };
        _activate();
        vm.error = undefined;
      }).catch(function(error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function _getSeqNumber(){
      vm.numberAddedOne = parseInt($localStorage.user.cashier.retentionNumberSeq) + 1;
      vm.seqNumberFirstPart = $localStorage.user.subsidiary.threeCode + '-'
        + $localStorage.user.cashier.threeCode;
      vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
      vm.seqNumber =  vm.seqNumberFirstPart + '-'
        + vm.seqNumberSecondPart;
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

    vm.editProvider = function(providerEdit){
      vm.error = undefined;
      vm.success = undefined;
      vm.provider = providerEdit;
    };

    vm.disableSave = function(){
      if(vm.provider){
        return utilStringService.isAnyInArrayStringEmpty([
          vm.provider.codeIntegridad,
          vm.provider.ruc,
          vm.provider.ruc_type,
          vm.provider.name,
          vm.provider.razonSocial,
          vm.provider.country,
          vm.provider.city,
          vm.provider.address1,
          vm.provider.contact,
          vm.provider.providerType
        ]);
      };
    };

    vm.cancel = function(){
      vm.provider = undefined;
    };

    vm.register = function(){
      var idValid = true;
      if(vm.provider.ruc.length > 10){
        idValid = validatorService.isRucValid(vm.provider.ruc);
      } else {
        idValid = validatorService.isCedulaValid(vm.provider.ruc);
      };
      if(vm.provider.id){
        update();
      } else {
        create();
      };
    };

    vm.remove = function(){
      vm.provider.active=false;
      update();
    };

    vm.createRetention = function(prov){
      var today = new Date();
      vm.retentionCreated = false;
      vm.retention = {
        provider: prov,
        typeRetention: undefined,
        items: [],
        ejercicio: ('0' + (today.getMonth() + 1)).slice(-2) + '/' +today.getFullYear()
      };
      $('#pickerBillDateRetention').data("DateTimePicker").date(today);
      $('#pickerBillDateDocumentRetention').data("DateTimePicker").date(today);
      $('#pickerBillDateDocumentRetention').on("dp.change", function (data) {
        vm.retention.ejercicio = ('0' + ($('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' +$('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getFullYear();
      });
      _getSeqNumber();
    };

    vm.createDetailFactura = function(prov){
      var today = new Date();
      vm.debstopayCreated = false;
      vm.debstopay = {
        provider: prov,
        typeTaxes: undefined,
        items: [],
        ejercicio: ('0' + (today.getMonth() + 1)).slice(-2) + '/' +today.getFullYear()
      };
      $('#pickerBillDateDocumentDebsToPay').data("DateTimePicker").date(today);
      $('#pickerBillDateDocumentDebsToPay').on("dp.change", function (data) {
        vm.debstopay.ejercicio = ('0' + ($('#pickerBillDateDocumentDebsToPay').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' +$('#pickerBillDateDocumentDebsToPay').data("DateTimePicker").date().toDate().getFullYear();
      });
    };

    vm.getPercentageTable = function(){
      vm.tablePercentage = undefined;
      if(vm.retention.typeRetention === '2'){
        vm.tablePercentage = vm.ivaTipo;
      };
      if(vm.retention.typeRetention === '1'){
        vm.tablePercentage = vm.fuenteTipo;
      };
    };

    vm.getTaxesTable = function(){
      vm.tableTaxes = undefined;
      if(vm.debstopay.typeTaxes === '1'){
        vm.tableTaxes = vm.ctasctables;
      };
    };

    vm.selecPercentage =function(percentage){
      vm.baseImponibleItem = undefined;
      vm.item = undefined;
      vm.item = {
        codigo: parseInt(vm.retention.typeRetention),
        fecha_emision_documento_sustento: dateService.getIsoDate($('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate()),
        numero_documento_sustento: vm.retention.numero,
        codigo_porcentaje: percentage.codigoDatil,
        codigo_porcentaje_integridad: percentage.codigo,
        porcentaje: percentage.percentage,
        tipo_documento_sustento: vm.docType
      };
    };

    vm.selecTaxes = function(taxes){
      vm.item = undefined;
      vm.item = {
        codigo: parseInt(vm.debstopay.typeTaxes),
        numero_documento_sustento:vm.debstopay.numero,
        name: vm.debstopay.name,
        tipo_documento_comprobante: vm.docType1,
        tipo_documento_ctascontables: vm.docType2,
        tipo_documento_purchase: vm.docType3,
        tipo_documento_sustento: vm.docType
      };
    };

    vm.addItem = function(){
      vm.item.valor_retenido = (parseFloat(vm.item.base_imponible) * (parseFloat(vm.item.porcentaje)/100)).toFixed(2);
      if(vm.indexEdit !== undefined){
        vm.retention.items.splice(vm.indexEdit, 1);
        vm.indexEdit = undefined
      };
      vm.retention.retentionSeq = vm.seqNumber;
      vm.retention.items.push(vm.item);
      vm.item = undefined;
      vm.retention.typeRetention = undefined;
      vm.tablePercentage = undefined;
    };

    vm.editItem = function(index){
      vm.item = angular.copy(vm.retention.items[index]);
      vm.indexEdit = index;
    };

    vm.editItemTaxes = function(index){
      vm.item = angular.copy(vm.debstopay.items[index]);
      vm.indexEdit = index;
    };

    vm.deleteItem = function(index){
      vm.retention.items.splice(index, 1);
    };

    vm.getTotalRetenciones = function(){
      var totalRetorno = 0;
      if(vm.retention){
        _.each(vm.retention.items, function(detail){
          totalRetorno = (parseFloat(totalRetorno) +parseFloat(detail.valor_retenido)).toFixed(2);
        });
      };
      return totalRetorno;
    };

    vm.previsualisation = function(){
      vm.retention.documentDate = $('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getTime();
      vm.retention.stringSeq = vm.retention.retentionSeq;
      vm.retention.detailRetentions = [];
      vm.retention.ejercicioFiscal = vm.retention.ejercicio;
      vm.retention.documentNumber = vm.retention.documentNumber;
      vm.totalRetention = 0;
      _.each(vm.retention.items, function(item){
        var detail = {
          taxType: item.codigo === 1 ? 'RETENCION EN LA FUENTE' : 'RETENCION EN EL IVA',
          code: item.codigo_porcentaje_integridad,
          baseImponible: item.base_imponible,
          percentage: item.porcentaje,
          total: item.valor_retenido
        };
        vm.totalRetention = (parseFloat(vm.totalRetention) +parseFloat(detail.total)).toFixed(2);
        vm.retention.detailRetentions.push(detail);
      });
    };

    vm.getClaveAcceso = function(){
      vm.loading = true;
      var eRet = eretentionService.createERetention(vm.retention, $localStorage.user);
      eretentionService.getClaveDeAcceso(eRet, $localStorage.user.subsidiary.userClient.id).then(function(resp){
        var obj = JSON.parse(resp.data);
        //var obj = {clave_acceso: '1234560', id:'id12345'};
        if(obj.errors === undefined){
          vm.retention.claveDeAcceso = obj.clave_acceso;
          vm.retention.idSri = obj.id;
          vm.retention.stringSeq = vm.retention.retentionSeq;
          vm.retention.retSeq = eRet.secuencial;
          vm.retention.ejercicioFiscal = vm.retention.ejercicio;
          vm.retention.documentNumber = vm.retention.numero;
          vm.retention.documentDate = $('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getTime()
          vm.retention.userIntegridad = $localStorage.user;
          vm.retention.subsidiary = $localStorage.user.subsidiary;
          vm.retention.detailRetentions = [];
          _.each(vm.retention.items, function(item){
            var detail = {
              taxType: item.codigo === String(1) ? 'RETENCION EN LA FUENTE' : 'RETENCION EN EL IVA',
              code: item.codigo_porcentaje_integridad,
              baseImponible: item.base_imponible,
              percentage: item.porcentaje,
              total: item.valor_retenido
            };
            vm.retention.detailRetentions.push(detail);
          });

          eretentionService.create(vm.retention).then(function(respRetention){
            vm.retention = respRetention;
            vm.totalRetention = 0;
            _.each(vm.retention.detailRetentions, function(detail){
              vm.totalRetention = (parseFloat(vm.totalRetention) +parseFloat(detail.total)).toFixed(2);
            });
            vm.retentionCreated = true;
            $localStorage.user.cashier.retentionNumberSeq = parseInt($localStorage.user.cashier.retentionNumberSeq) + 1;
            vm.loading = false;
          }).catch(function (error){
            vm.loading = false;
            vm.error = error;
          });
        } else {
          vm.loading = false;
          vm.error = "Error al obtener Clave de Acceso: " + JSON.stringify(obj.errors);
        };
      }).catch(function (error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.cancelRetentionCreated = function(){
      vm.retentionCreated = false;
      vm.retention = undefined
    };

    (function initController(){
      _activate();
    })();
});

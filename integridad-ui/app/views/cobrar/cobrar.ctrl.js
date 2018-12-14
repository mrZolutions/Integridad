'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasCobrarCtrl
 * @description
 * # CuentasCobrarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('CuentasCobrarCtrl', function ( _, $localStorage, clientService, cuentaContableService, paymentService, dateService, 
                                            creditsbillService, authService, billService, eretentionClientService){
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.clientList = undefined;
    vm.creditsbillList = undefined;
    vm.clientName = undefined;
    vm.clientId = undefined;
    

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

    vm.ivaTipo = [
      {name:'RETENCION DEL 10%' ,percentage:10, codigo:'721', codigoDatil:'9'},
      {name:'RETENCION DEL 20%' ,percentage:20, codigo:'723', codigoDatil:'10'},
      {name:'RETENCION DEL 30%' ,percentage:30, codigo:'725', codigoDatil:'1'},
      {name:'RETENCION DEL 50%' ,percentage:50, codigo:'727', codigoDatil:'9'},
      {name:'RETENCION DEL 70%' ,percentage:70, codigo:'729', codigoDatil:'2'},
      {name:'RETENCION DEL 100%' ,percentage:100, codigo:'731', codigoDatil:'3'}
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
      {name:'Pago al exterior - Beneficios Empresariales' ,percentage:25, codigo: '501', codigoDatil:'501'},
      {name:'Pago al exterior - Servicios Empresariales' ,percentage:22, codigo: '502', codigoDatil:'502'},
      {name:'Pago al exterior - Navegación Marítima y/o aérea' ,percentage:22, codigo: '503', codigoDatil:'503'},
      {name:'Pago al exterior- Dividendos distribuidos a personas naturales' ,percentage:0, codigo: '504', codigoDatil:'504'}
    ];


    function _activate() {
      vm.today = new Date();
      vm.clientBill = undefined;
      vm.success = undefined;
      vm.billList = undefined;
      vm.retentionClient = undefined;
      vm.clientList = [];
      vm.clientSelected = undefined;
      clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function(response) {
        vm.clientList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
      cuentaContableService.getAll().then(function(response) {
        vm.cuentaContableList = response;
      });
    };

    vm.clientConsult = function(client) {
      vm.loading = true;
      vm.success = undefined;
      vm.clientId = client.identification;
      vm.clientName = client.name;
      vm.clientCodConta = client.codConta;
      vm.clientAddress = client.address;
      vm.clientPhone = client.cel_phone;
      vm.clientEmail = client.email;
      billService.getBillsByClientId(client.id).then(function(response) {
        vm.billList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.creditsByBill = function(bill) {
      vm.loading = true;
      vm.success = undefined;
      vm.error = undefined;
      vm.billNumber = bill.stringSeq;
      vm.billValue = (bill.total).toFixed(2);
      creditsbillService.getAllCreditsOfBillById(bill.id).then(function(response) {
        vm.creditsbillList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.createAbono = function(credits) {
      vm.loading = true;
      vm.creditValue = (credits.valor).toFixed(2);
      vm.creditsId = credits.id;
      vm.payment = {
        credits: credits
      };
      vm.loading = false;
    };

    vm.pAbono = function(payment) {
      vm.loading = true;
      if (vm.payment.typePayment == 'PAC') {
        vm.valorReten = 0.00;
        vm.valorNotac = 0.00;
      } else if (vm.payment.typePayment == 'NTC') {
        vm.valorAbono = 0.00;
        vm.valorReten = 0.00;
        vm.payment.modePayment = 'NTC';
      };
      vm.payment.datePayment = $('#pickerDateOfPayment').data("DateTimePicker").date().toDate().getTime();
      vm.payment.creditId = vm.creditsId;
      vm.payment.documentNumber = vm.billNumber;
      vm.payment.valorReten = vm.valorReten;
      paymentService.create(payment).then(function(response) {
        vm.error = undefined;
        vm.success = 'Abono realizado con exito';
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
      _activate();
    };

//Inicio de Creación de Retenciones...
    vm.createRetentionClient = function(bill) {
      var today = new Date();
      vm.success = undefined;
      vm.retentionClientCreated = false;
      vm.billNumber = bill.stringSeq;
      vm.creditValue = bill.total;
      vm.creditValueSubtotal = bill.baseTaxes;
      vm.creditValueIva = bill.iva;
      vm.BillId = bill.id;
      vm.retentionClient = {
        bill: bill,
        typeRetention: undefined,
        items: [],
        ejercicio: ('0' + (today.getMonth() + 1)).slice(-2) + '/' + today.getFullYear()
      };
      $('#pickerDateToday').data("DateTimePicker").date(today);
      $('#pickerDateRetention').data("DateTimePicker").date(today);
      $('#pickerDateRetention').on("dp.change", function (data) {
        vm.retentionClient.ejercicio = ('0' + ($('#pickerDateRetention').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' + $('#pickerDateRetention').data("DateTimePicker").date().toDate().getFullYear();
      });
    };

    vm.getPercentageTable = function() {
      vm.tablePercentage = undefined;
      if (vm.retentionClient.typeRetention === '2') {
        vm.tablePercentage = vm.ivaTipo;
      };
      if (vm.retentionClient.typeRetention === '1') {
        vm.tablePercentage = vm.fuenteTipo;
      };
    };

    vm.selecPercentage =function(percentage) {
      vm.item = undefined;
      vm.item = {
        codigo: parseInt(vm.retentionClient.typeRetention),
        fecha_emision_documento_sustento: dateService.getIsoDate($('#pickerDateRetention').data("DateTimePicker").date().toDate()),
        numero_documento_sustento: vm.retentionClient.numero,
        codigo_porcentaje: percentage.codigoDatil,
        codigo_porcentaje_integridad: percentage.codigo,
        porcentaje: percentage.percentage,
        tipo_documento_sustento: vm.docType
      };
    };

    vm.addItem = function() {
      vm.item.valor_retenido = (parseFloat(vm.item.base_imponible) * (parseFloat(vm.item.porcentaje)/100)).toFixed(2);
      if (vm.indexEdit !== undefined) {
        vm.retentionClient.items.splice(vm.indexEdit, 1);
        vm.indexEdit = undefined
      };
      vm.retentionClient.retentionDoc = vm.billNumber;
      vm.retentionClient.items.push(vm.item);
      vm.item = undefined;
      vm.retentionClient.typeRetention = undefined;
      vm.tablePercentage = undefined;
    };

    vm.editItem = function(index) {
      vm.item = angular.copy(vm.retentionClient.items[index]);
      vm.indexEdit = index;
    };

    vm.deleteItem = function(index) {
      vm.retentionClient.items.splice(index, 1);
    };

    vm.getTotalRetencionesClient = function(){
      var totalRetorno = 0;
      if (vm.retentionClient) {
        _.each(vm.retentionClient.items, function(detail) {
          totalRetorno = (parseFloat(totalRetorno) +parseFloat(detail.valor_retenido)).toFixed(2);
        });
      };
      return totalRetorno;
    };

    vm.previewRetentionClient = function() {
      vm.retentionClient.documentDate = $('#pickerDateRetention').data("DateTimePicker").date().toDate().getTime();
      vm.retentionClient.ejercicioFiscal = vm.retentionClient.ejercicio;
      vm.retentionClient.documentNumber = vm.billNumber;
      vm.retentionClient.retentionNumber = vm.retentionClient.numero;
      vm.retentionClient.BillId = vm.BillId;
      vm.retentionClient.detailRetentionClient = [];
      vm.totalRetention = 0;
      _.each(vm.retentionClient.items, function(item) {
        var detail = {
          taxType: item.codigo === 1 ? 'RETENCION EN LA FUENTE' : 'RETENCION EN EL IVA',
          code: item.codigo_porcentaje_integridad,
          baseImponible: item.base_imponible,
          percentage: item.porcentaje,
          total: item.valor_retenido
        };
        vm.totalRetention = (parseFloat(vm.totalRetention) + parseFloat(detail.total)).toFixed(2);
        vm.retentionClient.detailRetentionClient.push(detail);
      });
    };

    vm.saveRetentionClient = function(retentionClient) {
      vm.loading = true;
      vm.success = undefined;
      vm.retentionClient.ejercicioFiscal = vm.retentionClient.ejercicio;
      vm.retentionClient.documentNumber = vm.billNumber;
      vm.retentionClient.retentionNumber = vm.retentionClient.numero;
      vm.retentionClient.dateToday = $('#pickerDateToday').data("DateTimePicker").date().toDate().getTime();
      vm.retentionClient.documentDate = $('#pickerDateRetention').data("DateTimePicker").date().toDate().getTime();
      vm.retentionClient.BillId = vm.BillId;
      vm.retentionClient.detailRetentionClient = [];
      _.each(vm.retentionClient.items, function(item) {
        var detail = {
          taxType: item.codigo === 1 ? 'RETENCION EN LA FUENTE' : 'RETENCION EN EL IVA',
          code: item.codigo_porcentaje_integridad,
          baseImponible: item.base_imponible,
          percentage: item.porcentaje,
          total: item.valor_retenido
        };
        vm.retentionClient.detailRetentionClient.push(detail);
      });
      eretentionClientService.create(retentionClient).then(function(respRetentionClient) {
        vm.totalRetention = 0;
        vm.retentionClient = respRetentionClient;
        _.each(vm.retentionClient.detailRetentionClient, function(detail) {
          vm.totalRetention = (parseFloat(vm.totalRetention) + parseFloat(detail.total)).toFixed(2);
        });
        vm.retentionClientCreated = true;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
      _activate();
    };

    vm.cancelRetentionClientCreated = function() {
      _activate();
    };
// Fin de Creación de Retenciones....

    vm.cancel = function() {
      vm.retentionClient = undefined;
      vm.success = undefined;
      vm.error = undefined;
      vm.billList = undefined;
    };

    (function initController() {
      _activate();
    })();
  });

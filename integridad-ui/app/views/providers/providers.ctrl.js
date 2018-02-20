'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProvidersCtrl', function (_, $localStorage, $location, providerService, utilStringService) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;

    vm.loading = false;
    vm.userData = $localStorage.user;
    vm.provider = undefined;
    vm.providerToUse = undefined;
    vm.providerList = undefined;
    vm.providerType = [
      'PROVEEDOR TIPO 1',
      'PROVEEDOR TIPO 2',
      'PROVEEDOR TIPO 3',
      'PROVEEDOR TIPO 4',
    ];

    function _activate(){
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
    }

    function create(){
      providerService.create(vm.provider).then(function (response) {
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    }

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
    }

    function getTables(type){
      vm.fuenteTipo = [
        {name:'Honorarios profesionales y demás pagos por servicios relacionados con el título profesional' ,percentage:10},
        {name:'Servicios predomina el intelecto no relacionados con el título profesional' ,percentage:8},
        {name:'Comisiones y demás pagos por servicios predomina intelecto no relacionados con el título profesional' ,percentage:8},
        {name:'Pagos a notarios y registradores de la propiedad y mercantil por sus actividades ejercidas como tales' ,percentage:8},
        {name:'Pagos a deportistas, entrenadores, árbitros, miembros del cuerpo técnico por sus actividades ejercidas como tales' ,percentage:8},
        {name:'Pagos a artistas por sus actividades ejercidas como tales' ,percentage:8},
        {name:'Honorarios y demás pagos por servicios de docencia' ,percentage:8},
        {name:'Servicios predomina la mano de obra' ,percentage:2},
        {name:'Utilización o aprovechamiento de la imagen o renombre' ,percentage:10},
        {name:'Servicios prestados por medios de comunicación y agencias de publicidad' ,percentage:1},
        {name:'Servicio de transporte privado de pasajeros o transporte público o privado de carga' ,percentage:1},
        {name:'Por pagos a través de liquidación de compra (nivel cultural o rusticidad) **' ,percentage:2},
        {name:'Transferencia de bienes muebles de naturaleza corporal' ,percentage:1},
        {name:'Compra de bienes de origen agrícola, avícola, pecuario, apícola, cunícula, bioacuático, y forestal' ,percentage:1},
        {name:'Regalías por concepto de franquicias de acuerdo a Ley de Propiedad Intelectual - pago a personas naturales' ,percentage:8},
        {name:'Cánones, derechos de autor,  marcas, patentes y similares de acuerdo a Ley de Propiedad Intelectual – pago a personas naturales' ,percentage:8},
        {name:'Regalías por concepto de franquicias de acuerdo a Ley de Propiedad Intelectual  - pago a sociedades' ,percentage:8},
        {name:'Cánones, derechos de autor,  marcas, patentes y similares de acuerdo a Ley de Propiedad Intelectual – pago a sociedades' ,percentage:8},
        {name:'Cuotas de arrendamiento mercantil, inclusive la de opción de compra' ,percentage:1},
        {name:'Por arrendamiento bienes inmuebles' ,percentage:8},
        {name:'Seguros y reaseguros (primas y cesiones)' ,percentage:1},
        {name:'Por rendimientos financieros pagados a naturales y sociedades  (No a IFIs)' ,percentage:2},
        {name:'Por RF: depósitos Cta. Corriente' ,percentage:2},
        {name:'Por RF:  depósitos Cta. Ahorros Sociedades' ,percentage:2},
        {name:'Por RF: depósito a plazo fijo  gravados' ,percentage:2},
        {name:'Por RF: depósito a plazo fijo exentos ***' ,percentage:0},
        {name:'Por rendimientos financieros: operaciones de reporto - repos' ,percentage:2},
        {name:'Por RF: inversiones (captaciones) rendimientos distintos de aquellos pagados a IFIs' ,percentage:2},
        {name:'Por RF: obligaciones' ,percentage:2},
        {name:'Por RF: bonos convertible en acciones' ,percentage:2},
        {name:'Por RF: Inversiones en títulos valores en renta fija gravados ' ,percentage:2},
        {name:'Por RF: Inversiones en títulos valores en renta fija exentos' ,percentage:0},
        {name:'Por RF: Intereses pagados a bancos y otras entidades sometidas al control de la Superintendencia de Bancos y de la Economía Popular y Solidaria' ,percentage:0},
        {name:'Por RF: Intereses pagados por entidades del sector público a favor de sujetos pasivos' ,percentage:2},
        {name:'Por RF: Otros intereses y rendimientos financieros gravados ' ,percentage:2},
        {name:'Por RF: Otros intereses y rendimientos financieros exentos' ,percentage:0},
        {name:'Por RF: Intereses y comisiones en operaciones de crédito entre instituciones del sistema financiero y entidades economía popular y solidaria.' ,percentage:1},
        {name:'Por RF: Por inversiones entre instituciones del sistema financiero y entidades economía popular y solidaria, incluso cuando el BCE actúe como intermediario.' ,percentage:1},
        {name:'Anticipo dividendos a residentes o establecidos en el Ecuador' ,percentage:22},
        {name:'Dividendos anticipados préstamos accionistas, beneficiarios o partìcipes a residentes o establecidos en el Ecuador' ,percentage:22},
        {name:'Dividendos distribuidos que correspondan al impuesto a la renta único establecido en el art. 27 de la LRTI (Tabla art. 36 menos crédito tributario pro dividendos: julio 2015)' ,percentage:undefined},
        {name:'Dividendos distribuidos a personas naturales residentes' ,percentage:undefined},
        {name:'Dividendos distribuidos a sociedades residentes' ,percentage:0},
        {name:'Dividendos distribuidos a fideicomisos residentes' ,percentage:0},
        {name:'Dividendos gravados distribuidos en acciones (reinversión de utilidades sin derecho a reducción tarifa IR)' ,percentage:undefined},
        {name:'Dividendos exentos distribuidos en acciones (reinversión de utilidades con derecho a reducción tarifa IR) ' ,percentage:0},
        {name:'Otras compras de bienes y servicios no sujetas a retención' ,percentage:0},
        {name:'Enajenación de derechos representativos de capital y otros derechos exentos (mayo 2016)' ,percentage:0},
        {name:'Compra de bienes inmuebles' ,percentage:0},
        {name:'Transporte público de pasajeros' ,percentage:0},
        {name:'Pagos en el país por transporte de pasajeros o transporte internacional de carga, a compañías nacionales o extranjeras de aviación o marítimas' ,percentage:0},
        {name:'Valores entregados por las cooperativas de transporte a sus socios' ,percentage:0},
        {name:'Compraventa de divisas distintas al dólar de los Estados Unidos de América' ,percentage:0},
        {name:'Pagos con tarjeta de crédito ' ,percentage:undefined},
        {name:'Pago al exterior tarjeta de crédito reportada por la Emisora de tarjeta de crédito, solo RECAP' ,percentage:0},
        {name:'Enajenación de derechos representativos de capital y otros derechos cotizados en bolsa ecuatoriana' ,percentage:0.002},
        {name:'Enajenación de derechos representativos de capital y otros derechos no cotizados en bolsa ecuatoriana' ,percentage:1},
        {name:'Por loterías, rifas, apuestas y similares' ,percentage:15},
        {name:'Por venta de combustibles a comercializadoras' ,percentage:undefined},
        {name:'Por venta de combustibles a distribuidores' ,percentage:undefined},
        {name:'Compra local de banano a productor' ,percentage:undefined},
        {name:'Liquidación impuesto único a la venta local de banano de producción propia' ,percentage:undefined},
        {name:'Impuesto único a la exportación de banano de producción propia - componente 1' ,percentage:undefined},
        {name:'Impuesto único a la exportación de banano de producción propia - componente 2' ,percentage:undefined},
        {name:'Impuesto único a la exportación de banano producido por terceros' ,percentage:undefined},
        {name:'Por energía eléctrica' ,percentage:1},
        {name:'Por actividades de construcción de obra material inmueble, urbanización, lotización o actividades similares' ,percentage:1},
        {name:'Otras retenciones aplicables el 2%' ,percentage:2},
        {name:'Pago local tarjeta de crédito reportada por la Emisora de tarjeta de crédito, solo RECAP' ,percentage:2},
        {name:'Ganancias de capital' ,percentage:undefined},
        {name:'Donaciones en dinero -Impuesto a la donaciones ' ,percentage:2},
        {name:'Retención a cargo del propio sujeto pasivo por la exportación de concentrados y/o elementos metálicos' ,percentage:undefined},
        {name:'Retención a cargo del propio sujeto pasivo por la comercialización de productos forestales' ,percentage: undefined},
        {name:'Pago al exterior - Rentas Inmobiliarias' ,percentage:22},
        {name:'Pago al exterior - Beneficios Empresariales' ,percentage:22},
        {name:'Pago al exterior - Servicios Empresariales' ,percentage:22},
        {name:'Pago al exterior - Navegación Marítima y/o aérea' ,percentage:22},
        {name:'Pago al exterior- Dividendos distribuidos a personas naturales' ,percentage:0}
      ];
      vm.ivaTipo = [
        {name:'RETENCION DEL 10%' ,percentage:10},
        {name:'RETENCION DEL 20%' ,percentage:20},
        {name:'RETENCION DEL 30%' ,percentage:30},
        {name:'RETENCION DEL 50%' ,percentage:50},
        {name:'RETENCION DEL 70%' ,percentage:70},
        {name:'RETENCION DEL 100%' ,percentage:100}
      ];
    }

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

    vm.cancel = function(){
      vm.provider = undefined;
    };

    vm.register = function(){
      if(vm.provider.id){
        update();
      } else {
        create();
      }
    };

    vm.remove = function(){
      vm.provider.active=false;
      update();
    };

    vm.createRetention = function(prov){
      vm.retention = {
        type: undefined
      };
      vm.providerToUse = prov;
    };

    (function initController() {
      _activate();
    })();
  });

'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebtsToPayCtrl
 * @description
 * # DebtsToPayCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('DebtsToPayCtrl', function(_, $localStorage, providerService, cuentaContableService, debtsToPayService, 
                                        utilSeqService, creditsDebtsService, paymentDebtsService, $location) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    
    vm.billNumber = undefined;
    vm.ejercicio = undefined;
    vm.usrCliId = undefined;
    vm.provider = undefined;
    vm.providerId = undefined;
    vm.providerRuc = undefined;
    vm.providerName = undefined;
    vm.providerSelected = undefined;
    vm.providerList = undefined;
    vm.providerDebtsList = undefined;
    vm.subTotal = undefined;
    vm.totalTotal = undefined;
    vm.seqNumber = undefined;
    vm.aux = undefined;
    vm.subIva = undefined;
    vm.typeTaxes = undefined;
    vm.cuentaCtableId = undefined;
    vm.creditsDebtsValue = undefined;
    vm.debtsToPayList = undefined;
    vm.debtsToPayCreated = undefined;
    vm.saldoCredito = undefined;
    vm.saldoDebito = undefined;
    vm.saldo = undefined;

    vm.countries = [
      {code:'16',name:'16 - AMERICAN SAMOA'},{code:'74',name:'74 - BOUVET ISLAND'},{code:'101',name:'101 - ARGENTINA'},{code:'102',name:'102 - BOLIVIA'},
      {code:'103',name:'103 - BRASIL'},{code:'104',name:'104 - CANADA'},{code:'105',name:'105 - COLOMBIA'},{code:'106',name:'106 - COSTA RICA'},
      {code:'107',name:'107 - CUBA'},{code:'108',name:'108 - CHILE'},{code:'109',name:'109 - ANGUILA'},{code:'110',name:'110 - ESTADOS UNIDOS'},
      {code:'111',name:'111 - GUATEMALA'},{code:'112',name:'112 - HAITI'},{code:'113',name:'113 - HONDURAS'},{code:'114',name:'114 - JAMAICA'},
      {code:'115',name:'115 - ISLAS MALVINAS'},{code:'116',name:'116 - MEXICO'},{code:'117',name:'117 - NICARAGUA'},{code:'118',name:'118 - PANAMA'},
      {code:'119',name:'119 - PARAGUAY'},{code:'120',name:'120 - PERU'},{code:'121',name:'121 - PUERTO RICO'},{code:'122',name:'122 - REPUBLICA DOMINICANA'},
      {code:'123',name:'123 - EL SALVADOR'},{code:'124',name:'124 - TRINIDAD Y TOBAGO'},{code:'125',name:'125 - URUGUAY'},{code:'126',name:'126 - VENEZUELA (REPUBLICA BOLIVARIANA)'},
      {code:'127',name:'127 - CURAZAO'},{code:'129',name:'129 - BAHAMAS'},{code:'130',name:'130 - BARBADOS'},{code:'131',name:'131 - GRANADA'},
      {code:'132',name:'132 - GUYANA'},{code:'133',name:'133 - SURINAM'},{code:'134',name:'134 - ANTIGUA Y BARBUDA'},{code:'135',name:'135 - BELICE'},
      {code:'136',name:'136 - DOMINICA'},{code:'137',name:'137 - SAN CRISTOBAL Y NEVIS'},{code:'138',name:'138 - SANTA LUCIA'},{code:'139',name:'139 - SAN VICENTE Y LAS GRANADINAS'},
      {code:'141',name:'141 - ARUBA'},{code:'142',name:'142 - BERMUDA'},{code:'143',name:'143 - GUADALUPE'},{code:'144',name:'144 - GUYANA FRANCESA'},
      {code:'145',name:'145 - ISLAS CAIMAN'},{code:'146',name:'146 - ISLAS VIRGENES (BRITANICAS)'},{code:'147',name:'147 - ISLA JOHNSTON'},{code:'148',name:'148 - MARTINICA'},
      {code:'149',name:'149 - ISLA MONTSERRAT'},{code:'151',name:'151 - ISLAS TURCAS Y CAICOS'},{code:'152',name:'152 - ISLAS VIRGENES(NORT.AMER.)'},{code:'201',name:'201 - ALBANIA'},
      {code:'202',name:'202 - ALEMANIA'},{code:'203',name:'203 - AUSTRIA'},{code:'204',name:'204 - BELGICA'},{code:'205',name:'205 - BULGARIA'},
      {code:'207',name:'207 - ALBORAN Y PEREJIL'},{code:'208',name:'208 - DINAMARCA'},{code:'209',name:'209 - ESPAÑA'},{code:'211',name:'211 - FRANCIA'},
      {code:'212',name:'212 - FINLANDIA'},{code:'213',name:'213 - REINO UNIDO'},{code:'214',name:'214 - GRECIA'},{code:'215',name:'215 - PAISES BAJOS (HOLANDA)'},
      {code:'216',name:'216 - HUNGRIA'},{code:'217',name:'217 - IRLANDA'},{code:'218',name:'218 - ISLANDIA'},{code:'219',name:'219 - ITALIA'},
      {code:'220',name:'220 - LUXEMBURGO'},{code:'221',name:'221 - MALTA'},{code:'222',name:'222 - NORUEGA'},{code:'223',name:'223 - POLONIA'},
      {code:'224',name:'224 - PORTUGAL'},{code:'225',name:'225 - RUMANIA'},{code:'226',name:'226 - SUECIA'},{code:'227',name:'227 - SUIZA'},
      {code:'228',name:'228 - ISLAS CANARIAS'},{code:'229',name:'229 - UCRANIA'},{code:'230',name:'230 - RUSIA'},{code:'231',name:'231 - YUGOSLAVIA'},
      {code:'233',name:'233 - ANDORRA'},{code:'234',name:'234 - LIECHTENSTEIN'},{code:'235',name:'235 - MONACO'},{code:'237',name:'237 - SAN MARINO'},
      {code:'238',name:'238 - VATICANO (SANTA SEDE)'},{code:'239',name:'239 - GIBRALTAR'},{code:'241',name:'241 - BELARUS'},{code:'242',name:'242 - BOSNIA Y HERZEGOVINA'},
      {code:'243',name:'243 - CROACIA'},{code:'244',name:'244 - ESLOVENIA'},{code:'245',name:'245 - ESTONIA'},{code:'246',name:'246 - GEORGIA'},
      {code:'247',name:'247 - GROENLANDIA'},{code:'248',name:'248 - LETONIA'},{code:'249',name:'249 - LITUANIA'},{code:'250',name:'250 - MOLDOVA'},
      {code:'251',name:'251 - MACEDONIA'},{code:'252',name:'252 - ESLOVAQUIA'},{code:'253',name:'253 - ISLAS FAROE'},{code:'260',name:'260 - FRENCH SOUTHERN TERRITORIES'},
      {code:'301',name:'301 - AFGANISTAN'},{code:'302',name:'302 - ARABIA SAUDITA'},{code:'303',name:'303 - MYANMAR (BURMA)'},{code:'304',name:'304 - CAMBOYA'},
      {code:'306',name:'306 - COREA NORTE'},{code:'307',name:'307 - TAIWAN (CHINA)'},{code:'308',name:'308 - FILIPINAS'},{code:'309',name:'309 - INDIA'},
      {code:'310',name:'310 - INDONESIA'},{code:'311',name:'311 - IRAK'},{code:'312',name:'312 - IRAN (REPUBLICA ISLAMICA)'},{code:'313',name:'313 - ISRAEL'},
      {code:'314',name:'314 - JAPON'},{code:'315',name:'315 - JORDANIA'},{code:'316',name:'316 - KUWAIT'},{code:'317',name:'317 - REP.POP.DEMOC. LAOS'},
      {code:'318',name:'318 - LIBANO'},{code:'319',name:'319 - MALASIA'},{code:'321',name:'321 - MONGOLIA (MANCHURIA)'},{code:'322',name:'322 - PAKISTAN'},
      {code:'323',name:'323 - SIRIA'},{code:'325',name:'325 - TAILANDIA'},{code:'327',name:'327 - BAHREIN'},{code:'328',name:'328 - BANGLADESH'},
      {code:'329',name:'329 - BUTAN'},{code:'330',name:'330 - COREA DEL SUR'},{code:'331',name:'331 - CHINA (REPUBLICA POPULAR)'},{code:'332',name:'332 - CHIPRE'},
      {code:'333',name:'333 - EMIRATOS ARABES UNIDOS'},{code:'334',name:'334 - QATAR'},{code:'335',name:'335 - MALDIVAS'},{code:'336',name:'336 - NEPAL'},
      {code:'337',name:'337 - OMAN'},{code:'338',name:'338 - SINGAPUR'},{code:'339',name:'339 - SRI LANKA (CEILAN)'},{code:'341',name:'341 - VIETNAM'},
      {code:'342',name:'342 - YEMEN'},{code:'343',name:'343 - ISLAS HEARD Y MCDONALD'},{code:'344',name:'344 - BRUNEI DARUSSALAM'},{code:'346',name:'346 - TURQUIA'},
      {code:'347',name:'347 - AZERBAIJAN'},{code:'348',name:'348 - KAZAJSTAN'},{code:'349',name:'349 - KIRGUIZISTAN'},{code:'350',name:'350 - TAJIKISTAN'},
      {code:'351',name:'351 - TURKMENISTAN'},{code:'352',name:'352 - UZBEKISTAN'},{code:'353',name:'353 - PALESTINA'},{code:'354',name:'354 - CHINA - HONG KONG'},
      {code:'355',name:'355 - CHINA - MACAO'},{code:'356',name:'356 - ARMENIA'},{code:'382',name:'382 - MONTENEGRO'},{code:'402',name:'402 - BURKINA FASO'},
      {code:'403',name:'403 - ARGELIA'},{code:'404',name:'404 - BURUNDI'},{code:'405',name:'405 - CAMERUN'},{code:'406',name:'405 - REP. DEMOCRÁTICA DEL CONGO'},
      {code:'407',name:'407 - ETIOPIA'},{code:'408',name:'408 - GAMBIA'},{code:'409',name:'409 - GUINEA'},{code:'410',name:'410 - LIBERIA'},
      {code:'412',name:'412 - MADAGASCAR'},{code:'413',name:'413 - MALAWI'},{code:'414',name:'414 - MALI'},{code:'415',name:'415 - MARRUECOS'},
      {code:'416',name:'416 - MAURITANIA'},{code:'417',name:'417 - NIGERIA'},{code:'419',name:'419 - ZIMBABWE (RHODESIA)'},{code:'420',name:'420 - SENEGAL'},
      {code:'421',name:'421 - SUDAN'},{code:'422',name:'422 - SUDAFRICA (CISKEI)'},{code:'423',name:'423 - SIERRA LEONA'},{code:'425',name:'425 - TANZANIA'},
      {code:'426',name:'426 - UGANDA'},{code:'427',name:'427 - ZAMBIA'},{code:'428',name:'428 - ÅLAND ISLANDS'},{code:'429',name:'429 - BENIN'},
      {code:'430',name:'430 - BOTSWANA'},{code:'431',name:'431 - REPUBLICA CENTROAFRICANA'},{code:'432',name:'432 - COSTA DE MARFIL'},{code:'433',name:'433 - CHAD'},
      {code:'434',name:'434 - EGIPTO'},{code:'435',name:'435 - GABON'},{code:'436',name:'436 - GHANA'},{code:'437',name:'437 - GUINEA-BISSAU'},
      {code:'438',name:'438 - GUINEA ECUATORIAL'},{code:'439',name:'439 - KENIA'},{code:'440',name:'440 - LESOTHO'},{code:'441',name:'441 - MAURICIO'},
      {code:'442',name:'442 - MOZAMBIQUE'},{code:'443',name:'443 - MAYOTTE'},{code:'444',name:'444 - NIGER'},{code:'445',name:'445 - RWANDA'},
      {code:'446',name:'446 - SEYCHELLES'},{code:'447',name:'447 - SAHARA OCCIDENTAL'},{code:'448',name:'448 - SOMALIA'},{code:'449',name:'449 - SANTO TOME Y PRINCIPE'},
      {code:'450',name:'450 - KINGDOM OF SWAZILANDIA'},{code:'451',name:'451 - TOGO'},{code:'452',name:'452 - TUNEZ'},{code:'453',name:'453 - ZAIRE'},
      {code:'454',name:'454 - ANGOLA'},{code:'456',name:'456 - CABO VERDE'},{code:'458',name:'458 - COMORAS'},{code:'459',name:'459 - DJIBOUTI'},
      {code:'460',name:'460 - NAMIBIA'},{code:'463',name:'463 - ERITREA'},{code:'464',name:'464 - MOROCCO'},{code:'465',name:'465 - REUNION'},
      {code:'466',name:'466 - SANTA ELENA'},{code:'499',name:'499 - JERSEY'},{code:'501',name:'501 - AUSTRALIA'},{code:'503',name:'503 - NUEVA ZELANDA'},
      {code:'504',name:'504 - SAMOA OCCIDENTAL'},{code:'506',name:'506 - FIJI'},{code:'507',name:'507 - PAPUA NUEVA GUINEA'},{code:'508',name:'508 - TONGA'},
      {code:'509',name:'509 - ISLAS PALAO (BELAU)'},{code:'510',name:'5510 - KIRIBATI'},{code:'511',name:'511 - ISLAS MARSHALL'},{code:'512',name:'512 - MICRONESIA'},
      {code:'513',name:'513 - NAURU'},{code:'514',name:'514 - ISLAS SALOMON'},{code:'515',name:'515 - TUVALU'},{code:'516',name:'516 - VANUATU'},
      {code:'517',name:'517 - GUAM'},{code:'518',name:'518 - ISLAS COCOS (KEELING)'},{code:'519',name:'519 - ISLAS COOK'},{code:'520',name:'520 - ISLAS NAVIDAD'},
      {code:'521',name:'521 - ISLAS MIDWAY'},{code:'522',name:'522 - ISLA NIUE'},{code:'523',name:'523 - ISLA NORFOLK'},{code:'524',name:'524 - NUEVA CALEDONIA'},
      {code:'525',name:'525 - ISLA PITCAIRN'},{code:'526',name:'526 - POLINESIA FRANCESA'},{code:'529',name:'529 - TIMOR DEL ESTE'},{code:'530',name:'530 - TOKELAI'},
      {code:'531',name:'531 - ISLA WAKE'},{code:'532',name:'532 - ISLAS WALLIS Y FUTUNA'},{code:'534',name:'534 - SINT MAARTEN (DUTCH PART)'},{code:'590',name:'590 - SAINT BARTHELEMY'},
      {code:'593',name:'593 - ECUADOR'},{code:'594',name:'594 - AGUAS INTERNACIONALES'},{code:'595',name:'595 - ALTO VOLTA'},{code:'596',name:'596 - BIELORRUSIA'},
      {code:'597',name:'597 - COTE DÍVOIRE'},{code:'598',name:'598 - CYPRUS'},{code:'599',name:'599 - REPUBLICA CHECA'},{code:'600',name:'600 - ISLANDS FALKLAND'},
      {code:'601',name:'601 - LATVIA'},{code:'602',name:'602 - LIBIA'},{code:'603',name:'603 - ISLA MARIANA NORTHERN'},{code:'604',name:'604 - ST. PIERRE AND MIQUE'},
      {code:'605',name:'605 - SYRIAN ARAB REPUBLIC'},{code:'606',name:'606 - TERRITORIO ANTARTICO BRITANICO'},{code:'607',name:'607 - TERRITORIO BRITANICO OCEANO INDICO'},{code:'688',name:'688 - SERBIA'},
      {code:'831',name:'831 - GUERNSEY'},{code:'833',name:'833 - ISLE OF MAN'}
    ];

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

    vm.medLista = [
      {code: 'efectivo', name: 'Efectivo' },
      {code: 'cheque', name: 'Cheque' },
      {code: 'cheque_posfechado', name: 'Cheque posfechado' },
      {code: 'tarjeta_credito', name: 'Tarjeta de crédito' },
      {code: 'tarjeta_debito', name: 'Tarjeta de débito' },
      {code: 'dinero_electronico_ec', name: 'Dinero electrónico' },
      {code: 'credito', name: 'Crédito' },
    ];

    vm.creditCardList = [
      'DINNERS CLUB',
      'VISA',
      'MASTERCARD',
      'AMERICAN'
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
      {code: 'BIEN', name: 'BIENES'},
      {code: 'SERV', name: 'SERVICIOS'},
      {code: 'MATP', name: 'MATERIA PRIMA'},
      {code: 'CONS', name: 'CONSUMIBLES'},
      {code: 'RMBG', name: 'REEMBOLSO DE GASTOS'},
      {code: 'TKAE', name: 'TIKETS AEREOS'}
    ];

    function _activate() {
      vm.provider = undefined;
      vm.aux = undefined;
      vm.ejercicio = undefined;
      vm.debtsToPayList = undefined;
      vm.providerSelected = undefined;
      vm.providerDebtsList = undefined;
      vm.debtsToPayCreated = undefined;
      vm.loading = true;
      vm.success = undefined;
      vm.error = undefined;
      vm.providerList = undefined;
      vm.medio = {};
      vm.pagos = [];
      vm.usrCliId = $localStorage.user.subsidiary.userClient.id;
      providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
        vm.providerList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function _getSeqNumber() {
      vm.numberAddedOne = parseInt($localStorage.user.cashier.debtsNumberSeq) + 1;
      vm.seqNumber = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    function _initializeDebts() {
      vm.debtsToPay = {
        provider: vm.providerSelected,
        userIntegridad: $localStorage.user,
        subsidiary: $localStorage.user.subsidiary,
        typeTaxes: undefined,
        items: [],
        pagos: []
      };
    };

    vm.providerSelect = function(provider) {
      vm.success = undefined;
      vm.loading = true;
      vm.providerSelected = provider;
      vm.companyData = $localStorage.user.subsidiary;
      _getSeqNumber();
      _initializeDebts();
      vm.loading = false;
      var today = new Date();
      $('#pickerDateDebtsToPay').data("DateTimePicker").date(today);
      $('#pickerDateDebtsToPay').on("dp.change", function(data) {
        vm.ejercicio = ('0' + ($('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' + $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getFullYear();
      });
    };

    vm.providerDebts = function(provider) {
      vm.loading = true;
      vm.success = undefined;
      vm.providerName = provider.name;
      vm.providerRuc = provider.ruc;
      vm.providerId = provider.id;
      debtsToPayService.getAllDebtsToPayByProviderId(provider.id).then(function(response) {
        vm.debtsToPayList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.consultProviderDebts = function(provider) {
      vm.loading = true;
      vm.success = undefined;
      vm.providerName = provider.name;
      vm.providerRuc = provider.ruc;
      vm.providerId = provider.id;
      debtsToPayService.getAllDebtsToPayByProviderId(provider.id).then(function(response) {
        vm.providerDebtsList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getPercentageTableAll = function() {
      if (vm.debtsToPay.typeTaxes === '1') {
        cuentaContableService.getCuentaContableByUserClient(vm.usrCliId).then(function(response) {
          vm.cuentaContableList = response;
          vm.loading = false;
        }).catch(function(error) {
          vm.loading = false;
          vm.error = error.data;
        });
        vm.typeTaxes = vm.debtsToPay.typeTaxes;
      } else if (vm.debtsToPay.typeTaxes === '2') {
        cuentaContableService.getCuentaContableByUserClient(vm.usrCliId).then(function(response) {
          vm.cuentaContableList = response;
          vm.loading = false;
        }).catch(function(error) {
          vm.loading = false;
          vm.error = error.data;
        });
        vm.typeTaxes = vm.debtsToPay.typeTaxes;
      };
    };

    vm.selectPurchaseTable = function(purchaseType) {
      if (vm.debtsToPay.typeTaxes === '1') {
        cuentaContableService.getCuentaContableByType(vm.usrCliId, purchaseType).then(function(response) {
          vm.cuentaContableList = response;
          vm.loading = false;
        }).catch(function(error) {
          vm.loading = false;
          vm.error = error.data;
        });
      } else if (vm.debtsToPay.typeTaxes === '2') {
        cuentaContableService.getCuentaContableByType(vm.usrCliId, purchaseType).then(function(response) {
          vm.cuentaContableList = response;
          vm.loading = false;
        }).catch(function(error) {
          vm.loading = false;
          vm.error = error.data;
        });
      };
    };

    vm.addIvaAndProvider = function() {
      if (vm.indexEdit !== undefined) {
        vm.debtsToPay.items.splice(vm.indexEdit, 1);
        vm.indexEdit = undefined;
      };
      if (vm.typeTaxes === '1') {
        vm.subIva = parseFloat((vm.item.base_imponible * 0.1200).toFixed(2));
        vm.itemIva = {
          codigo_contable: '1.01.01.01',
          desc_contable: 'IVA EN COMPRAS',
          tipo: 'DEBITO (D)',
          base_imponible: vm.subIva,
          nomb_contable: 'DEFINIDA PARA TODAS LAS COMPRAS'
        };
        vm.itemProvider = {
          codigo_contable: '2.01.01.01',
          desc_contable: 'PROVEEDORES LOCALES',
          tipo: 'CREDITO (C)',
          base_imponible: vm.debtsToPay.total,
          nomb_contable: 'DEFINIDA PARA TODOS LOS PROVEEDORES'
        };
        vm.debtsToPay.items.push(vm.itemIva);
        vm.debtsToPay.items.push(vm.itemProvider);
      } else if (vm.typeTaxes === '2') {
        vm.itemProvider = {
          codigo_contable: '2.01.01.01',
          desc_contable: 'PROVEEDORES LOCALES',
          tipo: 'CREDITO (C)',
          base_imponible: vm.debtsToPay.total,
          nomb_contable: 'DEFINIDA PARA TODOS LOS PROVEEDORES'
        };
        vm.debtsToPay.items.push(vm.itemProvider);
      };
    };

    vm.selectTaxes = function(tax) {
      vm.item = undefined;
      vm.item = {
        cta_contable: tax.id,
        codigo: parseInt(vm.debtsToPay.typeTaxes),
        codigo_contable: tax.code,
        desc_contable: tax.description,
        tipo: tax.accountType,
        nomb_contable: tax.name
      };
      vm.subTotal = parseFloat((vm.debtsToPay.total / 1.1200).toFixed(2));
      //vm.subIva = parseFloat((vm.debtsToPay.total - vm.subTotal).toFixed(2));
      vm.totalTotal = parseFloat(vm.debtsToPay.total);
    };

    vm.getRestaSubotal = function() {
      var totalDebito = vm.subTotal;
      if (vm.debtsToPay) {
        _.each (vm.debtsToPay.items, function(detail) {
          if (detail.tipo === 'DEBITO (D)') {
            totalDebito = (parseFloat(totalDebito) - parseFloat(detail.base_imponible)).toFixed(2);
          };
        });
      };
      return totalDebito;
    };

    vm.getRestaTotalTotal = function() {
      var totalDebito = vm.totalTotal;
      if (vm.debtsToPay) {
        _.each (vm.debtsToPay.items, function(detail) {
          if (detail.tipo === 'DEBITO (D)') {
            totalDebito = (parseFloat(totalDebito) - parseFloat(detail.base_imponible)).toFixed(2);
          };
        });
      };
      return totalDebito;
    };

    vm.editItemTaxes = function(index) {
      vm.item = angular.copy(vm.debtsToPay.items[index]);
      vm.indexEdit = index;
    };

    vm.deleteItemTaxes = function(index) {
      vm.debtsToPay.items.splice(index, 1);
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

    vm.getTotalSaldo = function() {
      var totalSaldo = 0;
      totalSaldo = (parseFloat(vm.saldoCredito) - parseFloat(vm.saldoDebito)).toFixed(2);
      return totalSaldo;
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

    vm.loadMedio = function() {
      var payed = 0;
      _.each(vm.pagos, function(pago){
        payed += parseFloat(pago.total);
      });
      vm.pagos;
      if (vm.medio.medio === 'efectivo' || vm.medio.medio === 'dinero_electronico_ec') {
        vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = vm.aux;
      };
      if (vm.medio.medio === 'credito') {
        vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = (vm.debtsToPay.total - payed).toFixed(4);
      };
      if (vm.medio.medio === 'cheque' || vm.medio.medio === 'cheque_posfechado') {
        vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
        vm.medio.total = (vm.debtsToPay.total - payed).toFixed(4);
      };
      if (vm.medio.medio === 'tarjeta_credito' || vm.medio.medio === 'tarjeta_debito') {
        vm.medio.payForm = '19 - TARJETA DE CREDITO';
        vm.medio.total = (vm.debtsToPay.total - payed).toFixed(4);
      };
    };

    vm.getFechaCobro = function() {
      var d = new Date();
      vm.medio.fechaCobro = _addDays(d, parseInt(vm.medio.chequeDiasPlazo));
    };

    vm.loadCredit = function() {
      var creditArray = [];
      var diasPlazo = parseInt(vm.medio.creditoIntervalos);
      var billNumber = vm.debtsToPay.threeNumberOne + '-' + vm.debtsToPay.threeNumberTwo + '-' + vm.debtsToPay.seccondPartNumber;
      var d = new Date();
      var statusCredits = 'PENDIENTE';
      var total = parseFloat(parseFloat(vm.debtsToPay.total) / parseFloat(vm.medio.creditoNumeroPagos)).toFixed(4);
      for (var i = 1; i <= parseInt(vm.medio.creditoNumeroPagos); i++) {
        var credito = {
          payNumber: i,
          diasPlazo: diasPlazo,
          estadoCredits: statusCredits,
          fecha: _addDays(d, diasPlazo),
          documentNumber: billNumber,
          valor: total
        };
        diasPlazo += parseInt(vm.medio.creditoIntervalos);
        creditArray.push(credito);
      };
      vm.medio.creditsDebts = creditArray;
    };

    function _addDays(date, days) {
      var result = new Date(date);
      result.setDate(result.getDate() + days);
      return result.getTime();
    };

    vm.addPago = function() {
      vm.pagos.push(angular.copy(vm.medio));
      vm.medio = {};
    };

    vm.removePago = function(index) {
      vm.pagos.splice(index, 1);
    };

    vm.getTotalPago = function() {
      vm.aux = 0;
      vm.varPago = 0;
      if (vm.debtsToPay) {
        vm.getCambio = 0;
        _.each(vm.pagos, function(med) {
          vm.varPago = parseFloat(parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2);
        });
        vm.getCambio = (vm.varPago - vm.debtsToPay.total).toFixed(2);
        vm.aux = (vm.varPago - vm.getCambio).toFixed(2);
      };
      return vm.varPago;
    };

    vm.saveDebtsToPay = function(debtsToPay) {
      vm.loading = true;
      vm.pagos.total = vm.aux;
      $('#modalAddPago').modal('hide');
      vm.debtsToPay.fecha = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
      vm.debtsToPay.billNumber = vm.debtsToPay.threeNumberOne + '-' + vm.debtsToPay.threeNumberTwo + '-' + vm.debtsToPay.seccondPartNumber;
      vm.debtsToPay.providerId = vm.providerId;
      vm.debtsToPay.subTotal = vm.subTotal;
      vm.debtsToPay.debtsSeq = vm.seqNumber;
      vm.debtsToPay.ejercicio = vm.ejercicio;
      vm.debtsToPay.saldo = vm.debtsToPay.total;
      vm.debtsToPay.detailDebtsToPay = [];
      vm.debtsToPay.pagos = vm.pagos;
      _.each(vm.debtsToPay.items, function(item) {
        var detail = {
          codeConta: item.codigo_contable,
          baseImponible: item.base_imponible,
          descrip: item.desc_contable,
          name: item.nomb_contable,
          tipo: item.tipo,
          cuentaContableId: item.cta_contable
        };
        vm.debtsToPay.detailDebtsToPay.push(detail);
      });
      debtsToPayService.create(debtsToPay).then(function(respDebtsToPay) {
        vm.totalDebtsToPay = 0;
        vm.debtsToPay = respDebtsToPay;
        $localStorage.user.cashier.debtsNumberSeq = vm.debtsToPay.debtsSeq;
        _.each(vm.debtsToPay.detailDebtsToPay, function(detail) {
          vm.totalDebtsToPay = (parseFloat(vm.totalDebtsToPay) + parseFloat(detail.baseImponible)).toFixed(2);
        });
        vm.debtsToPayCreated = true;
        vm.success = 'Factura Ingresada con Exito';
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
      _activate();
    };

    vm.creditsByDebts = function(debtsToPay) {
      vm.loading = true;
      vm.success = undefined;
      vm.error = undefined;
      vm.debtsBillNumber = debtsToPay.billNumber;
      vm.debtsValue = (debtsToPay.total).toFixed(2);
      creditsDebtsService.getAllCreditsDebtsOfDebtsToPayByDebtsToPayId(debtsToPay.id).then(function(response) {
        vm.creditsDebtsList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.createAbonoDebts = function(creditsDebts) {
      vm.loading = true;
      vm.creditsDebtsValue = (creditsDebts.valor).toFixed(2);
      vm.creditsDebtsId = creditsDebts.id;
      vm.paymentDebts = {
        creditsDebts: creditsDebts
      };
      vm.loading = false;
    };

    vm.pAbonoDebts = function(paymentDebts) {
      vm.loading = true;
      if (vm.paymentDebts.typePayment == 'PAC') {
        vm.valorReten = 0.00;
      };
      vm.paymentDebts.datePayment = $('#pickerDateOfPayment').data("DateTimePicker").date().toDate().getTime();
      vm.paymentDebts.creditId = vm.creditsDebtsId;
      vm.paymentDebts.documentNumber = vm.debtsBillNumber;
      vm.paymentDebts.valorReten = vm.valorReten;
      paymentDebtsService.create(paymentDebts).then(function(response) {
        vm.error = undefined;
        vm.success = 'Abono realizado con exito';
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
      _activate();
    };

    vm.debtsToPaySelected = function(debts) {
      vm.loading = true;
      debtsToPayService.getById(debts.id).then(function(response) {
        vm.providerDebtsList = undefined;
        vm.debtsToPay = response;
        vm.debtsDetails = response.detailDebtsToPay;
        vm.companyData = $localStorage.user.subsidiary;
        vm.debtsProviderSelected = response.provider;
        vm.pagos = response.pagos;
        var dateToShow = new Date(response.fecha);
        vm.billNumber = response.billNumber;
        vm.seqNumber = response.debtsSeq;
        $('#pickerDebtsToPayDate').data("DateTimePicker").date(dateToShow);
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getTotalDebitoRep = function() {
      var totalDebito = 0;
      if (vm.debtsDetails) {
        _.each (vm.debtsDetails, function(detail) {
          if (detail.tipo === 'DEBITO (D)') {
            totalDebito = (parseFloat(totalDebito) + parseFloat(detail.baseImponible)).toFixed(2);
          };
        });
      };
      vm.saldoDebito = totalDebito;
      return totalDebito;
    };

    vm.getTotalCreditoRep = function() {
      var totalCredito = 0;
      if (vm.debtsDetails) {
        _.each(vm.debtsDetails, function(detail) {
          if (detail.tipo === 'CREDITO (C)') {
            totalCredito = (parseFloat(totalCredito) + parseFloat(detail.baseImponible)).toFixed(2);
          };
        });
      };
      vm.saldoCredito = totalCredito;
      return totalCredito;
    };

    vm.getTotalSaldoRep = function() {
      var totalSaldo = 0;
      totalSaldo = (parseFloat(vm.saldoCredito) - parseFloat(vm.saldoDebito)).toFixed(2);
      return totalSaldo;
    };

    vm.cancel = function() {
      _activate();
    };

    vm.exit = function() {
      $location.path('/home');
    };

    (function initController() {
      _activate();
    })();
});
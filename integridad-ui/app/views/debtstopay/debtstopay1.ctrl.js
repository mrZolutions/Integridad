'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:DebtsToPayCtrl
 * @description
 * # DebtsToPayCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('DebtsToPayCtrl1', function(_, $localStorage, providerService, cuentaContableService, debtsToPayService, authService, contableService,
                                           utilSeqService, creditsDebtsService, paymentDebtsService, $location, eretentionService, cashierService, comprobanteService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.userData = $localStorage.user;
        
        vm.mrZolutions = '4907601b-6e54-4675-80a8-ab6503e1dfeb';
        vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';
        vm.catedral = '1e2049c3-a3bc-4231-a0de-dded8020dc1b';
        vm.ppe = '0a28cbbf-98d5-4ce3-be36-33a7a83bc29e';

        vm.providerList = undefined;
        vm.ctaCtableBankList = undefined;
        vm.providerSelected = undefined;
        vm.providerDebtsList = undefined;
        vm.paymentCreation = false;
        vm.billsSelected = [];
        vm.itemsMultiplePayments = [];
        vm.modePayment = undefined;
        vm.valorDocumento = undefined;
        vm.noAccount = undefined;
        vm.ctaCtableBankCode = undefined;
        vm.bankName = undefined;
        vm.paymenDetail = undefined;
        vm.cardBrand = undefined;
        vm.numeroLote = undefined;
        vm.billCreation = false;
        vm.retention = undefined;

        vm.seqNumber = undefined;
        vm.subIva = undefined;
        vm.subTotalDoce = undefined;
        vm.subTotalCero = undefined;
        vm.subTotal = undefined;
        vm.totalTotal = undefined;
        vm.typeTaxes = undefined;

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
            {code: 'credito', name: 'Crédito'}
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

        vm.typeContab = 'CxP. CON RETENCIONES';
        vm.typeContabCe = 'COMP. DE EGRESO';

        //Función de activación del módulo de Cuentas por Pagar
        function _activate() {
            vm.advertencia = false;
            vm.providerList = undefined;
            vm.ctaCtableBankList = undefined;
            vm.providerSelected = undefined;
            vm.providerDebtsList = undefined;
            vm.paymentCreation = false;
            vm.billsSelected = [];
            vm.itemsMultiplePayments = [];
            vm.modePayment = undefined;
            vm.valorDocumento = undefined;
            vm.noAccount = undefined;
            vm.ctaCtableBankCode = undefined;
            vm.bankName = undefined;
            vm.paymenDetail = undefined;
            vm.cardBrand = undefined;
            vm.numeroLote = undefined;
            vm.billCreation = false;
            vm.debtsToPay = undefined;
            vm.dailybookCxP = undefined;
            vm.retention = undefined;
            vm.medio = {};
            vm.pagos = [];
            vm.threeNumberOne = undefined;
            vm.threeNumberTwo = undefined;
            vm.seccondPartNumber = undefined;

            vm.usrCliId = $localStorage.user.subsidiary.userClient.id;
            vm.subCxPActive = $localStorage.user.subsidiary.cxp;
            
            if (vm.subCxPActive) {
                vm.loading = true;
                providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                    vm.providerList = response;
                    vm.loading = false;
                    setTimeout(function() {
                        document.getElementById("input41").focus();
                    }, 200);
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.advertencia = true;
            };
        };

        vm.providerSelect = function(provider){
            console.log(provider)
            vm.loading = true;
            vm.success = undefined;
            vm.update = false;
            vm.billCreation = true;
            vm.providerSelected = provider;
            vm.providerId = provider.id;
            vm.providerName = provider.name;
            vm.companyData = $localStorage.user.subsidiary;
            _getSeqNumber();
            _initializeDebts();
            _getDailyCxPSeqNumber();
            _initializeDailybookCxP();
            var today = new Date();
            $('#pickerDateDebtsToPay').data("DateTimePicker").date(today);
            //TODO check ejercicio fiscal change *********************************************************************************************************
            // $('#pickerDateDebtsToPay').on("dp.change", function(data) {
            //     vm.ejercicio = ('0' + ($('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' + $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getFullYear();
            // });
            vm.ejercicio = ('0' + (today.getMonth() + 1)).slice(-2) + '/' + today.getFullYear();

            var fnf1 = document.getElementById("billNumberInputFirst");
            var fnf2 = document.getElementById("billNumberInputSecond");
            var fnf3 = document.getElementById("billNumberInputThird");

            fnf1.addEventListener('blur', function(evt){
                vm.threeNumberOne = utilSeqService._pad_with_zeroes(this.value, 3);
                fnf1.value = vm.threeNumberOne;
                vm.completeBillNumber();
            }, false);
            fnf2.addEventListener('blur', function(evt){
                vm.threeNumberTwo = utilSeqService._pad_with_zeroes(this.value, 3);
                fnf2.value = vm.threeNumberTwo;
                vm.completeBillNumber();
            }, false);
            fnf3.addEventListener('blur', function(evt){
                vm.seccondPartNumber = utilSeqService._pad_with_zeroes(this.value, 9);
                fnf3.value = vm.seccondPartNumber;
                vm.completeBillNumber();
            }, false);
            
            vm.loading = false;
        };

        vm.completeBillNumber = function() {
            if(vm.threeNumberOne !== undefined && vm.threeNumberTwo !== undefined && vm.seccondPartNumber !== undefined){
                vm.debtsToPay.threeNumberOne = vm.threeNumberOne;
                vm.debtsToPay.threeNumberTwo = vm.threeNumberTwo;
                vm.debtsToPay.seccondPartNumber = vm.seccondPartNumber;
                vm.debtsToPay.billNumber = vm.debtsToPay.threeNumberOne + '-' + vm.debtsToPay.threeNumberTwo + '-' + vm.debtsToPay.seccondPartNumber;
            }

            console.log(vm.debtsToPay)
        }

        vm.getRetentionByProviderAndDocumentNumber = function(){
            vm.loading = true;
            eretentionService.getRetentionByProviderIdAndDocumentNumber(vm.providerId, vm.debtsToPay.billNumber).then(function(response) {
                vm.retentionList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.retentionSelected = function(retention){
            vm.loading = true;
            $('#modalFindRetention').modal('hide');
            vm.retentionList = undefined;
            eretentionService.getRetentionById(retention.id).then(function(response) {
                vm.retention = response;
                console.log(vm.retention)
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        }

        vm.addRetentionToDebtsDetail = function() {
            vm.retenSelected = true;
            vm.debtsToPay.billNumber
            // vm.debtsBillNumber = numOne + '-' + numTwo + '-' + parNum;
            vm.generalDetailCxP = vm.providerName + ' Fact. ' + vm.debtsToPay.billNumber;
            _.map(vm.retention.detailRetentions, function(detail) {
                if (detail.taxType == 'RETENCION EN LA FUENTE') {
                    vm.retenCodeFuente = detail.code;
                    vm.retenTaxTypeFuente = detail.taxType;
                    vm.retenTotalFuente = detail.total;
                } else if (detail.taxType == 'RETENCION EN EL IVA') {
                    vm.retenCodeIva = detail.code;
                    vm.retenTaxTypeIva = detail.taxType;
                    vm.retenTotalIva = detail.total;
                };
                // Usuario La Quinta
                if (vm.usrCliId === vm.laQuinta) {
                    if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                        switch(vm.retenCodeFuente) {
                            case '303':
                                vm.retenFteCodeContable = '2.01.07.01.002'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Honorarios Profesionales'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '304':
                                vm.retenFteCodeContable = '2.01.07.01.003'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios Profesionales predomina intelecto'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '307':
                                vm.retenFteCodeContable = '2.01.07.01.004'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios Mano de Obra'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '308':
                                vm.retenFteCodeContable = '2.01.07.01.005'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios entre Sociedades'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '309':
                                vm.retenFteCodeContable = '2.01.07.01.006'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios Publicidad y Comunicación'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '310':
                                vm.retenFteCodeContable = '2.01.07.01.007'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Transporte Privado de Pasajeros o de Carga'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '312':
                                vm.retenFteCodeContable = '2.01.07.01.008'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Transferencia Bienes Muebles naturales'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '319':
                                vm.retenFteCodeContable = '2.01.07.01.009'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Arrendamiento Mercantil'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '320':
                                vm.retenFteCodeContable = '2.01.07.01.010'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Arrendamiento Bienes Inmuebles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '322':
                                vm.retenFteCodeContable = '2.01.07.01.011'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Seguros y Reaseguros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '323':
                                vm.retenFteCodeContable = '2.01.07.01.012'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Rendimientos Financieros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '327':
                                vm.retenFteCodeContable = '2.01.07.01.013'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Venta de Combustibles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '328':
                                vm.retenFteCodeContable = '2.01.07.01.014'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Venta de Combustibles a distribuidores'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '340':
                                vm.retenFteCodeContable = '2.01.07.01.015'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 1%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '341':
                                vm.retenFteCodeContable = '2.01.07.01.016'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 2%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '342':
                                vm.retenFteCodeContable = '2.01.07.01.017'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 8%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '343':
                                vm.retenFteCodeContable = '2.01.07.01.018'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 25%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '344':
                                vm.retenFteCodeContable = '2.01.07.01.019'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables a otros porcentajes'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                        };
                    };
                    if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                        switch(vm.retenCodeIva) {
                            case '721':
                                vm.retenIvaCodeContable = '2.01.07.02.001'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA DEL 10% (Bienes)'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '723':
                                vm.retenIvaCodeContable = '2.01.07.02.002'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA DEL 20% (Servicios)'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '725':
                                vm.retenIvaCodeContable = '2.01.07.02.003'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA DEL 30%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '727':
                                vm.retenIvaCodeContable = '2.01.07.02.004'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA DEL 50%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '729':
                                vm.retenIvaCodeContable = '2.01.07.02.005'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA DEL 70%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '731':
                                vm.retenIvaCodeContable = '2.01.07.02.006'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA DEL 100%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                        };
                    };
                // Usuario Mr. Zolutions
                } else if (vm.usrCliId === vm.mrZolutions) {
                    if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                        switch(vm.retenCodeFuente) {
                            case '303':
                                vm.retenFteCodeContable = '2.01.07.03.303'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 10% Honorarios Profesionales Dietas'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '304':
                                vm.retenFteCodeContable = '2.01.07.03.304'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 8% Servicios predomina el Intelecto'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '307':
                                vm.retenFteCodeContable = '2.01.07.03.307'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 2% Servicios predomina Mano de Obra'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '309':
                                vm.retenFteCodeContable = '2.01.07.03.309'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 1% Servicios de Publicidad y Comunicación'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '310':
                                vm.retenFteCodeContable = '2.01.07.03.310'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 1% Servicios de Transporte'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '312':
                                vm.retenFteCodeContable = '2.01.07.03.312'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 1% Transferencia de Bienes Muebles Naturales'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '319':
                                vm.retenFteCodeContable = '2.01.07.03.319'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 1% Arrendamiento Mercantil'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '320':
                                vm.retenFteCodeContable = '2.01.07.03.320'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 8% Arrendamiento de Inmuebles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '322':
                                vm.retenFteCodeContable = '2.01.07.03.322'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Seguros y Reaseguros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '323':
                                vm.retenFteCodeContable = '2.01.07.03.323'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 2% Rendimientos Financieros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '325':
                                vm.retenFteCodeContable = '2.01.07.03.325'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 15% Anticipo Dividendos'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '327':
                                vm.retenFteCodeContable = '2.01.07.03.327'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 0.2% Venta de Combustibles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '342':
                                vm.retenFteCodeContable = '2.01.07.03.342'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 8% Otras Retenciones Aplicables'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '344':
                                vm.retenFteCodeContable = '2.01.07.03.344'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'RF 2% Otras Retenciones Aplicables'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                        };
                    };
                    if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                        switch(vm.retenCodeIva) {
                            case '721':
                                vm.retenIvaCodeContable = '2.01.07.02.721'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 10%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '723':
                                vm.retenIvaCodeContable = '2.01.07.02.723'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 20%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '725':
                                vm.retenIvaCodeContable = '2.01.07.02.725'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 30%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '727':
                                vm.retenIvaCodeContable = '2.01.07.02.727'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 50%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '729':
                                vm.retenIvaCodeContable = '2.01.07.02.729'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 70%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '731':
                                vm.retenIvaCodeContable = '2.01.07.02.731'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 100%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                        };
                    };
                // Usuario Catedral
                } else if (vm.usrCliId === vm.catedral) {
                    if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                        switch(vm.retenCodeFuente) {
                            case '303':
                                vm.retenFteCodeContable = '2.12.40.202'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Honorarios Profesionales Dietas'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '304':
                                vm.retenFteCodeContable = '2.12.40.203'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios predomina el Intelecto'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '307':
                                vm.retenFteCodeContable = '2.12.40.204'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios predomina Mano de Obra'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '308':
                                vm.retenFteCodeContable = '2.12.40.205'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios entre Sociedades'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '309':
                                vm.retenFteCodeContable = '2.12.40.206'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios de Publicidad y Comunicación'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '310':
                                vm.retenFteCodeContable = '2.12.40.207'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios de Transporte'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '312':
                                vm.retenFteCodeContable = '2.12.40.208'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Transferencia de Bienes Muebles Naturales'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '319':
                                vm.retenFteCodeContable = '2.12.40.209'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Arrendamiento Mercantil'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '320':
                                vm.retenFteCodeContable = '2.12.40.210'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Arrendamiento de Inmuebles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '322':
                                vm.retenFteCodeContable = '2.12.40.211'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Seguros y Reaseguros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '323':
                                vm.retenFteCodeContable = '2.12.40.212'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Rendimientos Financieros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '327':
                                vm.retenFteCodeContable = '2.12.40.213'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Venta de Combustibles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '328':
                                vm.retenFteCodeContable = '2.12.40.214'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Venta de Combustibles a distribuidores'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '340':
                                vm.retenFteCodeContable = '2.12.40.215'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 1%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '341':
                                vm.retenFteCodeContable = '2.12.40.216'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 2%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '342':
                                vm.retenFteCodeContable = '2.12.40.217'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 8%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '343':
                                vm.retenFteCodeContable = '2.12.40.218'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras Retenciones aplicables al 25%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '344':
                                vm.retenFteCodeContable = '2.12.40.219'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Aplicables a otros porcentajes'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                        };
                    };
                    if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                        switch(vm.retenCodeIva) {
                            case '725':
                                vm.retenIvaCodeContable = '2.12.40.103'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 30%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '729':
                                vm.retenIvaCodeContable = '2.12.40.104'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 70%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '731':
                                vm.retenIvaCodeContable = '2.12.40.105'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 100%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                        };
                    };
                // Usuario PPE
                } else if (vm.usrCliId === vm.ppe) {
                    if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                        switch(vm.retenCodeFuente) {
                            case '304':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios predomina el intelecto no relacionados con el título profesional'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '304A':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Comisiones y demás pagos por servicios predomina intelecto'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '304B':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Pagos a notarios y registradores de la propiedad y mercantil'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '309':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios de Publicidad y Comunicación'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '310':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Servicios de Transporte privado de pasajeros o transporte público'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '312':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Transferencia de Bienes Muebles Naturales'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '320':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Arrendamiento de Inmuebles'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '323':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Rendimientos Financieros'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '332':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras compras de bienes y servicios no sujetas a retención'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '332G':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Pagos con Tarjeta de Crédito'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '332I':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Pago a través de Convenio de Débito (Clientes IFIs)'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '334':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Enajenación de derechos representativos de capital y otros derechos no cotizados'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '344':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Otras retenciones aplicables el 2%'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '501':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Pago al exterior - Beneficios Empresariales'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '501A':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Pago a no residentes - Servicios técnicos'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                            case '505E':
                                vm.retenFteCodeContable = '2.01.07.01.03'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                                vm.retenFteNombContable = 'Pago a no residentes - Intereses de otros créditos externos'; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                                vm.retenFteNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValorCxP = parseFloat(vm.retenTotalFuente);
                            break;
                        };
                    };
                    if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                        switch(vm.retenCodeIva) {
                            case '721':
                                vm.retenIvaCodeContable = '2.01.07.01.04'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 10%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '723':
                                vm.retenIvaCodeContable = '2.01.07.01.04'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 20%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '725':
                                vm.retenIvaCodeContable = '2.01.07.01.04'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 30%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '727':
                                vm.retenIvaCodeContable = '2.01.07.01.04'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 50%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '729':
                                vm.retenIvaCodeContable = '2.01.07.01.04'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 70%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                            case '731':
                                vm.retenIvaCodeContable = '2.01.07.01.04'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                                vm.retenIvaNombContable = 'RETENCION IVA 100%'; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                                vm.retenIvaNombContableCxP = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValorCxP = parseFloat(vm.retenTotalIva);
                            break;
                        };
                    };
                };

                vm.itemRetentionFuente = undefined;
                vm.itemRetentionFuente = { 
                    codigo_contable: vm.retenFteCodeContable, desc_contable: vm.retenFteDescContable, 
                    tipo: 'CREDITO (C)', base_imponible: vm.retenFteValor,
                    nomb_contable: vm.retenFteNombContable, haber: vm.retenFteValor
                };

                vm.itemRetentionIVA = undefined;
                vm.itemRetentionIVA = { 
                    codigo_contable: vm.retenIvaCodeContable, desc_contable: vm.retenIvaDescContable,
                    tipo: 'CREDITO (C)', base_imponible: vm.retenIvaValor,
                    nomb_contable: vm.retenIvaNombContable, haber: vm.retenIvaValor
                };

                if (vm.retenCodeFuente != null) {
                    vm.debtsToPay.items.push(vm.itemRetentionFuente);
                };
            
                if (vm.retenCodeIva != null) {
                    vm.debtsToPay.items.push(vm.itemRetentionIVA);
                };
                    
                //Diario CxP
                vm.itemRetentionFuenteCxP = undefined;
                vm.itemRetentionFuenteCxP = { 
                    typeContab: vm.typeContab, codeConta: vm.retenFteCodeContable,
                    descrip: vm.retenFteDescContable, tipo: 'CREDITO (C)',
                    baseImponible: vm.retenFteValor, name: vm.retenFteNombContableCxP,
                    haber: vm.retenFteValor
                };
                
                vm.itemRetentionIVACxP = undefined;
                vm.itemRetentionIVACxP = { 
                    typeContab: vm.typeContab, codeConta: vm.retenIvaCodeContable,
                    descrip: vm.retenIvaDescContable, tipo: 'CREDITO (C)',
                    baseImponible: vm.retenIvaValor, name: vm.retenIvaNombContableCxP,
                    haber: vm.retenIvaValor
                };
        
                if (vm.retenCodeFuente != null) {
                    vm.itemRetentionFuenteCxP.numCheque = '--';
                    vm.itemRetentionFuenteCxP.dailybookNumber = vm.dailycxpStringSeq;
                    vm.itemRetentionFuenteCxP.userClientId = vm.usrCliId;
                    vm.itemRetentionFuenteCxP.dateDetailDailybook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
                    vm.dailybookCxP.detailDailybookContab.push(vm.itemRetentionFuenteCxP);
                };
                
                if (vm.retenCodeIva != null) {
                    vm.itemRetentionIVACxP.numCheque = '--';
                    vm.itemRetentionIVACxP.dailybookNumber = vm.dailycxpStringSeq;
                    vm.itemRetentionIVACxP.userClientId = vm.usrCliId;
                    vm.itemRetentionIVACxP.dateDetailDailybook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
                    vm.dailybookCxP.detailDailybookContab.push(vm.itemRetentionIVACxP);
                };
            
                vm.itemRetentionFuenteCxP = undefined;
                vm.itemRetentionIVACxP = undefined;
                vm.retenCodeFuente = undefined;
                vm.retenCodeIva = undefined;
            });
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

        vm.getPercentageTableAll = function() {
            cuentaContableService.getCuentaContableByUserClientNoBank(vm.usrCliId).then(function(response) {
                vm.cuentaContableList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
            vm.typeTaxes = vm.debtsToPay.typeTaxes;
        };

        vm.selectPurchaseTable = function(purchaseType) {
            cuentaContableService.getCuentaContableByType(vm.usrCliId, purchaseType).then(function(response) {
                vm.cuentaContableList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.selectTaxes = function(tax) {
            vm.item = undefined;
            vm.item = {
                cta_contable: tax.id,
                codigo: parseInt(vm.debtsToPay.typeTaxes),
                codigo_contable: tax.code,
                desc_contable: tax.description,
                tipo: tax.accountType,
                nomb_contable: tax.name,
            };
            vm.subTotal = parseFloat((vm.debtsToPay.total / 1.12).toFixed(2));
            vm.totalTotal = parseFloat(vm.debtsToPay.total);

            var totalDebito = parseInt(vm.debtsToPay.typeTaxes) === 1 ? vm.subTotal : vm.totalTotal;
            if (vm.debtsToPay) {
                _.each (vm.debtsToPay.items, function(detail) {
                    if (detail.tipo === 'DEBITO (D)') {
                        totalDebito = (parseFloat(totalDebito) - parseFloat(detail.base_imponible)).toFixed(2);
                    };
                });
            };
            vm.item.base_imponible = totalDebito;

            //Diario CxP
            vm.itema = undefined;
            vm.itema = {
                typeContab: vm.typeContab,
                codeConta: tax.code,
                descrip: tax.description,
                tipo: tax.accountType,
                name: tax.name
            };
            vm.subTotalDoceCxP = parseFloat((vm.debtsToPay.total / 1.12).toFixed(2));
        };

        vm.addItem = function() {
            if (vm.indexEdit !== undefined) {
                vm.debtsToPay.items.splice(vm.indexEdit, 1);
                vm.dailybookCxP.detailDailybookContab.splice(vm.indexEdit, 1);
                vm.indexEdit = undefined;
            };
            if (vm.item.tipo == 'DEBITO (D)') {
                vm.item.deber = vm.item.base_imponible;
            } else if (vm.item.tipo == 'CREDITO (C)') {
                vm.item.haber = vm.item.base_imponible;
            };
            vm.debtsToPay.items.push(vm.item);
            
            //Diario CxP
            if (vm.itema.tipo == 'DEBITO (D)') {
                vm.itema.deber = vm.item.base_imponible;
            } else if (vm.itema.tipo == 'CREDITO (C)') {
                vm.itema.haber = vm.item.base_imponible;
            };
            vm.itema.baseImponible = vm.item.base_imponible
            vm.itema.numCheque =  '--';
            vm.itema.dailybookNumber = vm.dailycxpStringSeq;
            vm.itema.userClientId = vm.usrCliId;
            vm.itema.dateDetailDailybook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
            vm.dailybookCxP.detailDailybookContab.push(vm.itema);
            
            vm.item = undefined;
            vm.itema = undefined;
            vm.debtsToPay.typeTaxes = undefined;
            vm.cuentaContableList = undefined;
        };

        vm.addIvaAndProvider = function() {
            if (vm.generalDetailCxP == null || vm.generalDetailCxP == '') {
                vm.generalDetailCxP = vm.providerName + ' Fact. ' + vm.debtsToPay.billNumber;
            };

            if (vm.indexEdit !== undefined) {
                vm.debtsToPay.items.splice(vm.indexEdit, 1);
                vm.indexEdit = undefined;
            };
            vm.retentionTotal =  vm.retention === undefined ? 0 : vm.retention.total;

            //Selección de las Cuentas Contables por defecto dependiendo del Cliente
            if (vm.usrCliId === vm.laQuinta) {
                vm.ivaContable = '1.01.05.01.001'; vm.provContable = '2.01.03.01.001';
            } else if (vm.usrCliId === vm.mrZolutions) {
                vm.ivaContable = '1.01.05.02.001'; vm.provContable = '2.01.03.01.001';
            } else if (vm.usrCliId === vm.catedral) {
                vm.ivaContable = '1.14.10.201'; vm.provContable = '2.12.10.101';
            } else if (vm.usrCliId === vm.ppe) {
                vm.ivaContable = '1.01.05.01.01'; vm.provContable = '2.01.03.01.01';
            } else {
                vm.ivaContable = '1.01.01.01'; vm.provContable = '2.01.01.01';
            };
          
            if (vm.typeTaxes === '1') {
                vm.subIva = (vm.item.base_imponible * 0.12).toFixed(2);
                vm.subTotalDoce = vm.item.base_imponible;
                vm.subTotalCero = vm.debtsToPay.total - vm.subTotalDoce - vm.subIva;
                vm.itemIva = {
                    codigo_contable: vm.ivaContable,
                    desc_contable: 'IVA EN COMPRAS',
                    tipo: 'DEBITO (D)',
                    base_imponible: vm.subIva,
                    nomb_contable: 'DEFINIDA PARA TODAS LAS COMPRAS',
                    deber: vm.subIva
                };
                var subBase = 0;
                subBase = (vm.debtsToPay.total - vm.retentionTotal).toFixed(2);
                vm.itemProvider = {
                    codigo_contable: vm.provContable,
                    desc_contable: 'PROVEEDORES LOCALES',
                    tipo: 'CREDITO (C)',
                    base_imponible: parseFloat((vm.debtsToPay.total - vm.retentionTotal).toFixed(2)),
                    nomb_contable: 'DEFINIDA PARA TODOS LOS PROVEEDORES',
                    haber: subBase
                };
                vm.debtsToPay.items.push(vm.itemIva);
                vm.debtsToPay.items.push(vm.itemProvider);

                //Diario Cxp
                vm.itemIvaCxP = {
                    typeContab: vm.typeContab,
                    codeConta: vm.ivaContable,
                    descrip: 'IVA EN COMPRAS',
                    tipo: 'DEBITO (D)',
                    baseImponible: vm.subIva,
                    name: vm.generalDetailCxP,
                    deber: vm.subIva
                };
                vm.itemIvaCxP.numCheque = '--';
                vm.itemIvaCxP.dailybookNumber = vm.dailycxpStringSeq;
                vm.itemIvaCxP.userClientId = vm.usrCliId;
                vm.itemIvaCxP.dateDetailDailybook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
                vm.itemProviderCxP = {
                    typeContab: vm.typeContab,
                    codeConta: vm.provContable,
                    descrip: 'PROVEEDORES LOCALES',
                    tipo: 'CREDITO (C)',
                    baseImponible: parseFloat((vm.debtsToPay.total - vm.retentionTotal).toFixed(2)),
                    name: vm.generalDetailCxP,
                    haber: subBase
                };
                vm.itemProviderCxP.numCheque = '--';
                vm.itemProviderCxP.dailybookNumber = vm.dailycxpStringSeq;
                vm.itemProviderCxP.userClientId = vm.usrCliId;
                vm.itemProviderCxP.dateDetailDailybook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
                vm.dailybookCxP.detailDailybookContab.push(vm.itemIvaCxP);
                vm.dailybookCxP.detailDailybookContab.push(vm.itemProviderCxP);

            } else if (vm.typeTaxes === '2') {
                vm.subIva = 0;
                vm.subTotalDoce = 0;
                vm.subTotalCero = vm.debtsToPay.total;
                var subBaseDos = 0;
                subBaseDos = (vm.debtsToPay.total - vm.retentionTotal).toFixed(2);
                vm.itemProvider = {
                    codigo_contable: vm.provContable,
                    desc_contable: 'PROVEEDORES LOCALES',
                    tipo: 'CREDITO (C)',
                    base_imponible: parseFloat((vm.debtsToPay.total - vm.retentionTotal).toFixed(2)),
                    nomb_contable: 'DEFINIDA PARA TODOS LOS PROVEEDORES',
                    haber: subBaseDos
                };
                vm.debtsToPay.items.push(vm.itemProvider);

                //Diario Cxp
                vm.itemProviderCxP = {
                    typeContab: vm.typeContab,
                    codeConta: vm.provContable,
                    descrip: 'PROVEEDORES LOCALES',
                    tipo: 'CREDITO (C)',
                    baseImponible: parseFloat((vm.debtsToPay.total - vm.retentionTotal).toFixed(2)),
                    name: vm.generalDetailCxP,
                    haber: subBaseDos
                };
                vm.itemProviderCxP.numCheque = '--';
                vm.itemProviderCxP.dailybookNumber = vm.dailycxpStringSeq;
                vm.itemProviderCxP.userClientId = vm.usrCliId;
                vm.itemProviderCxP.dateDetailDailybook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
                vm.dailybookCxP.detailDailybookContab.push(vm.itemProviderCxP);
            };
        };

        vm.formaDePago = function() {
            vm.medio.country = '593 - ECUADOR';
            vm.medio.medio = 'credito'
            vm.loadMedio();
        }

        vm.loadMedio = function() {
            var payed = 0;
            _.each(vm.pagos, function(pago){
                payed += parseFloat(pago.total);
            });
            if (vm.medio.medio === 'credito') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.total = parseFloat((vm.debtsToPay.total - payed).toFixed(4));
                vm.status = 'PENDIENTE';
            };
        };

        vm.getFechaCobro = function() {
            var d = new Date();
            vm.medio.fechaCobro = _addDays(d, parseInt(vm.medio.chequeDiasPlazo));
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
                    vm.varPago = parseFloat((vm.varPago + med.total).toFixed(2));
                });
                vm.getCambio = parseFloat((vm.varPago - vm.debtsToPay.total).toFixed(2));
                vm.aux = parseFloat((vm.varPago - vm.getCambio).toFixed(2));
            };
            return vm.varPago;
        };

        vm.saveDebtsToPay = function(debtsToPay) {
            vm.loading = true;
            vm.pagos.total = vm.aux;
            $('#modalAddPago').modal('hide');
            vm.debtsToPay.fecha = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
            vm.debtsToPay.providerId = vm.providerId;
            vm.debtsToPay.iva = vm.subIva;
            vm.debtsToPay.subTotalDoce = vm.subTotalDoce;
            vm.debtsToPay.subTotalCero = vm.subTotalCero;
            vm.debtsToPay.debtsSeq = vm.seqNumber;
            vm.debtsToPay.ejercicio = vm.ejercicio;
            if (vm.status === 'PENDIENTE') {
                vm.debtsToPay.saldo = vm.debtsToPay.total - parseFloat(vm.retention.total);
            } else {
                vm.debtsToPay.saldo = 0;
            };
            if (vm.retention == undefined) {
                vm.debtsToPay.retentionNumber = 'SIN RETENCIÓN';
                vm.debtsToPay.retentionDateCreated = 0;
                vm.debtsToPay.retentionTotal = 0;
            } else {
                vm.debtsToPay.retentionId = vm.retention.id;
                vm.debtsToPay.retentionNumber = vm.retention.stringSeq;
                vm.debtsToPay.retentionDateCreated = vm.retention.dateCreated;
                vm.debtsToPay.retentionTotal = parseFloat(vm.retention.total);
            };
            vm.debtsToPay.estado = vm.status;
            vm.debtsToPay.detailDebtsToPay = [];
            vm.debtsToPay.pagos = vm.pagos;
            _.each(vm.debtsToPay.items, function(item) {
                var detail = {
                    codeConta: item.codigo_contable,
                    baseImponible: item.base_imponible,
                    descrip: item.desc_contable,
                    name: item.nomb_contable,
                    tipo: item.tipo
                };
                vm.debtsToPay.detailDebtsToPay.push(detail);
            });
            debtsToPayService.getDebtsToPayByBillNumberAndAuthoNumber(vm.usrCliId, vm.debtsToPay.billNumber, vm.debtsToPay.authorizationNumber).then(function(response) {
                if (response.length === 0) {
                    debtsToPayService.createDebtsToPay(debtsToPay).then(function(respDebtsToPay) {
                        vm.debtsToPay = respDebtsToPay;
                        vm.responseDebtsToPay = respDebtsToPay;
                        $localStorage.user.cashier.debtsNumberSeq = vm.debtsToPay.debtsSeq;
                        if (vm.seqChanged) {
                            cashierService.update($localStorage.user.cashier).then(function(resp) {
                                //cashier actualizado en caso de haber sido cambiado manualmente
                            }).catch(function(error) {
                                vm.loading = false;
                                vm.error = error.data;
                            });
                        };
                        _asientoDiarioCxP();
                        vm.debtsToPayCreated = true;
                        vm.success = 'Factura de Compra Ingresada con Exito';
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                    _activate();
                } else {
                    vm.error = 'Factura de Compra Ya Existe';
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.loadCredit = function() {
            var creditArray = [];
            var diasPlazo = parseInt(vm.medio.creditoIntervalos);
            var billNumber = vm.debtsToPay.billNumber;
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

        vm.providerMultipleDebts = function(provider) {
            vm.loading = true;
            vm.success = undefined;
            vm.paymentCreation = true;
            vm.providerSelected = provider;
            vm.typePayment = 'PAC';
            vm.typePaymentName = 'PAGO CORRIENTE';
            vm.providerName = provider.name;
            _initializeDailybookCe();
            _initializeComprobantePago();
            cuentaContableService.getCuentaContableByUserClientAndBank(vm.usrCliId).then(function(response) {
                vm.ctaCtableBankList = response;
                
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });

            debtsToPayService.getDebtsToPayWithSaldoByProviderId(provider.id).then(function(response) {
                vm.providerDebtsList = response;
                $('#modalFindBills').modal('show');
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.findBills = function() {
            vm.loading = true;
            vm.success = undefined;
            vm.providerDebtsList = undefined; 
            vm.billsSelected = [];
            debtsToPayService.getDebtsToPayWithSaldoByProviderId(vm.providerSelected.id).then(function(response) {
                vm.providerDebtsList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.payModeSelected = function() {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;

            _.each(vm.providerDebtsList, function(bill){
                if(bill.selectedTotal || bill.selectedParcial){
                    vm.billsSelected.push(bill);
                    creditsDebtsService.getAllCreditsDebtsOfDebtsToPayByDebtsToPayId(bill.id).then(function(response) {
                        if(bill.selectedTotal){
                            _.each(response, function(cuota) {
                                vm.itemCobroBill = {};
                                var itemBillsMulti = {
                                    creditsDebts: cuota,
                                    bill_number: bill.billNumber,
                                    debt_total: (bill.total).toFixed(2),
                                    debt_pending: bill.saldo,
                                    debt_abono: cuota.valor.toFixed(2),
                                    tipo: 'PAGO TOTAL DE CUOTA',
                                };
                                vm.itemsMultiplePayments.push(itemBillsMulti);
                            });
                        } else {
                            bill.creditsMultiBillsList = response;
                            _.each(bill.creditsMultiBillsList, function(cred){
                                cred.valorAbono = undefined;
                            });
                        }
                        vm.loading = false;
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                }
            }); 
            
            vm.loading = false;        
        };

        vm.multipleAbono = function() {
            vm.loading = true;
            _.each(vm.billsSelected, function(bill){
                if(bill.selectedParcial){
                    _.each(bill.creditsMultiBillsList, function(cuota) {
                        if(cuota.valorAbono !== undefined){
                            vm.itemCobroBill = {};
                            var itemBillsMulti = {
                                creditsDebts: cuota,
                                bill_number: bill.billNumber,
                                debt_total: (bill.total).toFixed(2),
                                debt_pending: bill.saldo,
                                debt_abono: parseFloat(cuota.valorAbono).toFixed(2),
                                tipo: parseFloat(cuota.valorAbono) < parseFloat(cuota.valor).toFixed(2) ? 'ABONO PARCIAL DE CUOTA' :  'PAGO TOTAL DE CUOTA',
                            };
                            vm.itemsMultiplePayments.push(itemBillsMulti);
                        }
                    });
                }
            });

            vm.loading = false;
            $('#modalFindBills').modal('hide');
        };

        vm.displayCredits = function(){
            if(vm.billsSelected.length <= 0 ) {
                return false;
            } else if(vm.billsSelected.length === 1 && vm.billsSelected[0].selectedTotal){
                $('#modalFindBills').modal('hide');
                return false;
            } else {
                return true;
            }
            
        }

        vm.changeAbonoType = function(){
            if(vm.typePayment === 'RET'){
                vm.paymenDetail = 'ABONO POR RETENCIÓN';
            } else if(vm.typePayment === 'CEG') {
                vm.paymenDetail = 'ABONO POR COMP. EGRESO';
            } else {
                vm.paymenDetail = undefined;
            }
        };

        vm.selectCtaCtableBank = function(cuenta) {
            vm.ctaCtableBankCode = cuenta.code;
            vm.noAccount = cuenta.number;
            vm.bankName = cuenta.description;
        };

        vm.getTotalMultiAbonos = function() {
            var totalMultiAbono = 0;
            _.each(vm.itemsMultiplePayments, function(detail) {
                totalMultiAbono = (parseFloat(totalMultiAbono) + parseFloat(detail.debt_abono)).toFixed(2);
            });
            vm.valorDocumento = totalMultiAbono;
            return totalMultiAbono;
        };

        vm.disableMultipleCobro = function(){
            if(vm.typePayment === 'PAC' && (vm.noDocument === undefined || '' === vm.noDocument.trim() || vm.noDocument === 0)){
                return true;
            }
            if(vm.typePayment === 'PAC' && vm.modePayment === undefined){
                return true;
            }
            if(vm.getTotalMultiAbonos() != vm.valorDocumento){
                return true;
            }
        
            return false;
        };

        vm.getDebtsBillNumberPayed = function() {
            var debtsBillNumberPayed = '';
            _.each(vm.itemsMultiplePayments, function(detail) {
                if(!debtsBillNumberPayed.includes(detail.bill_number))
                    debtsBillNumberPayed = debtsBillNumberPayed + ' ' + detail.bill_number;
            });
            return debtsBillNumberPayed;
        };

        vm.pAbonoDebts = function() {

            if (vm.usrCliId === vm.laQuinta) {
                vm.provContable = '2.01.03.01.001';
            } else if (vm.usrCliId === vm.mrZolutions) {
                vm.provContable = '2.01.03.01.001';
            } else if (vm.usrCliId === vm.catedral) {
                vm.provContable = '2.12.10.101';
            } else if (vm.usrCliId === vm.ppe) {
                vm.provContable = '2.01.03.01.01';
            } else {
                vm.provContable = '2.01.01.01';
            };
            
            vm.debtsBillsNumberPayed = vm.getDebtsBillNumberPayed();

            _.each(vm.itemsMultiplePayments, function(detail) {

                if (vm.typePayment == 'PAC' || vm.typePayment == 'CEG') {
                    vm.valorReten = 0;
                    vm.valorAbono = detail.debt_abono;
                } else if (vm.typePayment == 'RET') {
                    vm.valorAbono = 0;
                    vm.valorReten = detail.debt_abono;
                    vm.modePayment = 'RET'
                } 
                
                var paymentDebts = {
                    noDocument: vm.noDocument ? vm.noDocument : 'EFECTIVO',
                    modePayment: vm.modePayment,
                    typePayment: vm.typePayment,
                    datePayment: $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime(),
                    documentNumber: vm.debtsBillsNumberPayed,
                    ctaCtableBanco: vm.ctaCtableBankCode ? vm.ctaCtableBankCode : '--',
                    banco: vm.bankName ? vm.bankName : '--',
                    cardBrand: vm.cardBrand,
                    numeroLote:vm.numeroLote,
                    ctaCtableProvider: vm.provContable ? vm.provContable : '--',
                    providerName: vm.providerName ? vm.providerName : '--',
                    detail: detail.tipo,
                    noAccount: vm.noAccount ? vm.noAccount : '--',
                    valorAbono: vm.valorAbono,
                    valorReten: vm.valorReten,
                    creditsDebts: detail.creditsDebts,

                }

                paymentDebtsService.getPaymentDebtsByUserClientIdWithBankAndNroDocument(vm.usrCliId, paymentDebts.banco, paymentDebts.noDocument).then(function(response) {
                    paymentDebtsService.createPaymentDebts(paymentDebts).then(function(response) {
                        vm.paymentDebtsCreated = response;
                        _asientoComprobanteEgreso();
                        _asientoComprobantePago();
                        vm.success = 'Abono realizado con exito';
                        vm.loading = false;
                        _activate();
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            });
            
        };

        vm.cancel = function() {
            _activate();
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt($localStorage.user.cashier.debtsNumberSeq) + 1;
            vm.seqNumber = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
            vm.newSeqNumber = vm.seqNumber;
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

        function _getDailyCxPSeqNumber() {
            vm.numberAddedOneCxP = parseInt($localStorage.user.cashier.dailyCppNumberSeq) + 1;
            vm.dailycxpSeq = vm.numberAddedOneCxP;
            vm.dailycxpStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOneCxP, 6);
        };

        function _initializeDailybookCxP() {
            vm.dailybookCxP = {
                provider: vm.providerSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                subTotalDoce: 0,
                iva: 0,
                subTotalCero: 0,
                total: 0,
                detailDailybookContab: []
            };
        };

        function _initializeDailybookCe() {
            vm.dailybookCe = {
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                provider: vm.providerSelected,
                ruc: vm.providerSelected.ruc,
                subTotalDoce: 0,
                iva: 0,
                subTotalCero: 0,
                total: 0,
                detailDailybookContab: []
            };
        };

        function _initializeComprobantePago() {
            vm.comprobantePago = {
                provider: vm.providerSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                providerName: vm.providerSelected.name,
                providerRuc: vm.providerSelected.ruc,
                subTotalDoce: 0,
                iva: 0,
                total: 0,
                detailComprobantePago: []
            };
        };

        function _getDailyCeSeqNumber() {
            vm.numberAddedOneCe = parseInt($localStorage.user.cashier.dailyCeNumberSeq) + 1;
            vm.dailyCeSeq = vm.numberAddedOneCe;
            vm.dailyCeStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOneCe, 6);
        };

        function _getComprobantePagoSeqNumber() {
            vm.numbAddedOneCP = parseInt($localStorage.user.cashier.compPagoNumberSeq) + 1;
            vm.comprobantePagoSeq = vm.numbAddedOneCP;
            vm.comprobantePagoStringSeq = utilSeqService._pad_with_zeroes(vm.numbAddedOneCP, 6);
        };

        function _asientoComprobanteEgreso() {
            _getDailyCeSeqNumber();
            vm.valorToSave = vm.paymentDebtsCreated.modePayment === 'RET' ? vm.paymentDebtsCreated.valorReten : vm.paymentDebtsCreated.valorAbono;
            vm.selectedTypeBook = '2';
            vm.generalDetailCe_1 = vm.paymentDebtsCreated.providerName + ' Fact. ' + vm.paymentDebtsCreated.documentNumber;
            vm.itema = {
                typeContab: vm.typeContabCe,
                codeConta: vm.paymentDebtsCreated.ctaCtableProvider,
                descrip: 'PROVEEDORES LOCALES',
                tipo: 'DEBITO (D)',
                baseImponible: parseFloat(vm.valorToSave),
                name: vm.generalDetailCe_1,
                deber: parseFloat(vm.valorToSave)
            };
            vm.itema.numCheque =  '--';
            vm.itema.dailybookNumber = vm.dailyCeStringSeq;
            vm.itema.userClientId = vm.usrCliId;
            vm.itema.dateDetailDailybook = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            vm.dailybookCe.detailDailybookContab.push(vm.itema);
            vm.generalDetailCe_2 = vm.paymentDebtsCreated.banco + ' Cancela Fact. ' + vm.paymentDebtsCreated.documentNumber + ', a: ' + vm.paymentDebtsCreated.providerName + ', con ' + vm.paymentDebtsCreated.modePayment + ' Nro. ' + vm.paymentDebtsCreated.noDocument;
            vm.itemb = {
                typeContab: vm.typeContabCe,
                codeConta: vm.paymentDebtsCreated.ctaCtableBanco,
                descrip: vm.paymentDebtsCreated.banco,
                tipo: 'CREDITO (C)',
                baseImponible: parseFloat(vm.valorToSave),
                name: vm.generalDetailCe_2,
                haber: parseFloat(vm.valorToSave)
            };
            vm.itemb.numCheque = vm.paymentDebtsCreated.noDocument;
            vm.itemb.dailybookNumber = vm.dailyCeStringSeq;
            vm.itemb.userClientId = vm.usrCliId;
            vm.itemb.dateDetailDailybook = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            vm.dailybookCe.detailDailybookContab.push(vm.itemb);
            vm.dailybookCe.codeTypeContab = vm.selectedTypeBook;
            vm.dailybookCe.nameBank = vm.paymentDebtsCreated.banco;
            vm.dailybookCe.billNumber = vm.paymentDebtsCreated.documentNumber;
            vm.dailybookCe.numCheque = vm.paymentDebtsCreated.noDocument;
            vm.dailybookCe.typeContab = vm.typeContabCe;
            vm.dailybookCe.dailyCeSeq = vm.dailyCeSeq;
            vm.dailybookCe.dailyCeStringSeq = vm.dailyCeStringSeq;
            vm.dailybookCe.dailyCeStringUserSeq = 'PAGO GENERADO ' + vm.dailyCeStringSeq;
            vm.dailybookCe.clientProvName = vm.paymentDebtsCreated.providerName;
            vm.dailybookCe.generalDetail = vm.generalDetailCe_1;
            vm.dailybookCe.total = vm.valorToSave;
            vm.dailybookCe.iva = parseFloat((vm.valorToSave * 0.12).toFixed(2));
            vm.dailybookCe.subTotalDoce = parseFloat((vm.valorToSave / 1.12).toFixed(2));
            vm.dailybookCe.subTotalCero = 0;
            vm.dailybookCe.dateRecordBook = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            
            contableService.createDailybookAsinCe(vm.dailybookCe).then(function(response) {
                $localStorage.user.cashier.dailyCeNumberSeq = vm.dailybookCe.dailyCeSeq;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _asientoComprobantePago() {
            _getComprobantePagoSeqNumber();
            vm.valorToSave = vm.paymentDebtsCreated.modePayment === 'RET' ? vm.paymentDebtsCreated.valorReten : vm.paymentDebtsCreated.valorAbono;
            vm.generalDetailCP_1 = 'Pago de Fact. Nro: ' + vm.paymentDebtsCreated.documentNumber + ' a: ' + vm.paymentDebtsCreated.providerName;
            vm.itemPagoA = {
                codeConta: vm.paymentDebtsCreated.ctaCtableProvider,
                descrip: 'PROVEEDORES LOCALES',
                tipo: 'DEBITO (D)',
                baseImponible: parseFloat(vm.valorToSave),
                name: vm.generalDetailCP_1,
                deber: parseFloat(vm.valorToSave)
            };
            vm.comprobantePago.detailComprobantePago.push(vm.itemPagoA);
            vm.generalDetailCP_2 = vm.paymentDebtsCreated.banco + ' Cancela Fact. Nro: ' + vm.paymentDebtsCreated.documentNumber;
            vm.itemPagoB = {
                codeConta: vm.paymentDebtsCreated.ctaCtableBanco,
                descrip: vm.paymentDebtsCreated.banco,
                tipo: 'CREDITO (C)',
                baseImponible: parseFloat(vm.valorToSave),
                name: vm.generalDetailCP_2,
                haber: parseFloat(vm.valorToSave)
            };
            vm.comprobantePago.detailComprobantePago.push(vm.itemPagoB);

            vm.comprobantePago.dateComprobante = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            vm.comprobantePago.comprobanteSeq = vm.comprobantePagoSeq;
            vm.comprobantePago.comprobanteStringSeq = vm.comprobantePagoStringSeq;
            vm.comprobantePago.billNumber = vm.paymentDebtsCreated.documentNumber;
            vm.comprobantePago.numCheque = vm.paymentDebtsCreated.noDocument;;
            vm.comprobantePago.nameBank = vm.paymentDebtsCreated.banco;
            vm.comprobantePago.comprobanteEstado = 'PROCESADO';
            vm.comprobantePago.codeConta = vm.paymentDebtsCreated.ctaCtableBanco;
            vm.comprobantePago.paymentDebtId = vm.paymentDebtsCreated.id;
            vm.comprobantePago.comprobanteConcep = vm.generalDetailCP_1;
            vm.comprobantePago.total = vm.valorToSave;
            vm.comprobantePago.iva = parseFloat((vm.valorToSave * 0.12).toFixed(2));
            vm.comprobantePago.subTotalDoce = parseFloat((vm.valorToSave / 1.12).toFixed(2));

            comprobanteService.createComprobantePago(vm.comprobantePago).then(function(response) {
                $localStorage.user.cashier.compPagoNumberSeq = vm.comprobantePago.comprobanteSeq;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _asientoDiarioCxP() {
            vm.selectedTypeBook = '5';
            vm.dailybookCxP.codeTypeContab = vm.selectedTypeBook;
            vm.dailybookCxP.billNumber = vm.responseDebtsToPay.billNumber;
            vm.dailybookCxP.typeContab = vm.typeContab;
            vm.dailybookCxP.dailycxpSeq = vm.dailycxpSeq;
            vm.dailybookCxP.dailycxpStringSeq = vm.dailycxpStringSeq;
            vm.dailybookCxP.dailycxpStringUserSeq = 'DIARIO CxP GENERADO ' + vm.dailycxpStringSeq;
            vm.dailybookCxP.clientProvName = vm.responseDebtsToPay.provider.name;
            vm.dailybookCxP.generalDetail = vm.responseDebtsToPay.provider.name + ' Fact: ' + vm.responseDebtsToPay.billNumber;
            vm.dailybookCxP.total =  vm.responseDebtsToPay.total;
            vm.dailybookCxP.iva = parseFloat((vm.responseDebtsToPay.total * 0.12).toFixed(2));
            vm.dailybookCxP.subTotalDoce = parseFloat((vm.responseDebtsToPay.total / 1.12).toFixed(2));
            vm.dailybookCxP.subTotalCero = 0;
            vm.dailybookCxP.dateRecordBook = $('#pickerDateDebtsToPay').data("DateTimePicker").date().toDate().getTime();
            if (vm.retentionId == null) {
                vm.dailybookCxP.retentionNumber = 'SIN RETENCIÓN';
                vm.dailybookCxP.retentionDateCreated = 0;
                vm.dailybookCxP.retentionTotal = 0;
            } else {
                vm.dailybookCxP.retentionId = vm.retentionId;
                vm.dailybookCxP.retentionNumber = vm.retention.stringSeq;
                vm.dailybookCxP.retentionDateCreated = vm.retention.dateCreated;
                vm.dailybookCxP.retentionTotal = parseFloat(vm.retention.total);
            };
            contableService.createDailybookAsinCxP(vm.dailybookCxP).then(function(response) {
                $localStorage.user.cashier.dailyCppNumberSeq = vm.dailybookCxP.dailycxpSeq;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
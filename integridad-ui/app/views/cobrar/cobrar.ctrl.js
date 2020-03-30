'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasCobrarCtrl
 * @description
 * # CuentasCobrarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('CuentasCobrarCtrl', function(_, $localStorage, clientService, cuentaContableService, paymentService, dateService, utilSeqService,
                                              creditsbillService, $location, billService, eretentionClientService, contableService, comprobanteService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;

        vm.loading = false;
        vm.userData = $localStorage.user;
        
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
            {name:'Pagos con Tarjeta de Crédito' ,percentage:0, codigo: '332G', codigoDatil:'332G'},
            {name:'Pago al exterior tarjeta de crédito reportada por la Emisora de tarjeta de crédito, solo RECAP' ,percentage:0, codigo: '332H', codigoDatil:'332H'},
            {name:'Pago a través de Convenio de Débito (Clientes IFIs)' ,percentage:0, codigo: '332I', codigoDatil:'332I'},
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
            {name:'Pago a no residentes - Servicios técnicos, administrativos o de consultoría y regalías' ,percentage:25, codigo: '501A', codigoDatil:'501A'},
            {name:'Pago al exterior - Servicios Empresariales' ,percentage:22, codigo: '502', codigoDatil:'502'},
            {name:'Pago al exterior - Navegación Marítima y/o aérea' ,percentage:22, codigo: '503', codigoDatil:'503'},
            {name:'Pago al exterior- Dividendos distribuidos a personas naturales' ,percentage:0, codigo: '504', codigoDatil:'504'},
            {name:'Pago a no residentes - Anticipo dividendos (No domiciliada en paraísos fiscales o regímenes de menor imposición)' ,percentage:25, codigo: '504E', codigoDatil:'504E'},
            {name:'Pago a no residentes - Intereses de otros créditos externos' ,percentage:25, codigo: '505E', codigoDatil:'505E'}
        ];

        vm.creditCardList = [
            'DINNERS CLUB',
            'VISA',
            'MASTERCARD',
            'AMERICAN'
        ];

        vm.typeContabCi = 'COMP. DE INGRESO'

        function _activate() {
            vm.advertencia = false;
            vm.error = undefined;
            vm.today = new Date();
            vm.clientBill = undefined;
            vm.success = undefined;
            vm.totalTotal = undefined;
            vm.billList = undefined;
            vm.billMultipleList = undefined;
            vm.creditsbillList = undefined;
            vm.retentionClient = undefined;
            vm.retentionClientList = undefined;
            vm.clientList = [];
            vm.itemsMultiplePayments = [];
            vm.creditsBillsSelected = undefined;
            vm.multipleCobroSelected = undefined;
            vm.clientName = undefined;
            vm.typePayment = undefined;
            vm.clientIdentification = undefined;
            vm.clientId = undefined;
            vm.clientSelected = undefined;
            vm.clientCodConta = undefined;
            vm.ctaCtableBankList = undefined;
            vm.paymentList = undefined;
            vm.bankName = undefined;
            vm.modePayment = undefined;
            vm.noDocument = undefined;
            vm.noAccount = undefined;
            vm.details = undefined;
            vm.valorDocumento = undefined;
            vm.dailyCiSeq = undefined;
            vm.dailyCiStringSeq = undefined;
            vm.comprobanteCobroList = undefined;
            vm.comprobanteCobroCreated = undefined;
            vm.comprobanteCobroSeq = undefined;
            vm.comprobanteCobroStringSeq = undefined;
            vm.itemsMultipleCobros = [];
            vm.billsSelected = [];

            vm.userCashier = $localStorage.user.cashier;
            vm.usrCliId = $localStorage.user.subsidiary.userClient.id;
            vm.subCxCActive = $localStorage.user.subsidiary.cxc;
            
            if (vm.subCxCActive) {
                vm.loading = true;
                clientService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                    vm.clientList = response;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.advertencia = true;
            };
        };

        //Sección de Comprobante de Ingreso para el Asiento Automático
        function _getDailyCiSeqNumber() {
            vm.numberAddedOne = parseInt(vm.userCashier.dailyCiNumberSeq) + 1;
            vm.dailyCiSeq = vm.numberAddedOne;
            vm.dailyCiStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
        };
    
        function _initializeDailybookCi() {
            vm.dailybookCi = {
                client: vm.clientSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                clientProvName: vm.clientSelected.name,
                subTotalDoce: 0,
                iva: 0,
                subTotalCero: 0,
                total: 0,
                detailDailybookContab: []
            };
        };
        //Fin de Sección

        //Sección de Comprobante de Cobro para el Asiento Automático
        function _getComprobanteCobroSeqNumber() {
            vm.numbAddedOne = parseInt(vm.userCashier.compCobroNumberSeq) + 1;
            vm.comprobanteCobroSeq = vm.numbAddedOne;
            vm.comprobanteCobroStringSeq = utilSeqService._pad_with_zeroes(vm.numbAddedOne, 6);
        };

        function _initializeComprobanteCobro() {
            vm.comprobanteCobro = {
                client: vm.clientSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                clientName: vm.clientSelected.name,
                clientRuc: vm.clientSelected.identification,
                subTotalDoce: 0,
                iva: 0,
                total: 0,
                detailComprobanteCobro: []
            };
        };

        vm.comprobanteCobroSelected = function(comprobanteCobro) {
            vm.loading = true;
            vm.comprobanteCobroList = undefined;
            vm.activecmpC = comprobanteCobro.active;
            comprobanteService.getComprobanteCobroById(comprobanteCobro.id).then(function(response) {
                vm.comprobanteCobroCreated = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getTotalComprobanteCobroCreated = function() {
            var totalComprobanteCobro = 0;
            if (vm.comprobanteCobroCreated) {
                _.each(vm.comprobanteCobroCreated.detailComprobanteCobro, function(detail) {
                    totalComprobanteCobro = (parseFloat(totalComprobanteCobro) + parseFloat(detail.totalAbono)).toFixed(2);
                });
            };
            return totalComprobanteCobro;
        };

        vm.comprobanteCobroDeactivate = function() {
            vm.loading = true;
            var index = vm.comprobanteCobroList.indexOf(vm.deactivateComprobanteCobro);
            comprobanteService.deactivateComprobanteCobro(vm.deactivateComprobanteCobro).then(function(response) {
                var index = vm.comprobanteCobroList.indexOf(vm.deactivateComprobanteCobro);
                if (index > -1) {
                    vm.comprobanteCobroList.splice(index, 1);
                };
                vm.deactivateComprobanteCobro = undefined;
                vm.success = 'Comprobante de Cobro anulado con exito';
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        //Fin de Sección

        vm.clientConsult = function(client) {
            vm.loading = true;
            vm.success = undefined;
            vm.clientSelected = client;
            vm.clientId = client.id;
            vm.clientIdentification = client.identification;
            vm.clientName = client.name;
            vm.clientCodConta = client.codConta;
            vm.clientAddress = client.address;
            vm.clientPhone = client.cel_phone;
            vm.clientEmail = client.email;
            _initializeDailybookCi();
            _initializeComprobanteCobro();
            billService.getAllBillsByClientIdWithSaldo(vm.clientId).then(function(response) {
                vm.billList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.paymentsByClient = function(client) {
            vm.loading = true;
            vm.success = undefined;
            vm.clientSelected = client;
            vm.clientId = client.id;
            vm.clientIdentification = client.identification;
            vm.clientName = client.name;
            paymentService.getPaymentsByClientId(vm.clientId).then(function(response) {
                vm.paymentList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getComprobanteByPayment = function(payment) {
            vm.loading = true;
            comprobanteService.getComprobanteCobroByBillNumberAndUserClientAndDateCreated(payment.documentNumber, vm.usrCliId, payment.datePaymentCreated).then(function(response) {
                if(response !== ''){
                    vm.error = undefined;
                    vm.activecmpC = response.active;
                    vm.comprobanteCobroCreated = response;
                } else {
                    vm.error = 'Pago no tiene comprobante';    
                }
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            })
        };

        vm.selectCtaCtableBank = function(cuenta) {
            // vm.ctaCtableBankList = undefined;
            vm.noAccount = cuenta.number;
            vm.ctaCtableBankCode = cuenta.code;
            vm.bankName = cuenta.description;
        };

        vm.paymentDeactivate = function() {
            vm.loading = true;
            var index = vm.paymentList.indexOf(vm.deactivatePayment);
            paymentService.deactivatePayment(vm.deactivatePayment).then(function(response) {
                var index = vm.paymentList.indexOf(vm.deactivatePayment);
                if (index > -1) {
                    vm.paymentList.splice(index, 1);
                };
                vm.deactivatePayment = undefined;
                vm.success = 'Cobro anulado con exito';
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientMultiplePayment = function(client) {
            vm.loading = true;
            vm.success = undefined;
            vm.clientSelected = client;
            vm.multipleCobroSelected = vm.clientSelected;
            vm.clientName = client.name;
            vm.clientCodConta = client.codConta;
            vm.clientId = client.id;
            vm.clientIdentification = client.identification;
            vm.typePayment = 'PAC';
            vm.typePaymentName = 'PAGO CORRIENTE';
            _initializeDailybookCi();
            _initializeComprobanteCobro();
            cuentaContableService.getCuentaContableByUserClientAndBank(vm.usrCliId).then(function(response) {
                vm.ctaCtableBankList = response;
                vm.findBills();
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
            vm.valorAbono = 0;
            vm.creditsMultiBillsList = undefined; 
            vm.creditsBillsSelected = undefined
            vm.billsSelected = [];
            billService.getAllBillsByClientIdWithSaldo(vm.clientId).then(function(response) {
                vm.billMultipleList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.creditsMultiByBills = function() {
            vm.loading = true;
            vm.success = undefined;
            vm.error = undefined;

            _.each(vm.billMultipleList, function(bill){
                if(bill.selectedTotal || bill.selectedParcial){
                    vm.billsSelected.push(bill);
                    creditsbillService.getAllCreditsOfBillById(bill.id).then(function(response) {
                        if(bill.selectedTotal){
                            _.each(response, function(cuota) {
                                vm.itemCobroBill = {};
                                var itemBillsMulti = {
                                    credit_bill: cuota,
                                    bill_number: bill.stringSeq,
                                    bill_total: (bill.total).toFixed(2),
                                    bill_pending: parseFloat(bill.saldo),
                                    bill_abono: cuota.valor.toFixed(2),
                                    tipo: 'PAGO TOTAL DE CUOTA',
                                };
                                vm.itemsMultiplePayments.push(itemBillsMulti);
                                vm.itemCobroBill.totalAbono = cuota.valor;
                                vm.itemCobroBill.dateBill = bill.dateCreated;
                                vm.itemCobroBill.billNumber = bill.stringSeq;
                                vm.itemsMultipleCobros.push(vm.itemCobroBill);
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

        vm.multipleAbono = function() {
            vm.loading = true;
            _.each(vm.billsSelected, function(bill){
                if(bill.selectedParcial){
                    _.each(bill.creditsMultiBillsList, function(cuota) {
                        if(cuota.valorAbono !== undefined){
                            vm.itemCobroBill = {};
                            var itemBillsMulti = {
                                credit_bill: cuota,
                                bill_number: bill.stringSeq,
                                bill_total: (bill.total).toFixed(2),
                                bill_pending: parseFloat(bill.saldo),
                                bill_abono: parseFloat(cuota.valorAbono).toFixed(2),
                                tipo: parseFloat(cuota.valorAbono) < parseFloat(cuota.valor).toFixed(2) ? 'ABONO PARCIAL DE CUOTA' :  'PAGO TOTAL DE CUOTA',
                            };
                            vm.itemsMultiplePayments.push(itemBillsMulti);
                            vm.itemCobroBill.totalAbono = parseFloat(cuota.valorAbono).toFixed(2);
                            vm.itemCobroBill.dateBill = bill.dateCreated;
                            vm.itemCobroBill.billNumber = bill.stringSeq;
                            vm.itemsMultipleCobros.push(vm.itemCobroBill);
                        }
                    });
                }
            });

            vm.loading = false;
            $('#modalFindBills').modal('hide');
        };

        vm.eliminaAbono = function(index) {
            vm.itemsMultiplePayments.splice(index, 1);
            vm.itemsMultipleCobros.splice(index, 1);
        };

        vm.getTotalMultiAbonos = function() {
            var totalMultiAbono = 0;
            _.each(vm.itemsMultiplePayments, function(detail) {
                totalMultiAbono = (parseFloat(totalMultiAbono) + parseFloat(detail.bill_abono)).toFixed(2);
            });
            vm.valorDocumento = totalMultiAbono;
            return totalMultiAbono;
        };

        vm.getBillNumberPayed = function() {
            var billNumberPayed = '';
            _.each(vm.billsSelected, function(bill) {
                billNumberPayed = billNumberPayed + ' ' + bill.stringSeq;
            });
            return billNumberPayed;
        };

        vm.pagoMultiAbono = function() {
            vm.loading = true;
            vm.billsNumberPayed = vm.getBillNumberPayed();
            vm.paymentCreatedDate = new Date().getTime();
            var paymentsListToSave = [];
            paymentService.getPaymentsByUserClientIdWithBankAndNroDocument(vm.usrCliId, vm.bankName, vm.noDocument).then(function(response) {
                if (response.length === 0) {
                    _.each(vm.itemsMultiplePayments, function(detail) { 
                        vm.payment = {
                            credits: detail.credit_bill,
                        };
                        vm.payment.typePayment = vm.typePayment;
                        vm.payment.datePayment = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
                        vm.payment.documentNumber = detail.bill_number;
                        vm.payment.modePayment = vm.modePayment;
                        vm.payment.detail = detail.tipo;
                        vm.payment.valorAbono = detail.bill_abono;
                        vm.payment.valorNotac = 0;
                        vm.payment.valorReten = 0;
                        vm.payment.noAccount = vm.noAccount;
                        vm.payment.noDocument = vm.noDocument;
                        vm.payment.ctaCtableBanco = vm.ctaCtableBankCode;
                        vm.payment.ctaCtableClient = vm.clientCodConta;
                        vm.payment.clientName = vm.clientName;
                        vm.payment.banco = vm.bankName;
                        vm.payment.datePaymentCreated = vm.paymentCreatedDate;
                        paymentsListToSave.push(vm.payment)
                        
                    });
                    paymentService.createPaymentList(paymentsListToSave).then(function(response) {
                        vm.loading = false;
                        vm.success = 'Abono realizado con exito';
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                    _asientoComprobanteMultipleCobro();
                    _asientoComprobanteMultipleIngreso();
                    _activate();
                } else {
                    vm.error = 'El Nro. de Cheque, Transferencia y/o Depósito Ya Existe y no puede repetirse';
                    vm.loading = false;
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _asientoComprobanteMultipleCobro() {
            _getComprobanteCobroSeqNumber();
            _.each(vm.itemsMultipleCobros, function (item) {
                item.banco = vm.bankName;
                item.cuenta = vm.noAccount;
                item.numCheque = vm.noDocument;
                item.tipoAbono = vm.modePayment;
            });

            vm.comprobanteCobro.billNumber = vm.billsNumberPayed;
            vm.comprobanteCobro.dateComprobante = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            vm.comprobanteCobro.comprobanteSeq = vm.comprobanteCobroSeq;
            vm.comprobanteCobro.comprobanteStringSeq = vm.comprobanteCobroStringSeq;
            vm.comprobanteCobro.comprobanteConcep = 'Cancela Facts. ' + vm.billsNumberPayed;
            vm.comprobanteCobro.comprobanteEstado = 'PROCESADO';
            vm.comprobanteCobro.total = vm.valorDocumento;
            vm.comprobanteCobro.subTotalDoce = parseFloat((vm.valorDocumento / 1.12).toFixed(2));
            vm.comprobanteCobro.iva = parseFloat((vm.valorDocumento * 0.12).toFixed(2));
            vm.comprobanteCobro.detailComprobanteCobro = [];
            vm.comprobanteCobro.detailComprobanteCobro = vm.itemsMultipleCobros;
            vm.comprobanteCobro.dateComprobanteCreated = vm.paymentCreatedDate;
            comprobanteService.createComprobanteCobro(vm.comprobanteCobro).then(function(response) {
                vm.userCashier.compCobroNumberSeq = vm.comprobanteCobro.comprobanteSeq;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _asientoComprobanteMultipleIngreso() {
            _getDailyCiSeqNumber();
            vm.selectedTypeBook = '3';
            vm.generalDetailCi_1 = vm.clientName + ' Cancela Facts. ' + vm.billsNumberPayed;
            vm.itema = {
                typeContab: vm.typeContabCi,
                codeConta: vm.clientCodConta,
                descrip: 'CLIENTES NO RELACIONADOS',
                tipo: 'CREDITO (C)',
                baseImponible: parseFloat(vm.valorDocumento),
                name: vm.generalDetailCi_1,
                haber: parseFloat(vm.valorDocumento)
            };
            vm.itema.numCheque = vm.noDocument;
            vm.itema.dailybookNumber = vm.dailyCiStringSeq;
            vm.itema.userClientId = vm.usrCliId;
            vm.itema.dateDetailDailybook = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            vm.dailybookCi.detailDailybookContab.push(vm.itema);
            vm.generalDetailCi_2 = 'Cobro de Facts. ' + vm.billsNumberPayed + ' con ' + vm.modePayment + ' Nro. ' + vm.noDocument + ' en ' + vm.bankName + ', a: ' + vm.clientName;
            vm.itemb = {
                typeContab: vm.typeContabCi,
                codeConta: vm.ctaCtableBankCode,
                descrip: vm.bankName,
                tipo: 'DEBITO (D)',
                baseImponible: parseFloat(vm.valorDocumento),
                name: vm.generalDetailCi_2,
                deber: parseFloat(vm.valorDocumento)
            };
            vm.itemb.numCheque = '--';
            vm.itemb.dailybookNumber = vm.dailyCiStringSeq;
            vm.itemb.userClientId = vm.usrCliId;
            vm.itemb.dateDetailDailybook = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            vm.dailybookCi.detailDailybookContab.push(vm.itemb);
            vm.dailybookCi.codeTypeContab = vm.selectedTypeBook;
            vm.dailybookCi.nameBank = vm.bankName;
            vm.dailybookCi.billNumber = vm.billsNumberPayed;
            vm.dailybookCi.numCheque = vm.noDocument;
            vm.dailybookCi.typeContab = vm.typeContabCi;
            vm.dailybookCi.dailyCiSeq = vm.dailyCiSeq;
            vm.dailybookCi.dailyCiStringSeq = vm.dailyCiStringSeq;
            vm.dailybookCi.dailyCiStringUserSeq = 'PAGO GENERADO ' + vm.dailyCiStringSeq;
            vm.dailybookCi.clientProvName = vm.clientName;
            vm.dailybookCi.generalDetail = vm.generalDetailCi_1;
            vm.dailybookCi.total = vm.valorDocumento;
            vm.dailybookCi.iva = parseFloat((vm.valorDocumento * 0.12).toFixed(2));
            vm.dailybookCi.subTotalDoce = parseFloat((vm.valorDocumento / 1.12).toFixed(2));
            vm.dailybookCi.subTotalCero = 0;
            vm.dailybookCi.dateRecordBook = $('#pickerDateOfMultiplePayment').data("DateTimePicker").date().toDate().getTime();
            
            contableService.createDailybookAsinCi(vm.dailybookCi).then(function(response) {
                vm.userCashier.dailyCiNumberSeq = vm.dailybookCi.dailyCiSeq;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

    //Inicio de Creación de Retenciones...
        vm.createRetentionClient = function(bill) {
            var today = new Date();
            vm.success = undefined;
            vm.error = undefined;
            vm.billNumber = bill.stringSeq;
            vm.BillId = bill.id;
            eretentionClientService.getRetentionClientByBillIdAndDocumentNumber(vm.BillId, vm.billNumber).then(function(response) {
                if (response.length === 0) {
                    vm.creditValue = bill.total;
                    vm.creditValueSubtotal = bill.subTotal;
                    vm.creditValueIva = bill.iva;
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
                } else {
                    vm.error = 'Ya existe una Retención ingresada para esta Factura';
                    vm.loading = false;
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.consultRetentionClient = function(bill) {
            vm.loading = true;
            vm.error = undefined;
            vm.billNumber = bill.stringSeq;
            eretentionClientService.getRetentionClientByBillId(bill.id).then(function(response) {
                vm.retentionClientList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
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
                tipo_documento_sustento: vm.docType,
                base_imponible: vm.creditValueSubtotal
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

        vm.getTotalRetentionClient = function() {
            var totalRetorno = 0;
            if (vm.retentionClient) {
                _.each(vm.retentionClient.items, function(detail) {
                    totalRetorno = (parseFloat(totalRetorno) + parseFloat(detail.valor_retenido)).toFixed(2);
                });
            };
            vm.totalTotal = totalRetorno;
            return totalRetorno;
        };

        vm.previewRetentionClient = function() {
            vm.loading = true;
            vm.retentionClient.documentDate = $('#pickerDateRetention').data("DateTimePicker").date().toDate().getTime();
            vm.retentionClient.ejercicioFiscal = vm.retentionClient.ejercicio;
            vm.retentionClient.documentNumber = vm.billNumber;
            vm.retentionClient.retentionNumber = vm.retentionClient.numero;
            vm.retentionClient.BillId = vm.BillId;
            vm.loading = false;
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
            vm.retentionClient.total = vm.totalTotal;
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
            eretentionClientService.createRetentionClient(retentionClient).then(function(respRetentionClient) {
                vm.totalRetention = 0;
                vm.retentionClient = respRetentionClient;
                _.each(vm.retentionClient.detailRetentionClient, function(detail) {
                    vm.totalRetention = parseFloat(vm.totalRetention + detail.total);
                });
                vm.success = 'Retención creada';
                vm.loading = false;
                _activate();
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.retentionClientSelected = function(retentionClient) {
            vm.loading = true;
            vm.error = undefined;
            eretentionClientService.getRetentionClientById(retentionClient.id).then(function(response) {
                vm.retentionClientToShow = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getTotalRetentionClientToShow = function() {
            var totalRetorno = 0;
            if (vm.retentionClientToShow) {
                _.each(vm.retentionClientToShow.detailRetentionClient, function(detail) {
                    totalRetorno = (parseFloat(totalRetorno) + parseFloat(detail.total)).toFixed(2);
                });
            };
            return totalRetorno;
        };

        vm.retentionClientDeactivate = function() {
            vm.loading = true;
            vm.error = undefined;
            var index = vm.retentionClientList.indexOf(vm.deactivateRetentionClient);
            eretentionClientService.deactivateRetentionClient(vm.deactivateRetentionClient).then(function(response) {
                var index = vm.retentionClientList.indexOf(vm.deactivateRetentionClient);
                if (index > -1) {
                    vm.retentionClientList.splice(index, 1);
                };
                vm.deactivateRetentionClient = undefined;
                vm.success = 'Registro eliminado con exito';
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };
    // Fin de Creación de Retenciones....

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
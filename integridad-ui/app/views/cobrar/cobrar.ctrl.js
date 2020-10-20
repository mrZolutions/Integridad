'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasCobrarCtrl
 * @description
 * # CuentasCobrarCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('CuentasCobrarCtrl', function(_, holderService, clientService, cuentaContableService, paymentService, dateService, utilSeqService,
                                              creditsbillService, $location, billService, billOfflineService, eretentionClientService, contableService, comprobanteService,
                                              optionConfigCuentasService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.cobrarOption = 'COBRAR';
        vm.optionsList = undefined;

        vm.loading = false;
        vm.userData = holderService.get();
        
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

            vm.userCashier = vm.userData.cashier;
            vm.usrCliId = vm.userData.subsidiary.userClient.id;
            vm.subCxCActive = vm.userData.subsidiary.cxc;

            optionConfigCuentasService.getOptionConfigCuentas().then(function(response) {
                vm.optionsList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });

            switch(true) {
                case $location.path().includes('/retentionClient'): vm.cobrarOption = 'RETEN'; break;
            }
            
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
                userIntegridad: vm.userData,
                subsidiary: vm.userData.subsidiary,
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
                userIntegridad: vm.userData,
                subsidiary: vm.userData.subsidiary,
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

        vm.getSaldo = function(saldoString){
            var saldo = parseFloat(saldoString).toFixed(4);
            return isNaN(saldo) ? '' : saldo;
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


            billOfflineService.getAllBillsOfflineByClientIdWithSaldo(vm.clientId).then(function(response) {
                if(vm.billMultipleList){
                    vm.billMultipleList = vm.billMultipleList.concat(response);
                } else {
                    vm.billMultipleList = response;
                }
                
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
                        console.log('-*-*-*-*-*-*: ',response)
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
            } else if(_.findWhere(vm.billsSelected, {selectedParcial: true}) === undefined){
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
            vm.itemb.numCheque = vm.noDocument;
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
            var typeSelected = vm.retentionClient.typeRetention === '2' ? 'IVA' : 'FUENTE';
            vm.tablePercentage = _.filter(vm.optionsList, function(opt){ return opt.type === typeSelected; });

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

        vm.disableMultipleCobro = function(){
            if(vm.noDocument === undefined || '' === vm.noDocument.trim()){
                return true;
            }
            if(vm.modePayment === undefined){
                return true;
            }
            if(vm.getTotalMultiAbonos() != vm.valorDocumento){
                return true;
            }
        
            return false;
        }

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
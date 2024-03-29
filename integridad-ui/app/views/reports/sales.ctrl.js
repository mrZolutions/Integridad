'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ReportSalesCtrl
 * @description
 * # ReportSalesCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('ReportSalesCtrl', function(_, holderService, creditsbillService, billService, eretentionService, eretentionClientService,
                                            cellarService, creditNoteCellarService, billOfflineService, paymentService, paymentDebtsService,
                                            creditsDebtsService, $location, debtsToPayService, consumptionService, creditNoteService,
                                            productService, clientService, providerService, contableService) {
        var vm = this;
        var today = new Date();
        $('#pickerBillDateOne').data("DateTimePicker").date(today);
        $('#pickerBillDateTwo').data("DateTimePicker").date(today);
        // undefined = retention; true = products; false = sales
        vm.isProductReportList = undefined;
        vm.reportList = undefined;
        vm.user = holderService.get()
        vm.userClientId = vm.user.subsidiary.userClient.id;
        vm.valParra = '2455e4bf-68f3-4071-b5c9-833d62512b00';
        vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';
        vm.mrZolutions = '4907601b-6e54-4675-80a8-ab6503e1dfeb';
        vm.pineda = '6e663299-d61c-44de-b42c-a3d6595b46d2';
        vm.catedral = '1e2049c3-a3bc-4231-a0de-dded8020dc1b';
        vm.reportName = 'Report';

        vm.getReportProducts = function() {
            vm.reportName = 'Productos Vendidos';
            vm.isProductReportList = '1';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;

            billService.getActivesByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportSales = function() {
            vm.reportName = 'Ventas';
            vm.isProductReportList = '2';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;

            billService.getAllByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportRetention = function() {
            vm.reportName = 'Retencion en Compras';
            vm.isProductReportList = '3';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            eretentionService.getAllByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getCreditsPendingReport = function() {
            vm.reportName = 'Cuentas Pendientes Por Cobrar';
            vm.isProductReportList = '4';
            vm.reportList = undefined;
            vm.loading = true;
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            creditsbillService.getAllCreditsOfBillByUserClientIdResume(vm.userClientId, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportRetentionClient = function() {
            vm.reportName = 'Retencion Ventas';
            vm.isProductReportList = '5';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            eretentionClientService.getRetentionClientByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getCCResumenReport = function() {
            vm.reportName = 'Abonos y Otros';
            vm.isProductReportList = '6';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            paymentService.getAllPaymentsByUserClientIdAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getCreditsDebtsPendingReport = function() {
            vm.reportName = 'Cuentas Pendientes Por Pagar';
            vm.isProductReportList = '7';
            vm.reportList = undefined;
            vm.loading = true;
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            creditsDebtsService.getAllCreditsDebtsPendingOfDebtsToPayByUserClientId(vm.userClientId, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportDebts = function() {
            vm.reportName = 'Cuentas Por Pagar de Compras';
            vm.isProductReportList = '8';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            debtsToPayService.getDebtsToPayByUserClientIdAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportCierreCaja = function() {
            vm.reportName = 'Cierre de Caja';
            vm.isProductReportList = '9';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            billService.getForCashClosureReportAndDate(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportCsmProducts = function() {
            vm.reportName = 'Bodega Egresos';
            vm.isProductReportList = '10';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            consumptionService.getActivesByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getCPResumenPaymentDebtsReport = function() {
            vm.reportName = 'Abonos y Otros Por Pagar';
            vm.isProductReportList = '11';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            paymentDebtsService.getPaymentDebtsByUserClientIdAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getCellarEntryReport = function() {
            vm.reportName = 'Bodega Ingresos';
            vm.isProductReportList = '12';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            cellarService.getByUserClientIdAndDatesActives(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportCreditNote = function() {
            vm.reportName = 'Notas de Credito Ventas';
            vm.isProductReportList = '13';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            creditNoteService.getCreditNotesByUserClientIdAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportCreditNoteCompras = function() {
            vm.reportName = 'Notas de Credito Compras';
            vm.isProductReportList = '14';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            creditNoteCellarService.getCreditNotesCellarByUserClientIdAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportExistency = function() {
            vm.reportName = 'Inventario';
            vm.isProductReportList = '15';
            vm.reportList = undefined;
            vm.loading = true;
            
            productService.getProductsForExistencyReport(vm.userClientId).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportExistencyV2 = function() {
            vm.reportName = 'Inventario';
            vm.isProductReportList = '22';
            vm.reportList = undefined;
            vm.loading = true;
            
            productService.getProductsForExistencyReportV2(vm.userClientId).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportSalesOffline = function() {
            vm.reportName = 'Ventas';
            vm.isProductReportList = '16';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            
            billOfflineService.getBillsOfflineByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportProductsOffline = function() {
            vm.reportName = 'Productos Vendidos';
            vm.isProductReportList = '17';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;

            billOfflineService.getProductsSoldByUserClientAndDates(vm.userClientId, dateOne, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportExistencyCat = function() {
            vm.reportName = 'Inventario';
            vm.isProductReportList = '18';
            vm.reportList = undefined;
            vm.loading = true;
            
            productService.getProductsForExistencyCatReport(vm.userClientId).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportClients = function() {
            vm.reportName = 'Reporte Clientes';
            vm.isProductReportList = '19';
            vm.reportList = undefined;
            vm.loading = true;
            clientService.getClientsReport(vm.userClientId).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getReportProviders = function() {
            vm.reportName = 'Reporte Proveedores';
            vm.isProductReportList = '20';
            vm.reportList = undefined;
            vm.loading = true;
            providerService.getProvidersReport(vm.userClientId).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        }

        vm.getReportDiarios = function() {
            vm.error = undefined;
            vm.reportName = 'Reporte de diarios';
            vm.isProductReportList = '21';
            vm.reportList = undefined;
            vm.loading = true;
            var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
            var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
            if(dateTwo - dateOne > 2764800000){
                vm.loading = false;
                vm.error = "Reporte de máximo 31 días"
            } else {
                contableService.getDailysTotal(vm.userClientId, dateOne, dateTwo )
                    .then(function (response) {
                        vm.reportList = response;
                        vm.loading = false;
                    }).catch(function (error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
            }
        }

        vm.getTotal = function(total, subTotal) {
            var total = total - subTotal;
            return (total).toFixed(2);
        };

        vm.exportExcel = function() {
            var dataReport = [];
            switch (vm.isProductReportList) {
                case '1':
                    _.each(vm.reportList, function(bill) {
                        var data = {
                            TIPO: bill.type,
                            NUMERO: bill.billSeqString,
                            CODIGO: bill.code,
                            DESCRIPCION: bill.description,
                            CANTIDAD: bill.quantity,
                            VAL_UNITARIO: bill.valUnit,
                            SUBTOTAL: parseFloat(bill.subTotal.toFixed(2)),
                            DESCUENTO: parseFloat(bill.discount.toFixed(2)),
                            IVA: parseFloat(bill.iva.toFixed(2)),
                            TOTAL: parseFloat(bill.total.toFixed(2))
                        };
              
                        dataReport.push(data);
                    });
                break;
                case '2':
                    _.each(vm.reportList, function(bill) {
                        var data = {
                            FECHA: bill.date,
                            CODIGO_CLIENTE: bill.clientCode,
                            CLIENTE: bill.clientName,
                            RUC_CI: bill.ruc,
                            NUMERO_FACTURA: bill.billNumber,
                            NUMERO_EN_SRI: bill.billSeq,
                            NUMERO_AUTORIZACION: bill.authorizationNumber,
                            ESTADO: bill.status,
                            OTI: bill.oti,
                            BASE_DOCE: bill.status === 'ACTIVA' ? parseFloat(bill.baseTaxes.toFixed(2)) : parseFloat(0),
                            DESCUENTO: bill.status === 'ACTIVA' ? parseFloat(bill.discount.toFixed(2)) : parseFloat(0),
                            BASE_CERO: bill.status === 'ACTIVA' ? parseFloat(bill.baseNoTaxes.toFixed(2)) : parseFloat(0),
                            IVA: bill.status === 'ACTIVA' ? parseFloat(bill.iva.toFixed(2)) : parseFloat(0),
                            TOTAL: bill.status === 'ACTIVA' ? parseFloat(bill.total.toFixed(2)) : parseFloat(0),
                            FORMA_DE_PAGO: bill.paymentMode,
                            FECHA_VENCIMIENTO: bill.endDate,
                            CAJA: bill.cashier,
                            BODEGA: bill.warehouse,
                            SUCURSAL: bill.subsidiary,
                            USUARIO: bill.userName
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '3':
                    _.each(vm.reportList, function(retention) {
                        var data = {
                            FECHA_FACT: retention.documentDate,
                            NUM_FACT: retention.documentNumber,
                            PROVEEDOR: retention.providerName,
                            RUC: retention.ruc,
                            FECHA_RET: retention.dateCreated,
                            NUM_RET: retention.retentionNumber,
                            NUM_AUTOR: retention.authorizationNumber,
                            EJER_FISCAL: retention.ejercicioFiscal,
                            NUM_CXP: retention.debtsSeq,
                            ESTADO: retention.status,
                            COD_RET_FTE: retention.codigoRetentionFuente,
                            BASE_FTE: parseFloat((retention.baseFuente).toFixed(2)),
                            PORCENT_FTE: parseFloat((retention.porcenFuente).toFixed(2)),
                            RETEN_FTE: parseFloat((retention.subTotalFuente).toFixed(2)),
                            COD_RET_IVA: retention.codigoRetentionIva,
                            BASE_IVA: parseFloat((retention.baseIva).toFixed(2)),
                            PORCENT_IVA: parseFloat((retention.porcenIva).toFixed(2)),
                            RETEN_IVA: parseFloat((retention.subTotalIva).toFixed(2)),
                            TOTAL_RETENIDO: parseFloat((retention.total).toFixed(2)),
                            SUCURSAL: retention.subsidiary,
                            USUARIO: retention.userName
                        };
                        
                        dataReport.push(data);
                    });
                break;
                case '4':
                    _.each(vm.reportList, function(creditsreport) {
                        var data = {
                            RUC_CI: creditsreport.identification,
                            CLIENTE: creditsreport.clientName,
                            FACTURA: creditsreport.billNumber,
                            FECHA_VENTA: creditsreport.fechVenta,
                            DIAS_CREDIT: creditsreport.diasCredit,
                            DIAS_VENCE: creditsreport.diasVencim,
                            VENTA: parseFloat(creditsreport.costo.toFixed(2)),
                            ABONO: parseFloat(creditsreport.valorAbono.toFixed(2)),
                            RETEN: parseFloat(creditsreport.valorReten.toFixed(2)),
                            N_C: parseFloat(creditsreport.valorNotac.toFixed(2)),
                            SALDO: parseFloat(creditsreport.saldo.toFixed(2)),
                        };      
                
                        dataReport.push(data);
                    });
                break;
                case '5':
                    _.each(vm.reportList, function(retention) {
                        var data = {
                            FECHA: retention.date,
                            FECHA_FACT: retention.documentDate,
                            COD_CLIENTE: retention.clientCode,
                            CLIENTE: retention.clientName,
                            RUC_CI: retention.identification,
                            NUM_RETEN: retention.retentionNumber,
                            NUM_FACT: retention.documentNumber,
                            EJER_FISCAL: retention.ejercicioFiscal,
                            ESTADO: retention.status,
                            COD_RET_FTE: retention.codigoRetentionFuente,
                            BASE_FUENTE: parseFloat((retention.baseFuente).toFixed(2)),
                            PORCENT_FTE: parseFloat((retention.porcenFuente).toFixed(2)),
                            RETEN_FTE: parseFloat((retention.subTotalFuente).toFixed(2)),
                            COD_RET_IVA: retention.codigoRetentionIva,
                            BASE_IVA: parseFloat((retention.baseIva).toFixed(2)),
                            PORCENT_IVA: parseFloat((retention.porcenIva).toFixed(2)),
                            RETEN_IVA: parseFloat((retention.subTotalIva).toFixed(2)),
                            TOTAL_RETENIDO: parseFloat((retention.total).toFixed(2))
                        };
                        
                        dataReport.push(data);
                    });
                break;
                case '6':
                    _.each(vm.reportList, function(ccresumenreport) {
                        var data = {
                            RUC_CI: ccresumenreport.identification,
                            CLIENTE: ccresumenreport.nameClient,
                            FACTURA: ccresumenreport.billNumber,
                            VENTA: ccresumenreport.billTotal,
                            TIPO_TRANSAC: ccresumenreport.tipTransac,
                            MODO_COBRO: ccresumenreport.formPago,
                            NUME_CHQ: ccresumenreport.numCheque,
                            FECHA_COBRO: ccresumenreport.fechPago,
                            VALOR_ABONO: parseFloat(ccresumenreport.valorAbono.toFixed(2)),
                            VALOR_RETEN: parseFloat(ccresumenreport.valorReten.toFixed(2)),
                            VALOR_NC: parseFloat(ccresumenreport.valorNotac.toFixed(2))
                        };
              
                        dataReport.push(data);
                    });
                break;
                case '7':
                    _.each(vm.reportList, function(creditsdebtsreport) {
                        var data = {
                            RUC: creditsdebtsreport.ruc,
                            PROVEEDOR: creditsdebtsreport.providerName,
                            FACTURA: creditsdebtsreport.billNumber,
                            FECHA_VENTA: creditsdebtsreport.fechVenta,
                            FECHA_VENCE: creditsdebtsreport.fechVence,
                            DIAS_CREDIT: creditsdebtsreport.diasCredit,
                            DIAS_VENCE: creditsdebtsreport.diasVencim,
                            VENTA: parseFloat(creditsdebtsreport.total.toFixed(2)),
                            ABONO: parseFloat(creditsdebtsreport.valorAbono.toFixed(2)),
                            RETEN: parseFloat(creditsdebtsreport.valorReten.toFixed(2)),
                            SALDO: parseFloat(creditsdebtsreport.saldo.toFixed(2))
                        };      
                
                        dataReport.push(data);
                    });
                break;
                case '8':
                    _.each(vm.reportList, function(debt) {
                        var data = {
                            FECHA: debt.date,
                            CODIGO_PROVEEDOR: debt.providerCode,
                            PROVEEDOR: debt.providerName,
                            RUC: debt.ruc,
                            NUMERO_CUENTA: debt.debtNumber,
                            NUMERO_FACTURA: debt.billNumber,
                            NUMERO_DIARIO: debt.dailyNumber,
                            NUMERO_AUTORIZACION: debt.authorizationNumber,
                            COMP_COMPRA: debt.buyTypeVoucher,
                            COD_SUSTENTO: debt.purchaseType,
                            ESTADO: debt.status,
                            DESCRIPCION: debt.observacion,
                            NRO_RETEN: debt.retentionNumber,
                            BASE_DOCE: debt.status === 'ACTIVA' ? parseFloat(debt.subTotalDoce.toFixed(2)) : parseFloat(0),
                            IVA: debt.status === 'ACTIVA' ? parseFloat(debt.iva.toFixed(2)) : parseFloat(0),
                            BASE_CERO: debt.status === 'ACTIVA' ? parseFloat(debt.subTotalCero.toFixed(2)) : parseFloat(0),
                            TOTAL: debt.status === 'ACTIVA' ? parseFloat(debt.total.toFixed(2)) : parseFloat(0),
                            FECHA_VENCIMIENTO: debt.endDate,
                            CAJA: debt.cashier,
                            SUCURSAL: debt.subsidiary,
                            USUARIO: debt.userName
                        };
              
                        dataReport.push(data);
                    });
                break;
                case '9':
                    _.each(vm.reportList, function(closure) {
                        var data = {
                            FECHA: closure.billDateCreated,
                            NUMERO_FACTURA: closure.billStringSeq,
                            BASE_DOCE: closure.billBaseTaxes,
                            BASE_CERO: closure.billBaseNoTaxes,
                            SUBTOTAL: closure.billSubTotal,
                            IVA: closure.billIva,
                            TOTAL: closure.billTotal,
                            FORMA_PAGO: closure.pagoMedio,
                            TARJETA: closure.pagoCardBrand,
                            No_LOTE: closure.pagoNumeroLote,
                            CHEQUE_CUENTA: closure.pagoChequeAccount,
                            CHEQUE_BANCO: closure.pagoChequeBank,
                            CHEQUE_NUMERO: closure.pagoChequeNumber
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '10':
                    _.each(vm.reportList, function(consp) {
                        var data = {
                            TIPO: consp.type,
                            CONSUMO_NUM: consp.csmNumberSeq,
                            CODIGO: consp.code,
                            DESCRIPCION: consp.description,
                            CANTIDAD: consp.quantity,
                            VAL_UNITARIO: consp.valUnit,
                            SUBTOTAL: parseFloat(consp.subTotal.toFixed(2)),
                            IVA: parseFloat(consp.iva.toFixed(2)),
                            TOTAL: parseFloat(consp.total.toFixed(2)),
                            CLIENTE: consp.clientName
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '11':
                    _.each(vm.reportList, function(ccresumenpdreport) {
                        var data = {
                            RUC: ccresumenpdreport.ruc,
                            PROVEEDOR: ccresumenpdreport.nameProv,
                            NRO_CXP: ccresumenpdreport.debtsNumber,
                            NRO_FACTURA: ccresumenpdreport.billNumber,
                            VALOR_COMPRA: ccresumenpdreport.debtsTotal,
                            TIPO_TRANSAC: ccresumenpdreport.tipTransac,
                            MODO_PAGO: ccresumenpdreport.formPago,
                            NUME_CHQ: ccresumenpdreport.numCheque,
                            FECHA_PAGO: ccresumenpdreport.fechPago,
                            VALOR_ABONO: parseFloat(ccresumenpdreport.valorAbono.toFixed(2)),
                            VALOR_RETEN: parseFloat(ccresumenpdreport.valorReten.toFixed(2))
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '12':
                    _.each(vm.reportList, function(cellarReport) {
                        var data = {
                            FECHA_INGRESO: cellarReport.fechaIngreso,
                            NRO_INGRESO: cellarReport.whNumberSeq,
                            PROVEEDOR: cellarReport.razonSocial,
                            FECHA_FACTURA: cellarReport.fechaBill,
                            NRO_FACTURA: cellarReport.billNumber,
                            PRODUCTO: cellarReport.prodName,
                            CANTIDAD: cellarReport.prodQuantity,
                            COST_UNIT: parseFloat(cellarReport.prodCostEach.toFixed(4)),
                            IVA: parseFloat(cellarReport.prodIva.toFixed(4)),
                            TOTAL: parseFloat(cellarReport.prodTotal.toFixed(2))
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '13':
                    _.each(vm.reportList, function(creditNote) {
                        var data = {
                            NOT_CRED: creditNote.stringSeq,
                            FECHA: creditNote.dateCreated,
                            NRO_FACTURA: creditNote.documentStringSeq,
                            ESTADO: creditNote.status,
                            BASE_DOCE: creditNote.status === 'ACTIVA' ? parseFloat(creditNote.baseDoce.toFixed(2)) : parseFloat(0),
                            BASE_CERO: creditNote.status === 'ACTIVA' ? parseFloat(creditNote.baseCero.toFixed(2)) : parseFloat(0),
                            IVA: creditNote.status === 'ACTIVA' ? parseFloat(creditNote.iva.toFixed(2)) : parseFloat(0),
                            TOTAL: creditNote.status === 'ACTIVA' ? parseFloat(creditNote.total.toFixed(2)) : parseFloat(0),
                            MOTIVO: creditNote.motivo
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '14':
                    _.each(vm.reportList, function(creditNoteComp) {
                        var data = {
                            NOT_CRED: creditNoteComp.stringSeq,
                            FECHA: creditNoteComp.dateCreated,
                            NRO_FACTURA: creditNoteComp.documentStringSeq,
                            ESTADO: creditNoteComp.status,
                            BASE_DOCE: creditNoteComp.status === 'ACTIVA' ? parseFloat(creditNoteComp.baseDoce.toFixed(2)) : parseFloat(0),
                            BASE_CERO: creditNoteComp.status === 'ACTIVA' ? parseFloat(creditNoteComp.baseCero.toFixed(2)) : parseFloat(0),
                            IVA: creditNoteComp.status === 'ACTIVA' ? parseFloat(creditNoteComp.iva.toFixed(2)) : parseFloat(0),
                            TOTAL: creditNoteComp.status === 'ACTIVA' ? parseFloat(creditNoteComp.total.toFixed(2)) : parseFloat(0),
                            MOTIVO: creditNoteComp.motivo
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '15':
                    _.each(vm.reportList, function(existency) {
                        var data = {
                            CODIGO: existency.code,
                            NOMBRE: existency.name,
                            TIPO: existency.typeProduct,
                            COSTO_REAL: parseFloat(existency.costReal.toFixed(2)),
                            COSTO_EFEC: parseFloat(existency.costCash.toFixed(2)),
                            COSTO_TARJ: parseFloat(existency.costCard.toFixed(2)),
                            COSTO_CRED: parseFloat(existency.costCredit.toFixed(2)),
                            COSTO_MAYOR: parseFloat(existency.costMajor.toFixed(2)),
                            MINIMO: parseInt(existency.maxMin),
                            CANTIDAD: parseInt(existency.quantity),
                            GRUPO: existency.groupo,
                            SUB_GRUPO: existency.subGroup,
                            MARCA: existency.marca,
                            LINEA: existency.linea,
                            OBSERVACION: existency.observacion
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '16':
                    _.each(vm.reportList, function(billOff) {
                        var data = {
                            FECHA: billOff.date,
                            CODIGO_CLIENTE: billOff.clientCode,
                            CLIENTE: billOff.clientName,
                            RUC_CI: billOff.ruc,
                            NUMERO_FACTURA: billOff.billNumber,
                            ESTADO: billOff.status,
                            BASE_DOCE: billOff.status === 'ACTIVA' ? parseFloat(billOff.baseTaxes.toFixed(2)) : parseFloat(0),
                            DESCUENTO: billOff.status === 'ACTIVA' ? parseFloat(billOff.discount.toFixed(2)) : parseFloat(0),
                            BASE_CERO: billOff.status === 'ACTIVA' ? parseFloat(billOff.baseNoTaxes.toFixed(2)) : parseFloat(0),
                            IVA: billOff.status === 'ACTIVA' ? parseFloat(billOff.iva.toFixed(2)) : parseFloat(0),
                            TOTAL: billOff.status === 'ACTIVA' ? parseFloat(billOff.total.toFixed(2)) : parseFloat(0),
                            CAJA: billOff.cashier,
                            SUCURSAL: billOff.subsidiary,
                            USUARIO: billOff.userName
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '17':
                    _.each(vm.reportList, function(billOff) {
                        var data = {
                            NUMERO: billOff.billOffSeqString,
                            CODIGO: billOff.code,
                            DESCRIPCION: billOff.description,
                            CANTIDAD: billOff.quantity,
                            VAL_UNITARIO: billOff.valUnit,
                            SUBTOTAL: parseFloat(billOff.subTotal.toFixed(2)),
                            DESCUENTO: parseFloat(billOff.discount.toFixed(2)),
                            IVA: parseFloat(billOff.iva.toFixed(2)),
                            TOTAL: parseFloat(billOff.total.toFixed(2))
                        };
              
                        dataReport.push(data);
                    });
                break;
                case '18':
                    _.each(vm.reportList, function(existency) {
                        var data = {
                            CODIGO: existency.code,
                            NOMBRE: existency.name,
                            COST_REAL: parseFloat(existency.costReal.toFixed(2)),
                            CANTIDAD: parseInt(existency.quantity),
                            COST_PROMED: parseFloat(existency.averCost),
                            COST_CRED: parseFloat(existency.minorQuantity)
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '19':
                    _.each(vm.reportList, function(clnt) {
                        var data = {
                            TIP_IDENT: clnt.typeId,
                            IDENTIFICACION: clnt.identification,
                            NOMBRE: clnt.name,
                            DIRECCION: clnt.address,
                            NRO_TELEF: clnt.phone,
                            NRO_CEL: clnt.celPhone,
                            CORREO: clnt.email,
                            PERS_CONTAC: clnt.contact
                        };
                
                        dataReport.push(data);
                    });
                break;
                case '20':
                    _.each(vm.reportList, function(prov) {
                        var data = {
                            TIP_IDENT: prov.ruc_type,
                            IDENTIFICACION: prov.ruc,
                            NOMBRE: prov.name,
                            RAZON_SOC: prov.razonSocial,
                            DIRECCION: prov.address1,
                            NRO_TELEF: prov.phone,
                            NRO_CEL: prov.celPhone,
                            CORREO: prov.email,
                            TIP_PROV: prov.providerType,
                            PERS_CONTAC: prov.contact
                        };
                
                        dataReport.push(data);
                    });
                case '21':
                    _.each(vm.reportList, function (prov) {
                        var fechaSplitted = prov.fecha.split('/')
                        var dia = fechaSplitted[0];
                        var mes = fechaSplitted[1];
                        fechaSplitted[0] = mes;
                        fechaSplitted[1] = dia;
                        var fecha = fechaSplitted.join('/')
                        var data = {
                            FECHA: new Date(fecha),
                            TIPO_DIARIO: prov.tipoDocumento,
                            CLIENTE_PROVEEDOR: prov.clienteProveedor,
                            DETALLE: prov.descripcion,
                            NUMERO: prov.numero,
                            NUMERO_FACTURA: prov.numeroFactura,
                            DEBE: parseFloat(prov.deber.toFixed(4)),
                            HABER: parseFloat(prov.haber.toFixed(4))
                        };

                        dataReport.push(data);
                    });
                case '22':
                    _.each(vm.reportList, function(existency) {
                        var data = {
                            CODIGO: existency.code,
                            NOMBRE: existency.name,
                            CANTIDAD: parseInt(existency.quantity),
                            TIPO: existency.tipo,
                            PROVEEDOR: existency.groupo,
                            MARCA: existency.marca,
                            LINEA: existency.linea,
                            MEDIDA: existency.medida,
                            IVA: existency.iva,
                            PVP: parseFloat(existency.pvp.toFixed(2)),
                            PORCENTAGE_EFEC: existency.cashPercentage,
                            DESCUENTO_EFEC: existency.cashDiscount,
                            PRECIO_SIN_IVA: parseFloat(existency.sinIva.toFixed(2)),
                            PRECIO_FARMACIA: parseFloat(existency.precio.toFixed(2))
                        };
                
                        dataReport.push(data);
                    });
                break;
            };
            
            var ws = XLSX.utils.json_to_sheet(dataReport);
            /* add to workbook */
            var wb = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(wb, ws, "Ventas");

            /* write workbook and force a download */
            // XLSX.writeFile(wb, 'Reporte_Kardex_'.concat(vm.prodKarName, '.xlsx'));
            XLSX.writeFile(wb, vm.reportName.concat('.xlsx'));
        };

        vm.getBold = function(data) {
            return data.clientName === null ? {"font-weight": 'bold'} : {};
        }

        vm.exit = function() {
            $location.path('/home');
        };
});
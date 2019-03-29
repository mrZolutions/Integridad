'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ReportSalesCtrl
 * @description
 * # ReportSalesCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ReportSalesCtrl', function(_, $localStorage, creditsbillService, billService, eretentionService, 
                                          paymentService, creditsDebtsService, $location, debtsToPayService, consumptionService) {
    var vm = this;
    var today = new Date();
    $('#pickerBillDateOne').data("DateTimePicker").date(today);
    $('#pickerBillDateTwo').data("DateTimePicker").date(today);
    // undefined = retention; true = products; false = sales
    vm.isProductReportList = undefined;
    vm.reportList = undefined;

    vm.getReportProducts = function() {
      vm.isProductReportList = '1';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      billService.getActivesByUserClientAndDates(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportSales = function() {
      vm.isProductReportList = '2';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      billService.getAllByUserClientAndDates(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportRetention = function() {
      vm.isProductReportList = '3';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      eretentionService.getAllByUserClientAndDates(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getCreditsPendingReport = function() {
      vm.isProductReportList = '4';
      vm.reportList = undefined;
      vm.loading = true;
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      creditsbillService.getAllCreditsOfBillByUserClientId(userCliId, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getCreditsPayedReport = function() {
      vm.isProductReportList = '5';
      vm.reportList = undefined;
      vm.loading = true;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      creditsbillService.getAllPayedOfBillByUserClientId(userCliId).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getCCResumenReport = function() {
      vm.isProductReportList = '6';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      paymentService.getAllPaymentsByUserClientIdAndDates(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getCreditsDebtsPendingReport = function() {
      vm.isProductReportList = '7';
      vm.reportList = undefined;
      vm.loading = true;
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      creditsDebtsService.getAllCreditsDebtsPendingOfDebtsToPayByUserClientId(userCliId, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportDebts = function() {
      vm.isProductReportList = '8';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      debtsToPayService.getDebtsToPayByUserClientIdAndDates(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportCierreCaja = function() {
      vm.isProductReportList = '9';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      billService.getForCashClosureReportAndDate(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportCsmProducts = function() {
      vm.isProductReportList = '10';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86399000;
      var userCliId = $localStorage.user.subsidiary.userClient.id;

      consumptionService.getActivesByUserClientAndDates(userCliId, dateOne, dateTwo).then(function(response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getTotal = function(total, subTotal) {
      var total = total - subTotal;
      return (total).toFixed(2);
    };

    vm.exportExcel = function() {
      var dataReport = [];
      if (vm.isProductReportList === '1') {
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
      } else if (vm.isProductReportList === '2') {
        _.each(vm.reportList, function(bill) {
          var data = {
            FECHA: bill.date,
            CODIGO_CLIENTE: bill.clientCode,
            CLIENTE: bill.clientName,
            RUC_CI: bill.ruc,
            NUMERO_FACTURA: bill.billNumber,
            NUMERO_AUTORIZACION: bill.authorizationNumber,
            ESTADO: bill.status,
            OTI: bill.oti,
            BASE_DOCE: bill.status === 'ACTIVA' ? parseFloat(bill.baseTaxes.toFixed(2)) : parseFloat(0),
            DESCUENTO: bill.status === 'ACTIVA' ? parseFloat(bill.discount.toFixed(2)) : parseFloat(0),
            BASE_CERO: bill.status === 'ACTIVA' ? parseFloat(bill.baseNoTaxes.toFixed(2)) : parseFloat(0),
            IVA: bill.status === 'ACTIVA' ? parseFloat(bill.iva.toFixed(2)) : parseFloat(0),
            TOTAL: bill.status === 'ACTIVA' ? parseFloat(bill.total.toFixed(2)) : parseFloat(0),
            FECHA_VENCIMIENTO: bill.endDate,
            CAJA: bill.cashier,
            BODEGA: bill.warehouse,
            SUCURSAL: bill.subsidiary,
            USUARIO: bill.userName
          };

          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '3') {
        _.each(vm.reportList, function(retention) {
          var data = {
            FECHA: retention.date,
            FECHA_DOCUMENTO: retention.documentDate,
            CODIGO_PROVEEDOR: retention.providerCode,
            PROVEEDOR: retention.providerName,
            RUC: retention.ruc,
            NUMERO_RETENCION: retention.retentionNumber,
            NUMERO_AUTORIZACION: retention.authorizationNumber,
            NUMERO_FACTURA: retention.documentNumber,
            EJERCICIO_FISCAL: retention.ejercicioFiscal,
            ESTADO: retention.status,
            BASE_FUENTE: parseFloat((retention.baseFuente).toFixed(2)),
            PORCENT_FUENTE: parseFloat((retention.porcenFuente).toFixed(2)),
            SUBTOTAL_FUENTE: parseFloat((retention.subTotalFuente).toFixed(2)),
            BASE_IVA: parseFloat((retention.baseIva).toFixed(2)),
            PORCENT_IVA: parseFloat((retention.porcenIva).toFixed(2)),
            SUBTOTAL_IVA: parseFloat((retention.subTotalIva).toFixed(2)),
            TOTAL: parseFloat((retention.total).toFixed(2)),
            SUCURSAL: retention.subsidiary,
            USUARIO: retention.userName
          };
        
          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '4') {
        _.each(vm.reportList, function(creditsreport) {
          var data = {
            CLIENTE: creditsreport.clientName,
            FACTURA: creditsreport.billNumber,
            FECHA_VENTA: creditsreport.fechVenta !== null ? new Date(creditsreport.fechVenta) : creditsreport.fechVenta,
            FECHA_VENCE: creditsreport.fechVence !== null ? new Date(creditsreport.fechVence) : creditsreport.fechVence,
            DIAS_CREDIT: creditsreport.diasCredit,
            DIAS_VENCE: creditsreport.diasVencim,
            VENTA: parseFloat(creditsreport.costo.toFixed(2)),
            ABONO: parseFloat(creditsreport.valorAbono.toFixed(2)),
            RETEN: parseFloat(creditsreport.valorReten.toFixed(2)),
            N_C: parseFloat(creditsreport.valorNotac.toFixed(2)),
            SALDO: parseFloat(creditsreport.saldo.toFixed(2)),
            PLAZO_MENOR_30: parseFloat(creditsreport.pplazo.toFixed(2)),
            PLAZO_31_60: parseFloat(creditsreport.splazo.toFixed(2)),
            PLAZO_61_90: parseFloat(creditsreport.tplazo.toFixed(2)),
            PLAZO_91_120: parseFloat(creditsreport.cplazo.toFixed(2)),
            PLAZO_MAYOR_120: parseFloat(creditsreport.qplazo.toFixed(2))
          };      

          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '5') {
        _.each(vm.reportList, function(creditspayedreport) {
          var data = {
            CLIENTE: creditspayedreport.clientName,
            IDENTIFICACION: creditspayedreport.ruc,
            FACTURA: creditspayedreport.billNumber,
            VALOR_FACTURA: creditspayedreport.costo,
            SALDO: creditspayedreport.saldo,
            STATUS: creditspayedreport.statusCredits
          };

          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '6') {
        _.each(vm.reportList, function(ccresumenreport) {
          var data = {
            CLIENTE: ccresumenreport.nameClient,
            FACTURA: ccresumenreport.billNumber,
            VENTA: ccresumenreport.billTotal,
            TIPO_TRANSAC: ccresumenreport.tipTransac,
            MODO_PAGO: ccresumenreport.formPago,
            FECHA_PAGO: ccresumenreport.fechPago !== null ? new Date(ccresumenreport.fechPago) : ccresumenreport.fechPago,
            VALOR_ABONO: parseFloat(ccresumenreport.valorAbono.toFixed(2)),
            VALOR_RETEN: parseFloat(ccresumenreport.valorReten.toFixed(2)),
            VALOR_NC: parseFloat(ccresumenreport.valorNotac.toFixed(2))
          };

          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '7') {
        _.each(vm.reportList, function(creditsdebtsreport) {
          var data = {
            PROVEEDOR: creditsdebtsreport.providerName,
            FACTURA: creditsdebtsreport.billNumber,
            FECHA_VENTA: creditsdebtsreport.fechVenta !== null ? new Date(creditsdebtsreport.fechVenta) : creditsdebtsreport.fechVenta,
            FECHA_VENCE: creditsdebtsreport.fechVence !== null ? new Date(creditsdebtsreport.fechVence) : creditsdebtsreport.fechVence,
            DIAS_CREDIT: creditsdebtsreport.diasCredit,
            DIAS_VENCE: creditsdebtsreport.diasVencim,
            VENTA: parseFloat(creditsdebtsreport.total.toFixed(2)),
            ABONO: parseFloat(creditsdebtsreport.valorAbono.toFixed(2)),
            RETEN: parseFloat(creditsdebtsreport.valorReten.toFixed(2)),
            SALDO: parseFloat(creditsdebtsreport.saldo.toFixed(2))
          };      

          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '8') {
        _.each(vm.reportList, function(debt) {
          var data = {
            FECHA: debt.date,
            CODIGO_PROVEEDOR: debt.providerCode,
            PROVEEDOR: debt.providerName,
            RUC_CI: debt.ruc,
            NUMERO_CUENTA: debt.debtNumber,
            NUMERO_FACTURA: debt.billNumber,
            NUMERO_AUTORIZACION: debt.authorizationNumber,
            ESTADO: debt.status,
            BASE_DOCE: debt.status === 'ACTIVA' ? parseFloat(debt.subTotalDoce.toFixed(2)) : parseFloat(0),
            IVA: debt.status === 'ACTIVA' ? parseFloat(debt.iva.toFixed(2)) : parseFloat(0),
            BASE_CERO: debt.status === 'ACTIVA' ? parseFloat(debt.subTotalCero.toFixed(2)) : parseFloat(0),
            TOTAL: debt.status === 'ACTIVA' ? parseFloat(debt.total.toFixed(2)) : parseFloat(0),
            FECHA_VENCIMIENTO: debt.endDate,
            CAJA: debt.cashier,
            BODEGA: debt.warehouse,
            SUCURSAL: debt.subsidiary,
            USUARIO: debt.userName
          };

          dataReport.push(data);
        });
      } else if (vm.isProductReportList === '9') {
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
      } else if (vm.isProductReportList === '10') {
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
            TOTAL: parseFloat(consp.total.toFixed(2))
          };

          dataReport.push(data);
        });
      };

      var ws = XLSX.utils.json_to_sheet(dataReport);
      /* add to workbook */
      var wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "Ventas");

      /* write workbook and force a download */
      XLSX.writeFile(wb, "Reporte.xlsx");
    };

    vm.exit = function() {
      $location.path('/home');
    };
});
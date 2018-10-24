'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ReportSalesCtrl', function (_, $localStorage, creditsbillService, billService, eretentionService, utilStringService, FileSaver, productService) {
    var vm = this;
    var today = new Date();
    $('#pickerBillDateOne').data("DateTimePicker").date(today);
    $('#pickerBillDateTwo').data("DateTimePicker").date(today);
    // undefined = retention; true = products; false = sales
    vm.isProductReportList = undefined;
    vm.reportList = undefined;

    vm.getReportProducts = function(){
      vm.isProductReportList = '1';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86340000;
      var subId = $localStorage.user.subsidiary.userClient.id;

      billService.getActivesByUserClientAndDates(subId, dateOne, dateTwo).then(function(response){
        vm.reportList = response;
        vm.loading = false;
      }).catch(function (error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportSales = function(){
      vm.isProductReportList = '2';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86340000;
      var subId = $localStorage.user.subsidiary.userClient.id;

      billService.getAllByUserClientAndDates(subId, dateOne, dateTwo).then(function(response){
        vm.reportList = response;
        vm.loading = false;
      }).catch(function (error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportRetention = function(){
      vm.isProductReportList = '3';
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86340000;
      var subId = $localStorage.user.subsidiary.userClient.id;

      eretentionService.getAllByUserClientAndDates(subId, dateOne, dateTwo).then(function(response){
        vm.reportList = response;
        vm.loading = false;
      }).catch(function (error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getCreditsPendingReport = function(){
      vm.isProductReportList = '4';
      vm.reportList = undefined;
      vm.loading = true;
      var subId = $localStorage.user.subsidiary.userClient.id;

      creditsbillService.getAllCreditsOfBillByUserClientId(subId).then(function(response){
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getCreditsPayedReport = function(){
      vm.isProductReportList = '5';
      vm.reportList = undefined;
      vm.loading = true;
      var subId = $localStorage.user.subsidiary.userClient.id;

      creditsbillService.getAllPayedOfBillByUserClientId(subId).then(function(response){
        vm.reportList = response;
        vm.loading = false;
      }).catch(function(error){
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.exportExcel = function(){
      var dataReport = [];
      if(vm.isProductReportList === '1'){
        _.each(vm.reportList, function(bill){
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
      } else if(vm.isProductReportList === '2'){
        _.each(vm.reportList, function(bill){
          var data = {
            FECHA: bill.date,
            CODIGO_CLIENTE: bill.clientCode,
            CLIENTE: bill.clientName,
            RUC_CI: bill.ruc,
            NUMERO_FACTURA: bill.billNumber,
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
      } else if(vm.isProductReportList === '3'){
        _.each(vm.reportList, function(retention){
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
            TOTAL:parseFloat(retention.total.toFixed(2)),
            SUCURSAL: retention.subsidiary,
            USUARIO: retention.userName
          };
        
          dataReport.push(data);
        });
      } else if(vm.isProductReportList === '4'){
        _.each(vm.reportList, function(creditsreport){
          var data = {
            NOMBRE_CLIENTE: creditsreport.clientName,
            IDENTIFICACION: creditsreport.ruc,
            NUMERO_FACTURA: creditsreport.billNumber,
            VALOR_FACTURA: creditsreport.costo,
            NUMERO_CUOTAS: creditsreport.payNumber,
            SALDO: creditsreport.saldo,
            STATUS: creditsreport.statusCredits
          };

          dataReport.push(data);
        });
      } else if(vm.isProductReportList === '5'){
        _.each(vm.reportList, function(creditspayedreport){
          var data = {
            NOMBRE_CLIENTE: creditspayedreport.clientName,
            IDENTIFICACION: creditspayedreport.ruc,
            NUMERO_FACTURA: creditspayedreport.billNumber,
            VALOR_FACTURA: creditspayedreport.costo,
            NUMERO_CUOTAS: creditspayedreport.payNumber,
            SALDO: creditspayedreport.saldo,
            STATUS: creditspayedreport.statusCredits
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
  });

'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ReportSalesCtrl', function (_, $localStorage, $location, billService, utilStringService, FileSaver) {
    var vm = this;
    var today = new Date();
    $('#pickerBillDateOne').data("DateTimePicker").date(today);
    $('#pickerBillDateTwo').data("DateTimePicker").date(today);
    vm.isProductReportList = undefined;
    vm.reportList = undefined;

    vm.getReportProducts = function(){
      vm.isProductReportList = true;
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86340000;
      var subId = $localStorage.user.subsidiary.userClient.id

      billService.getActivesByUserClientAndDates(subId, dateOne, dateTwo).then(function (response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.getReportSales = function(){
      vm.isProductReportList = false;
      vm.reportList = undefined;
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86340000;
      var subId = $localStorage.user.subsidiary.userClient.id

      billService.getAllByUserClientAndDates(subId, dateOne, dateTwo).then(function (response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.exportExcel = function(){
      var dataReport = [];
      if(vm.isProductReportList){
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

          dataReport.push(data)
        });
      } else {
        _.each(vm.reportList, function(bill){
          var data = {
            FECHA: bill.date,
            CODIGO_CLIENTE: bill.clientCode,
            CLIENTE: bill.clientName,
            RUC_CI: bill.ruc,
            NUMERO_FACTURA: bill.billNumber,
            ESTADO: bill.status,
            OTI: bill.oti,
            SUBTOTAL: parseFloat(bill.subTotal.toFixed(2)),
            IVA: parseFloat(bill.iva.toFixed(2)),
            TOTAL: parseFloat(bill.total.toFixed(2)),
            FECHA_VENCIMIENTO: bill.endDate,
            CAJA: bill.cashier,
            BODEGA: bill.warehouse,
            SUCURSAL: bill.subsidiary,
            USUARIO: bill.userName,
          };

          dataReport.push(data)
        });
      }


      var ws = XLSX.utils.json_to_sheet(dataReport);

      /* add to workbook */
      var wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "Ventas");

      /* write workbook and force a download */
      XLSX.writeFile(wb, "Reporte.xlsx");
    };
  });

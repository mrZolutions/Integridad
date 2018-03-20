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
    vm.reportList = undefined;

    vm.getReport = function(){
      vm.loading = true;
      var dateOne = $('#pickerBillDateOne').data("DateTimePicker").date().toDate().getTime();
      var dateTwo = $('#pickerBillDateTwo').data("DateTimePicker").date().toDate().getTime();
      dateTwo += 86340000;
      var subId = $localStorage.user.subsidiary.userClient.id

      billService.getByUserClientAndDates(subId, dateOne, dateTwo).then(function (response) {
        vm.reportList = response;
        vm.loading = false;
      }).catch(function (error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.exportExcel = function(){
      var header = '<meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8">';
      var blob = new Blob([header + document.getElementById('exportTable').innerHTML], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'
        });

      FileSaver.saveAs(blob, "Report.xlsx");
    };
  });

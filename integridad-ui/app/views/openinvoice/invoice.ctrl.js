'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:InvoiceCtrl
 * @description
 * # InvoiceCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('InvoiceCtrl', function(_, $routeParams, $http) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.data = undefined;
        vm.invoiceNumber = undefined;
        vm.mailClient = undefined;

        vm.baseUrl = 'http://localhost:3600/invoices/print/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type':'application/json',
        };

        vm.processData = function (data) {
            console.log(data)
            vm.data = data;
            vm.invoiceNumber = data.emisor.establecimiento.codigo +'-'+ data.emisor.establecimiento.punto_emision+'-';
            vm.invoiceNumber += data.secuencial.toString().padStart(9, '0');

            vm.mailClient = data.comprador.email.split(',')[0];
        }


        vm.clickPdf = function (){
            var element = document.getElementById('printArea');
            var opt = {
                margin:       0.5,
                filename:     'factura.pdf',
                image:        { type: 'jpeg', quality: 0.98 },
                html2canvas:  { dpi: 192, letterRendering: true },
                jsPDF:        { unit: 'in', format: 'A4', orientation: 'portrait' }
              };
            html2pdf(element, opt);
        }

        vm.clickXml = function () {
            var blob = new Blob([vm.data.xmlField], {type: 'text/xml'});
            if(window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveBlob(blob, 'factura');
            }
            else{
                var elem = window.document.createElement('a');
                elem.href = window.URL.createObjectURL(blob);
                elem.download = 'factura';        
                document.body.appendChild(elem);
                elem.click();        
                document.body.removeChild(elem);
            }
        }

        function _getData(){
            var url = vm.baseUrl + $routeParams.id +'/'+$routeParams.acceso;
            $http.get(url, vm.config).then(function (response) {
                console.log(response)
                if(response.data[0]){
                    vm.processData(response.data[0]);
                } else {
                    console.log('Respnse DATA Error')    
                }
            }).catch(function(error) {
                console.log(error)
                vm.loading = false;
                vm.error = error.data;
            });
        }

        (function initController() {
            _getData();
        })();

});
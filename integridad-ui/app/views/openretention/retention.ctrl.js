'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:RetentionCtrl
 * @description
 * # RetentionCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('RetentionCtrl', function(_, $routeParams, $http) {
    
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.retention = undefined;

        vm.baseUrl = 'https://invoicesmrz.herokuapp.com/retentions/print/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type': 'application/json',
        };
        vm.options = {
            width: 2,
            height: 60,
            quite: 10,
            displayValue: true,
            font: "monospace",
            textAlign: "center",
            fontSize: 12,
            backgroundColor: "",
            lineColor: "#000"
        };

        vm.clickPdf = function (){
            var element = document.getElementById('printArea');
            var opt = {
                margin: 0.4,
                filename: 'retention.pdf',
                image: { type: 'jpeg', quality: 0.98 },
                html2canvas: { dpi: 192, letterRendering: true },
                jsPDF: { unit: 'in', format: 'A4', orientation: 'portrait' }
              };
            html2pdf(element, opt);
        }

        vm.clickXml = function () {
            var blob = new Blob([vm.retention.xmlField], {type: 'text/xml'});
            if(window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveBlob(blob, 'retencion');
            }
            else{
                var elem = window.document.createElement('a');
                elem.href = window.URL.createObjectURL(blob);
                elem.download = 'retencion';        
                document.body.appendChild(elem);
                elem.click();        
                document.body.removeChild(elem);
            }
        }

        vm.processData = function(retention){
            vm.retentionNumber = retention.emisor.establecimiento.codigo +'-'+ retention.emisor.establecimiento.punto_emision+'-';
            vm.retentionNumber += retention.secuencial.toString().padStart(9, '0');
            vm.retention = retention;
            vm.total = 0;

            for(var i = 0; i < retention.items.length; i++){
                var item = retention.items[i];
                vm.total += parseFloat(item.valor_retenido);
            }
        }

        function _getData() {
            var url = vm.baseUrl + $routeParams.id + '/' + $routeParams.acceso;
            $http.get(url, vm.config).then(function (response) {
                console.log(response)
                if (response.data[0]) {
                    vm.processData(response.data[0]);
                } else {
                    console.log('Respnse DATA Error')
                }
            }).catch(function (error) {
                console.log(error)
                vm.loading = false;
                vm.error = error.data;
            });
        };

        (function initController() {
            _getData();
        })();
});
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

        var payForm = {
            "efectivo": {name:'20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO', value: '20'},
            "dinero_electronico_ec": {name:'20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO', value: '20'},
            "transferencia": {name:'20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO', value: '20'},
            "credito": {name:'20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO', value: '20'},
            "cheque": {name:'20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO', value: '20'},
            "cheque_posfechado": {name:'20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO', value: '20'},
            "tarjeta_credito": {name:'19 - TARJETA DE CREDITO', value: '19'},
            "tarjeta_debito": {name:'19 - TARJETA DE CREDITO', value: '19'},
        }

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.data = undefined;
        vm.invoiceNumber = undefined;
        vm.mailClient = undefined;
        vm.pagos = undefined;
        vm.cambio = undefined;

        vm.baseUrl = 'https://invoicesmrz.herokuapp.com/invoices/print/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type':'application/json',
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

        vm.processData = function (data) {
            vm.data = data;
            vm.invoiceNumber = data.emisor.establecimiento.codigo +'-'+ data.emisor.establecimiento.punto_emision+'-';
            vm.invoiceNumber += data.secuencial.toString().padStart(9, '0');

            vm.mailClient = data.comprador.email.split(',')[0];

            vm.txt = vm.data.clave_acceso;

            if(vm.data.credito){
                vm.pagos = {name: "CREDITO", code: payForm["credito"].name, total: vm.data.credito.monto}
            } else if(vm.data.pagos){
                vm.pagos = {
                    name: vm.data.pagos[0].medio, 
                    code: payForm[vm.data.pagos[0].medio].name,
                    total: vm.data.pagos[0].total
                }
            }

            vm.cambio = (parseFloat(vm.data.totales.importe_total) - parseFloat(vm.pagos.total)).toFixed(2)
        }


        vm.clickPdf = function (){
            var element = document.getElementById('printArea');
            var opt = {
                margin: 0.4,
                filename: 'factura.pdf',
                image: { type: 'jpeg', quality: 0.98 },
                html2canvas: { dpi: 192, letterRendering: true },
                jsPDF: { unit: 'in', format: 'A4', orientation: 'portrait' }
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
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

        vm.baseUrl = 'http://localhost:3600/invoices/print/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type':'application/json',
        };

        vm.processData = function (data) {
            console.log(data)
            vm.data = data;
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
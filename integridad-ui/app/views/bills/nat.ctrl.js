'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:NatCtrl
 * @description
 * # NatCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('NatCtrl', function(_, $http, holderService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.baseUrl = 'https://invoicesmrz.herokuapp.com/invoices/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type':'application/json',
        };
        vm.billList = [];

        vm.resendBill = function(bill){
            vm.loading = true;
            $http.get(vm.baseUrl + 'resend/' + bill.id, vm.config).then(function (response) {
                vm.success = 'Comprobante reenviado';
                vm.loading = false;
            }).catch(function(error) {
                console.log(error)
                vm.loading = false;
                vm.error = error.data;
            });
        }

        vm.recheckBill = function(bill){
            vm.loading = true;
            $http.get(vm.baseUrl + 'recheck/' + bill.id, vm.config).then(function (response) {
                vm.success = 'Comprobante revisado';
                vm.loading = false;
            }).catch(function(error) {
                console.log(error)
                vm.loading = false;
                vm.error = error.data;
            });
        }

        function _getData(){
            vm.loading = true;
            vm.user = holderService.get();
            if(vm.user.token !== null){
                vm.config.headers['Authorization'] = 'Bearer ' + vm.user.token;

                $http.get(vm.baseUrl + 'nat/company', vm.config).then(function (response) {
                    if(response.data){
                        vm.billList = response.data;
                        for (const bill of vm.billList) {
                            bill.stringSeq = bill.emisor.establecimiento.codigo + bill.emisor.establecimiento.punto_emision + 
                            bill.secuencial.toString().padStart(9, '0');
                        }
                    } else {
                        console.log('Respnse DATA Error')    
                    }
                    vm.loading = false;
                }).catch(function(error) {
                    console.log(error)
                    vm.loading = false;
                    vm.error = error.data;
                });
            } 
            vm.loading = false;
        }

        (function initController() {
            _getData();
        })();
});
'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:NatRetentionCtrl
 * @description
 * # NatRetentionCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('NatRetentionCtrl', function(_, $http, holderService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.baseUrl = 'https://invoicesmrz.herokuapp.com/retentions/';
        // vm.baseUrl = 'http://localhost:3600/retentions/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type':'application/json',
        };
        vm.retentionList = [];
        vm.doc = undefined;

        vm.resendRetention = function(ret){
            vm.error = undefined;
            vm.success = undefined;
            if(ret) vm.doc = ret;
            vm.loading = true;
            $http.post(vm.baseUrl + 'resend/', vm.doc, vm.config).then(function (response) {
                vm.doc = undefined;
                vm.success = 'Comprobante reenviado';
                vm.loading = false;
            }).catch(function(error) {
                console.log(error)
                vm.loading = false;
                vm.error = error.data;
            });
        }

        vm.recheckRetention = function(ret){
            vm.loading = true;
            $http.get(vm.baseUrl + 'recheck/' + ret.id, vm.config).then(function (response) {
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
                        vm.retentionList = response.data;
                        for (var i = 0; i < vm.retentionList.length; i++) {
                            var item = vm.retentionList[i];
                            item.stringSeq = item.emisor.establecimiento.codigo + item.emisor.establecimiento.punto_emision + 
                            item.secuencial.toString().padStart(9, '0');
                            item.total = 0;
                            for(var j = 0; j < item.items.length; j++){
                                var retItm = item.items[j];
                                item.total += parseFloat(retItm.valor_retenido);
                            }
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
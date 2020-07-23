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
        vm.baseUrl = 'https://invoicesmrz.herokuappk.com/invoices/';
        // vm.baseUrl = 'http://localhost:3600/invoices/';
        vm.config = {};
        vm.config.headers = {
            'Content-Type':'application/json',
        };
        vm.tipoImpuesto = {
            '2':'IVA',
            '3':'ICE',
            '5':'IRBPNR'
        };
        vm.codigoPorcentaje = {
            '0':0,
            '2':12,
            '3':14
        };
        vm.payForms = [
            { name:'Efectivo', codigo: "efectivo" },
            { name:'Dinero Electr&oacutenico', codigo: "dinero_electronico_ec" },
            { name:'Transferencia', codigo: "transferencia" },
            { name:'Cr&eacute;dito', codigo: "credito" },
            { name:'Cheque', codigo: "cheque" },
            { name:'Cheque Posfechado', codigo: "cheque_posfechado" },
            { name:'Tarjeta de Cr&eacute;dito', codigo: "tarjeta_credito" },
            { name:'Tarjeta de D&eacute;bito', codigo: "tarjeta_debito" },
        ];
        vm.billList = [];
        vm.doc = undefined;

        vm.resendBill = function(bill){
            vm.error = undefined;
            vm.success = undefined;
            if(bill) vm.doc = bill;
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

        vm.recheckBill = function(bill){
            vm.error = undefined;
            vm.success = undefined;
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

        vm.getTotalDetail = function(index){
            vm.doc.items[index].precio_total_sin_impuestos = 
            (vm.doc.items[index].cantidad * vm.doc.items[index].precio_unitario) - 
            (vm.doc.items[index].descuento);

            _.each(vm.doc.items[index].impuestos, function(imp){ 
                imp.base_imponible = vm.doc.items[index].precio_total_sin_impuestos;
                imp.valor = imp.base_imponible * parseFloat('0.'+imp.tarifa);
            });

            _getTotals()
        }

        vm.removeDetail = function(index){
            vm.doc.items.splice(index,1);
            _getTotals()
        }

        vm.getTotales = function(){
            _getTotals();
        }

        function _getTotals() {
            vm.doc.totales.total_sin_impuestos = 0;
            var impuestos = 0;
            _.each(vm.doc.items, function(itm){
                _.each(itm.impuestos, function(imp){
                    var impuesto = _.find(vm.doc.totales.impuestos, 
                        function(impTotales){ 
                            return  impTotales.codigo === imp.codigo && impTotales.codigo_porcentaje === imp.codigo_porcentaje; 
                        });
                    
                    impuestos += imp.valor;
                    if(impuesto){
                        impuesto.base_imponible += imp.base_imponible
                    } else {
                        vm.doc.totales.impuestos.push(angular.copy(imp));
                    }
                    
                });
                vm.doc.totales.total_sin_impuestos += itm.precio_total_sin_impuestos;
            });
            vm.doc.totales.importe_total = 
                vm.doc.totales.total_sin_impuestos + impuestos - vm.doc.totales.descuento + 
                vm.doc.totales.propina;
        }

        function _getData(){
            vm.loading = true;
            vm.user = holderService.get();
            if(vm.user.token !== null){
                // vm.config.headers['Authorization'] = 'Bearer ' + vm.user.token;
                vm.config.headers['Authorization'] = 'Bearer ' + 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1ZWUxNWJiNTI5NmI3MDYwZDYwMGRkNjEiLCJlbWFpbCI6ImRhbmllbEBnbWFpbC5jb20iLCJwZXJtaXNzaW9uTGV2ZWwiOjIxNDc0ODM2NDcsInByb3ZpZGVyIjoiZW1haWwiLCJuYW1lIjoiRGFuaWVsIEEuIEFyY29zIiwiY29tcGFueUlkIjoiNWVmMzkwODE1ZDZmOGQzY2RkMDVmMzFmIiwicmVmcmVzaEtleSI6Ikd0NEU3enFqa0MwR2dRVzhrRk1Lc0E9PSIsImlhdCI6MTU5NTQzNjEzNH0.0o2TJmSutBo5HfUzo0Qa5nSbohHd1ez35m9LTH7sM84';

                $http.get(vm.baseUrl + 'nat/company', vm.config).then(function (response) {
                    if(response.data){
                        vm.billList = response.data;
                        for (var i = 0; i < vm.billList.length; i++) {
                            var bill = vm.billList[i];
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
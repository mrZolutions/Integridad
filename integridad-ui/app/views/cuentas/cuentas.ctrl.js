'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:CuentasContablesCtrl
 * @description
 * # CuentasContablesCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('CuentasContablesCtrl', function(_, $location, holderService, cuentaContableService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.cuentasContablesList = undefined;
        vm.typeSelected = {};
        vm.user = holderService.get();

        vm.tipoCtaCtable = [
            {code: 'GENE' ,name: 'GENERAL'}, {code: 'BIEN' ,name: 'BIENES'},
            {code: 'SERV', name: 'SERVICIOS'}, {code: 'MATP', name: 'MATERIA PRIMA'},
            {code: 'CONS', name: 'CONSUMIBLES'}, {code: 'RMBG', name: 'REEMBOLSO GASTOS'},
            {code: 'TKAE', name: 'TICKETS AEREOS'}, {code: 'ACTV', name: 'ACTIVOS'},
            {code: 'PASV', name: 'PASIVOS'}, {code: 'INGR', name: 'INGRESOS', showNumber: true},
            {code: 'EGRE', name: 'EGRESOS', showNumber: true}, {code: 'PATR', name: 'PATRIMONIOS'},
            {code: 'BANC', name: 'DEFINIDA PARA BANCOS', showNumber: true}, {code: 'GADM', name: 'GASTOS ADMINISTRATIVOS'},
            {code: 'GEMP', name: 'GASTOS DE EMPLEADOS'}, {code: 'INVT', name: 'INVENTARIOS'}
        ];

        function _activate() {
            vm.advertencia = false;
            vm.error = undefined;
            vm.success = undefined;
            vm.cuentaSelected = undefined;
            vm.user = vm.user;
            vm.subContabActive = vm.user.subsidiary.contab;
            if (vm.subContabActive) {
                vm.loading = true;
                cuentaContableService.getCuentaContableByUserClient(vm.user.subsidiary.userClient.id).then(function(response) {
                    vm.cuentasContablesList = response;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.advertencia = true;
            };
        };

        function _initializeCuenta() {
            vm.cuentaSelected = {
                userClient: vm.user.subsidiary.userClient
            };
        };

        function create() {
            vm.loading = true;
            cuentaContableService.create(vm.cuentaSelected).then(function(response) {
                _activate();
                vm.error = undefined;
                vm.success = 'Registro realizado con exito';
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function update() {
            cuentaContableService.update(vm.cuentaSelected).then(function(response) {
                _activate();
                if (vm.cuentaSelected.active) {
                    vm.success = 'Registro actualizado con exito';
                } else {
                    vm.success = 'Registro eliminado con exito';
                };
                vm.error = undefined;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.typeChange = function() {
            vm.cuentaSelected.type = vm.typeSelected.code;
        };

        vm.cuentaEdit = function(cuenta) {
            vm.error = undefined;
            vm.success = undefined;
            vm.typeSelected = _.find(vm.tipoCtaCtable, function(tipo){ return tipo.code === cuenta.type; });
            vm.cuentaSelected = cuenta;
        };

        vm.remove = function() {
            vm.cuentaSelected.active = false;
            update();
        };

        vm.cuentaCreate = function() {
            vm.error = undefined;
            vm.success = undefined;
            vm.typeSelected = {};
            _initializeCuenta();
        };

        vm.save = function() {
            vm.error = undefined;
            vm.success = undefined;
            if (vm.cuentaSelected.id) {
                update();
            } else {
                create();
            };
        };

        vm.cancel = function() {
            _activate();
        };

        vm.import = function(){
            var f = document.getElementById('file').files[0];
            if(f){
                var r = new FileReader();
                r.onload = function(event) {
                    var data = event.target.result;
                    var workbook = XLSX.read(data, {
                    type: "binary"
                    });
                    workbook.SheetNames.forEach(function(sheet) {
                        var cuentas = XLSX.utils.sheet_to_row_object_array(
                        workbook.Sheets[sheet]
                        );
                        
                        _.each(cuentas, function(newCuenta){
                            newCuenta.userClient = vm.user.subsidiary.userClient;
                        })
                        cuentaContableService.createList(cuentas).then(function(response) {
                            _activate();
                            vm.error = undefined;
                            $('#modalUpload').modal('hide');
                            vm.success = 'Registros creados con exito';
                        }).catch(function(error) {
                            vm.loading = false;
                            vm.error = error.data;
                        });

                    });
                };
                r.readAsBinaryString(f);
            } 
        }

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
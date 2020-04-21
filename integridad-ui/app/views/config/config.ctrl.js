'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ConfigCtrl
 * @description
 * # ConfigCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('ConfigCtrl', function(_, $location, $localStorage, configCuentasService, cuentaContableService, optionConfigCuentasService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.configCuentasList = undefined;

        optionConfigCuentasService.getOptionConfigCuentas().then(function(response) {
            vm.optionsConfig = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });

        cuentaContableService.getCuentaContableByUserClient($localStorage.user.subsidiary.userClient.id).then(function(response) {
            vm.cuentasContablesList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });

        function _activate() {
            vm.userClient = $localStorage.user.subsidiary.userClient
            vm.error = undefined;
            vm.success = undefined;

            vm.loading = true;
            configCuentasService.getConfigCuentaByUserClient(vm.userClient.id).then(function(response){
                vm.configCuentasList = response
                for(var index in vm.configCuentasList){
                    var cuentaContableFound = _.findWhere(vm.cuentasContablesList, {id: vm.configCuentasList[index].idCuenta});
                    for(var indexOption in vm.optionsConfig){
                        if(vm.optionsConfig[indexOption].id === vm.configCuentasList[index].option.id){
                            vm.optionsConfig[indexOption].selected = cuentaContableFound;
                        } 
                    }
                }
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function create() {
            configCuentasService.save(vm.configCuentasList, vm.userClient.id).then(function(response) {
                _activate();
                vm.error = undefined;
                vm.success = 'Registro realizado con exito';
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.save = function() {
            vm.loading = true;
            vm.error = undefined;
            vm.success = undefined;

            vm.configCuentasList = [];
            for(var index in vm.optionsConfig){
                if(vm.optionsConfig[index].selected !== undefined && vm.optionsConfig[index].selected !== ''){
                    var configCuenta = {
                        userClient: vm.userClient,
                        active: true,
                        option: vm.optionsConfig[index],
                        idCuenta: vm.optionsConfig[index].selected.id,
                        code: vm.optionsConfig[index].selected.code,
                        description: vm.optionsConfig[index].selected.name,
                    };

                    vm.configCuentasList.push(configCuenta);
                }
            }

            create();
        };

        vm.cancel = function() {
            _activate();
        };

        (function initController() {
            _activate();
        })();
});
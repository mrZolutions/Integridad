'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ConfigCtrl
 * @description
 * # ConfigCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('ConfigCtrl', function(_, holderService, configCuentasService, cuentaContableService, optionConfigCuentasService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.configCuentasList = undefined;
        vm.cuentasContablesList = undefined;
        vm.optionsConfig = undefined;
        vm.user = holderService.get();

        optionConfigCuentasService.getOptionConfigCuentas().then(function(response) {
            vm.optionsConfig = response;
            vm.optionsConfigInitial = angular.copy(vm.optionsConfig);
            if(vm.cuentasContablesList){
                _filterAccounts();
            }
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });

        cuentaContableService.getCuentaContableByUserClient(vm.user.subsidiary.userClient.id).then(function(response) {
            vm.cuentasContablesList = response;
            if(vm.optionsConfig){
                _filterAccounts();
            }
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });

        function _filterAccounts(){
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

        function _activate() {
            vm.userClient = vm.user.subsidiary.userClient
            vm.error = undefined;
            vm.success = undefined;

            _filterAccounts();
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
            vm.optionsConfig = angular.copy(vm.optionsConfigInitial);
            _activate();
        };

        (function initController() {
            _activate();
        })();
});
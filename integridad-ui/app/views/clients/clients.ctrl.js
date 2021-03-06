'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ClientsCtrl
 * @description
 * # ClientsCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('ClientsCtrl', function ($routeParams, $location, projectService, utilStringService, countryListService, clientService, 
        holderService, validatorService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.client=undefined;
        vm.countryList = countryListService.getCountryList();
        vm.citiesList = countryListService.getCitiesEcuador();
        vm.clientList = undefined;
        vm.salesOption = 'CLIENTS';

        function _activate() {
            vm.user = holderService.get()
            vm.usrCliId = vm.user.subsidiary.userClient.id;
            switch(true) {
                case $location.path().includes('/cotizacion'): vm.salesOption = 'COTIZ'; break;
            }

            if ($routeParams.create) {
                vm.clientCreate();
            } else {
                vm.loading = true;
                clientService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                    vm.clientList = response;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            };
        };

        function create() {
            clientService.getClientByUserClientAndIdentification(vm.usrCliId, vm.client.identification).then(function(response) {
                if (response.length === 0) {
                    clientService.create(vm.client).then(function(response) {
                        vm.client=undefined;
                        _activate();
                        vm.error = undefined;
                        vm.success = 'Registro realizado con exito';
                        if ($routeParams.create) {
                            $location.path('/bills/bill');
                        };
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.error = 'El Cliente Ya Existe';
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function update(isRemove) {
            clientService.update(vm.client).then(function(response) {
                vm.client=undefined;
                _activate();
                vm.error = undefined;
                if (isRemove) {
                    vm.success = 'Registro eliminado con exito';
                } else {
                    vm.success = 'Registro actualizado con exito';
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientCotizar= function(client) {
            localStorage.setItem("client", JSON.stringify(client));
            window.location = "#!/quotation/quotation";
        };

        vm.clientCreate = function() {
            projectService.getNumberOfProjects(vm.user.subsidiary.userClient.id).then(function(response) {
                vm.success = undefined;
                vm.error = undefined
                var number = parseInt(response);
                vm.client = {
                    country: 'Ecuador',
                    city: 'Quito',
                    codApp: number + 1,
                    userClient: vm.user.subsidiary.userClient
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientEdit = function(client) {
            vm.success = undefined;
            vm.error = undefined
            vm.client = angular.copy(client);
        };

        vm.validateEcuador = function() {
            if (vm.client.country !== 'Ecuador') {
                vm.client.city = undefined;
            };
        };

        vm.save = function() {
            var idValid = true;
            var validationError = utilStringService.isAnyInArrayStringEmpty([
                vm.client.name, vm.client.identification, vm.client.codApp
            ]);
            if (vm.client.typeId === 'CED') {
                idValid = validatorService.isCedulaValid(vm.client.identification);
            } else if (vm.client.typeId === 'RUC') {
                idValid = validatorService.isRucValid(vm.client.identification);
            } else if (vm.client.typeId === 'IEX') {
                idValid = true;
            };

            if (validationError) {
                vm.error = 'Debe ingresar Nombres completos, una identificacion y el Codigo de Contabilidad';
            } else if (!idValid) {
                vm.error = 'Identificacion invalida';
            } else {
                vm.loading = true;
                if (vm.client.id === undefined) {
                    create();
                } else {
                    update(false);
                };
            };
        };

        vm.remove = function() {
            vm.client.active = false;
            update(true);
        };

        vm.cancel = function() {
            vm.client = undefined;
            vm.success = undefined;
            vm.error = undefined;
            if ($routeParams.create) {
                $location.path('/bills/bill');
            };
        };

        vm.exit = function() {
            $location.path('/home');
        };

        vm.import = function() {
            var f = document.getElementById('file').files[0];
            if(f){
                var r = new FileReader();
                r.onload = function(event) {
                    var data = event.target.result;
                    var workbook = XLSX.read(data, {
                    type: "binary"
                    });
                    workbook.SheetNames.forEach(function(sheet) {
                        var clientes = XLSX.utils.sheet_to_row_object_array(
                        workbook.Sheets[sheet]
                        );
                        
                        _.each(clientes, function(newClient){
                            newClient.userClient = vm.user.subsidiary.userClient;
                            newClient.entryDate = 0;
                            newClient.dateCreated = new Date().getTime();
                            newClient.active = true;
                        })

                        clientService.createList(clientes).then(function(response) {
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
        };

        (function initController() {
            _activate();
        })();
});
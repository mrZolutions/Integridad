'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:UsersCtrl
 * @description
 * # UsersCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('UsersCtrl', function(_, utilStringService, userTypeService, authService, projectService, asociadoService,
                                      subsidiaryService, validatorService, cashierService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.userIntegridad = undefined;
        vm.project = undefined;
        vm.asociado = undefined;
        vm.bosses = [];
        vm.codeBosses = {
            EMP : 'ADM',
            ADM : 'SAD',
            SAD : 'SAD'
        };

        function _activate() {
            vm.loading = true;
            authService.getLazy().then(function(response) {
                vm.userList = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
            getProjects();
            getAociados();
        };

        function getAociados() {
            asociadoService.getLazy().then(function(response) {
                vm.asociadosList = _.sortBy(response, 'apellidos');
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function getProjects() {
            projectService.getLazy().then(function(response) {
                vm.projectList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function create() {
            vm.loading = true;
            authService.registerUser(vm.userIntegridad).then(function(response) {
                vm.loading = false;
                vm.error = undefined;
                vm.success = 'Registro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta';
                vm.userIntegridad = undefined;
                _activate();
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function update(isRemove) {
            vm.loading = true;
            authService.updateUser(vm.userIntegridad).then(function(response) {
                vm.loading = false;
                vm.error = undefined;
                if (isRemove) {
                    vm.success = 'Registro eliminado con exito';
                } else {
                    vm.success = 'Registro actualizado con exito';
                };
                vm.userIntegridad = undefined;
                _activate();
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getSubsidiaries = function() {
            vm.cashierList = undefined;
            subsidiaryService.getByProjectId(vm.project.id).then(function(response) {
                vm.subsidiaryList = response;
                vm.loading = false;

                if(vm.subsidiaryList.length === 1){
                    vm.userIntegridad.subsidiary = vm.subsidiaryList[0];
                    vm.getCashiers();
                }
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getBosses = function() {
            authService.getBosses(vm.codeBosses[vm.userIntegridad.userType.code],vm.userIntegridad.subsidiary.id).then(function(response) {
                vm.bosses = response;
            });
        };

        vm.getCashiers = function() {
            cashierService.getBySubsidiaryId(vm.userIntegridad.subsidiary.id).then(function(response) {
                vm.cashierList = response;
                vm.loading = false;

                if(vm.cashierList.length === 1){
                    vm.userIntegridad.cashier = vm.cashierList[0];
                    // id de EMPLEADOS
                    vm.userIntegridad.userType = _.find(vm.userTypes, function(usr){ return usr.id === "2c2fa78b-879a-4ef6-b701-00d25028f0fb"})
                    vm.getBosses()
                }
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.userCreate = function() {
            vm.success=undefined;
            vm.error=undefined;
            vm.userIntegridad = {};
        };

        vm.userEdit = function(user) {
            vm.success=undefined;
            vm.error=undefined
            vm.userIntegridad=angular.copy(user);
            if (vm.userIntegridad.cedula !== undefined && vm.userIntegridad.cedula !== null) {
                vm.typeId = 'Cedula';
            } else {
                vm.typeId = 'Ruc';
            };
            getProjects();
            vm.project = vm.userIntegridad.subsidiary.userClient;
            vm.getSubsidiaries();
            vm.getCashiers();
            vm.getBosses();
            $('#pickerBirthday').data("DateTimePicker").date(new Date(vm.userIntegridad.birthDay));
        };

        vm.register = function() {
            vm.userIntegridad.birthDay = $('#pickerBirthday').data("DateTimePicker").date().toDate().getTime();
            vm.userIntegridad.email = vm.userIntegridad.email.trim();
            vm.userIntegridad.password = utilStringService.randomString();

            var validationError = utilStringService.isAnyInArrayStringEmpty([
                vm.userIntegridad.email, vm.userIntegridad.password, vm.userIntegridad.firstName,
                vm.userIntegridad.lastName
            ]);

            if (validationError) {
                vm.error = 'Debe ingresar Nombres completos y un email';
            } else {
                validationError = utilStringService.isStringEmpty(vm.userIntegridad.cedula) && utilStringService.isStringEmpty(vm.userIntegridad.ruc);
                if (validationError) {
                    vm.error = 'Debe ingresar un numero de cedula o ruc';
                };
            };
            var idValid = true;
            if (vm.typeId === 'Cedula') {
                idValid = validatorService.isCedulaValid(vm.userIntegridad.cedula);
            } else if (vm.typeId === 'Ruc') {
                idValid = validatorService.isRucValid(vm.userIntegridad.ruc);
            };
            if (!idValid) {
                vm.error = 'Identificacion invalida';
            } else if (!validationError) {
                if (vm.userIntegridad.id === undefined) {
                    create();
                } else {
                    vm.userIntegridad.password = '';
                    update(false);
                };
            };
        };

        vm.remove = function() {
            vm.userIntegridad.active = false;
            update(true);
        };

        vm.cancel = function() {
            vm.userIntegridad = undefined;
            vm.success = undefined;
            vm.error = undefined
        };

        vm.userIntegridadResend = function(userIntegridad) {
            vm.loading = true;
            authService.recoverUser(userIntegridad).then(function(response) {
                vm.loading = false;
                vm.recover = false;
                vm.success = 'Se envio un email a ' + userIntegridad.email + ' con un nuevo Password para ingresar al sistema.';
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.setAsoValues = function() {
            if(vm.asociado){
                // id de TRANSPORTISTAS
                vm.project = _.find(vm.projectList, function(proj){ return proj.id === "e660b893-0c70-4985-b9d7-890938412ec4"})
                vm.userIntegridad.firstName = vm.asociado.nombres
                vm.userIntegridad.lastName = vm.asociado.apellidos
                vm.userIntegridad.celPhone = vm.asociado.telefono
                vm.typeId = 'Cedula'
                vm.userIntegridad.cedula = vm.asociado.ruc.slice(0, -3)
                vm.userIntegridad.email = vm.asociado.correo
                vm.userIntegridad.asociado = vm.asociado
                vm.userIntegridad.type = 'ASO'
                vm.getSubsidiaries();
            }
        };

        (function initController() {
            vm.loading = true;
            userTypeService.getUserTypes().then(function (response) {
                vm.userTypes =  response;
                vm.loading = false;
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
            _activate();
        })();
});
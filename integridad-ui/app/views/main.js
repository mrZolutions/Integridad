'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('MainCtrl', function ($rootScope, $location, authService, utilStringService, permissionService, holderService, $localStorage) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;

        vm.init = true;
        vm.recover = false;
        vm.userIntegridad = {};
        // vm.idTypes=[];

        vm.error = undefined;
        vm.success = undefined;

        function getPermissions() {
            var user = holderService.get();
            permissionService.getPermissions(user.userType).then(function(response) {
                console.log(response)
                console.log('*-*-*-*-*-*--*-*-*-*-*-*')
                var toGroup = response;
                // var grouped =_.groupBy(toGroup, 'moduleMenu.menuName');
                // console.log(grouped)

                var grouped =
                _
                .chain(toGroup)
                .groupBy('moduleMenu')
                .map(function(value, key) {
                    return {
                        type: key,
                        menu: value
                    }
                })
                .value();
                console.log(grouped)
                $localStorage.permissions = grouped;






                // $localStorage.permissions = response;
                $rootScope.updateMenu();
                vm.loading = false;
                $location.path('/home');
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.login = function() {
            vm.loading = true;
            var user = {email: vm.email, password: vm.password};
            authService.authUser(user).then(function(response) {
                // $localStorage.user = response;
                holderService.set(response);
                var userSaved = holderService.get();
                if (response.tempPass) {
                    vm.loading = false;
                    vm.passwordNotMatch = false;
                    vm.userIntegridad = angular.copy(userSaved.user);
                    $('#modalChangePassword').modal('show');
                } else {
                    var d = new Date();
                    $localStorage.timeloged = d.getTime();
                    getPermissions();
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.update = function() {
            vm.loading = true;
            vm.userIntegridad.password = vm.newPass;
            vm.userIntegridad.tempPass = false;
            authService.updateUser(vm.userIntegridad).then(function(response) {
                vm.error = undefined;
                vm.success = 'Perfil actualizado con exito';
                holderService.set(response);
                var d = new Date();
                $localStorage.timeloged = d.getTime();
                getPermissions();
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.validatePassword = function() {
            vm.passwordNotMatch = vm.newPass !== vm.passConfirmation;
        };

        vm.register = function() {
            vm.userIntegridad.birthDay = $('#pickerBirthday').data("DateTimePicker").date().toDate().getTime();
            vm.userIntegridad.email = vm.userIntegridad.email.trim();
            vm.userIntegridad.password = vm.userIntegridad.password.trim();
            var validationError = utilStringService.isAnyInArrayStringEmpty([
                vm.userIntegridad.email, vm.userIntegridad.password, vm.userIntegridad.firstName,
                vm.userIntegridad.lastName
            ]);

            if( validationError) {
                vm.error = 'Debe ingresar Nombres completos, email y password';
            } else {
                validationError = utilStringService.isStringEmpty(vm.userIntegridad.cedula) && utilStringService.isStringEmpty(vm.userIntegridad.ruc);
                if(validationError){
                    vm.error = 'Debe ingresar un numero de cedula o ruc';
                };
            };

            if (!validationError) {
                vm.loading = true;
                authService.registerUser(vm.userIntegridad).then(function(response) {
                    vm.loading = false;
                    vm.error = undefined;
                    vm.init = true;
                    vm.success = 'Registro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta';
                }).catch(function (error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            };
        };

        vm.recoverPass = function() {
            vm.loading = true;
            var user = {email: vm.email};
            authService.recoverUser(user).then(function(response) {
                vm.loading = false;
                vm.recover = false;
                vm.success = 'Se envio un email a la cuenta registrada con un nuevo Password para ingresar al sistema.';
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };
});
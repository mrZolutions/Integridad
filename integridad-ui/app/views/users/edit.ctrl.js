'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:UserEditCtrl
 * @description
 * # UserEditCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('UserEditCtrl', function($rootScope, utilStringService, $location, authService, holderService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.userIntegridad = angular.copy(holderService.get());
        vm.changePassword = false;
        vm.passwordNotMatch = false;

        function _activate() {
            $('#pickerBirthdayEdit').data("DateTimePicker").date(new Date(vm.userIntegridad.birthDay));
        };

        vm.validatePassword = function() {
            vm.passwordNotMatch = vm.newPass !== vm.passConfirmation;
        };

        vm.editUser = function() {
            if (vm.changePassword && !vm.passwordNotMatch) {
                vm.userIntegridad.password = vm.newPass;
            } else {
                vm.userIntegridad.password = '';
            };

            vm.userIntegridad.birthDay = $('#pickerBirthdayEdit').data("DateTimePicker").date().toDate().getTime();
            vm.userIntegridad.email = vm.userIntegridad.email.trim();

            var validationError = utilStringService.isAnyInArrayStringEmpty([
                vm.userIntegridad.email, vm.userIntegridad.firstName,
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

            if (!validationError) {
                vm.loading = true;
                authService.updateUser(vm.userIntegridad).then(function(response) {
                    vm.loading = false;
                    vm.error = undefined;
                    vm.success = 'Perfil actualizado con exito';
                    holderService.set(response)
                    $rootScope.updateMenu();
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            };
        };

        vm.cancel = function() {
            vm.userIntegridad = angular.copy(holderService.get());
            vm.success = undefined;
            vm.error = undefined
            vm.changePassword = false;
            vm.passwordNotMatch = false;
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
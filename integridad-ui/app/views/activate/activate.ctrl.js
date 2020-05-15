'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ActivateCtrl
 * @description
 * # ActivateCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('ActivateCtrl', function ($routeParams, $location, authService, permissionService, $rootScope, holderService, $localStorage) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.user = holderService.get();

        vm.errorActive = false;
        function getPermissions(){
            permissionService.getPermissions(vm.user.userType).then(function (response) {
                var dads = _.filter(response, function(item){ return item.permissionFather === null; });
                var sons = _.filter(response, function(item){ return item.permissionFather !== null; });

                sons.forEach(function(item){
                    var dad = _.findWhere(dads, {id: item.permissionFather});
                    dad.sons = dad.sons === undefined ? [item] : dad.sons.push(item);
                })

                var toGroup = dads;
                var grouped = {};
                toGroup.forEach(function(item){
                    var list = grouped[item.moduleMenu.id];
                    if(list){
                        list.push(item);
                    } else{
                        grouped[item.moduleMenu.id] = [item];
                    }
                });

                $localStorage.permissions = grouped;
                $rootScope.updateMenu();
                vm.loading = false;
                $location.path('/home');
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        }

        authService.activeUser($routeParams.idUSer, $routeParams.validate).then(function (response) {
            holderService.set(response)
            if(response.tempPass){
                vm.loading = false;
                vm.passwordNotMatch = false;
                vm.userIntegridad = angular.copy(holderService.get());
                $('#modalChangePassword').modal('show');
            } else {
                var d = new Date();
                $localStorage.timeloged = d.getTime();
                getPermissions();
            }
        }).catch(function (error) {
            vm.errorActive = true;
            vm.error = error.data;
        });

        vm.validatePassword = function () {
            vm.passwordNotMatch = vm.newPass !== vm.passConfirmation;
        };

        vm.update = function(){
            vm.loading = true;
            vm.userIntegridad.password = vm.newPass;
            vm.userIntegridad.tempPass = false;
            authService.updateUser(vm.userIntegridad).then(function (response) {
                vm.error = undefined;
                vm.success = 'Perfil actualizado con exito';
                holderService.set(response)
                vm.user = holderService.get();
                var d = new Date();
                $localStorage.timeloged = d.getTime();
                getPermissions();
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };
});
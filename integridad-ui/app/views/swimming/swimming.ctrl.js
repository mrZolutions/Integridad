'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:SwimmingCtrl
 * @description
 * # SwimmingCtrl
 * Controller of the integridadUiApp
 */

angular.module('integridadUiApp')
    .controller('SwimmingCtrl', function (_, $location, clientService, $localStorage, swimmingService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.client = undefined;
        vm.clientList = undefined;
        vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';

        function _activate() {
            vm.usrClntId = $localStorage.user.subsidiary.userClient.id;
            vm.clientSelected = undefined;
            vm.advertencia = undefined;
            vm.swimmList = undefined;
            vm.dateSwimmPool = undefined;
            vm.selectedTypeOption = undefined;
            vm.optionType = undefined;
            vm.newTicket = false;
            vm.loading = true;
            if (vm.usrClntId !== vm.laQuinta) {
                vm.advertencia = true;
                vm.selectedTypeOption = 'advert';
            };
            vm.loading = false;
        };

        vm.loadTypeOption = function(optionType) {
            vm.optionType = optionType;
            switch (vm.optionType) {
                case '1':
                    vm.selectedTypeOption = vm.optionType;
                    clientService.getLazyByUserClientId(vm.usrClntId).then(function(response) {
                        vm.clientList = response;
                        vm.loading = false;
                        setTimeout(function() {
                            document.getElementById("inputTP0").focus();
                        }, 200);
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                break;
            };
        };

        function _initializeTicket() {
            vm.swimmPool = {
                client: vm.clientSelected,
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                fecha: vm.dateSwimmPool.getTime(),
                stringSeq: vm.dateSwimmPool.getTime(),
                barCode: vm.dateSwimmPool.getTime(),
                subTotal: 0.0,
                iva: 0.0,
                total: 0.0
            };
        };

        vm.clientSelect = function(client) {
            vm.loading = true;
            vm.newTicket = true;
            vm.dateSwimmPool = new Date();
            vm.clientSelected = client;
            var today = new Date();
            //$('#pickerSwimmDate').data("DateTimePicker").date(today);
            _initializeTicket();
            vm.loading = false;
        };

        vm.cancel = function() {
            _activate();
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
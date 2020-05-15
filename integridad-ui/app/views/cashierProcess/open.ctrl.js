'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ClientsCtrl
 * @description
 * # ClientsCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp').controller('OpenCashierCtrl', function (processCashierService, $location, holderService) {

    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.processCashier = undefined;
    vm.details = undefined;
    vm.date = undefined;
    vm.denominacionSelected = undefined;
    vm.denominacionQuantity = undefined;
    vm.total = undefined;
    vm.currencies = ['US DOLAR'];
    vm.denominaciones = [0.01, 0.05, 0.10, 0.25, 0.50, 1.0, 5.0, 10.0, 20.0, 50.0, 100.0];

    function _activate() {
        vm.user = holderService.get()
        vm.details =  [];
        vm.date = new Date();
        vm.denominacionSelected = undefined;
        vm.denominacionQuantity = undefined;
        vm.total = 0;
        vm.processCashier = {
            user : vm.user,
            cashier : vm.user.cashier,
            date : vm.date.getTime(),
            currency : vm.currencies[0],
            type : 'APERTURA',
            code : vm.user.firstName.charAt(0) + vm.user.lastName.charAt(0) + Math.floor(Date.now() / 1000),
        };
    }

    vm.addDetail = function(){
        var detail = {
            denominacion: vm.denominacionSelected,
            quantity: vm.denominacionQuantity,
            subtotal: parseFloat(parseFloat(vm.denominacionQuantity) * parseFloat(vm.denominacionSelected)).toFixed(4),
        }
        vm.total = parseFloat(parseFloat(vm.total) + parseFloat(detail.subtotal)).toFixed(4);
        vm.denominacionSelected = undefined;
        vm.denominacionQuantity = undefined;
        vm.details.push(detail);
    };

    vm.removeDetail = function(index){
        vm.total = parseFloat(parseFloat(vm.total) - parseFloat(vm.details[index].subtotal)).toFixed(4);
        vm.details.splice(index,1);
    };

    vm.save = function(){
        vm.processCashier.details = vm.details;
        vm.processCashier.total = vm.total;
        processCashierService.save(vm.processCashier).then(function(response){
            document.getElementById("printBtnApertura").click();
            _activate();
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    }

    vm.salir = function() {
        $location.path('/home');
    };

    (function initController() {
        _activate();
    })();
});
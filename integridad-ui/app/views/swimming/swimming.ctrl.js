'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:SwimmingCtrl
 * @description
 * # SwimmingCtrl
 * Controller of the integridadUiApp
 */

angular.module('integridadUiApp')
    .controller('SwimmingCtrl', function (_, $location, $localStorage, swimmingService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.client = undefined;
        vm.clientList = undefined;
        vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';
        vm.userData = $localStorage.user;
        vm.totalTkAdulto = 5;
        vm.totalTkMenor = 3;
        vm.totalTk3raEdad = 3.5;

        function _activate() {
            vm.error = undefined;
            vm.usrClntId = $localStorage.user.subsidiary.userClient.id;
            vm.subsidiaryId = $localStorage.user.subsidiary.id;
            vm.swimmPool = undefined;
            vm.advertencia = undefined;
            vm.swimmList = undefined;
            vm.dateSwimmPool = undefined;
            vm.selectedTypeOption = undefined;
            vm.optionType = undefined;
            vm.newTicket = false;
            vm.fechaTk = undefined;
            vm.iva = 0;
            vm.subTotal = 0;
            vm.totalTicket = 0;
            vm.savedSwimmPool = false;
            vm.tipoClnt = undefined;
            vm.codTipoClnt = undefined;
            vm.status = undefined;
            vm.hoy = undefined;
            vm.ticketBarCode = undefined;
            vm.success = undefined;
            vm.loading = true;
            if (vm.usrClntId !== vm.laQuinta) {
                vm.advertencia = true;
                vm.selectedTypeOption = 'advert';
            };
            vm.loading = false;
        };

        function _initializeTicket() {
            vm.swimmPool = {
                userIntegridad: $localStorage.user,
                subsidiary: $localStorage.user.subsidiary,
                fecha: vm.dateSwimmPool.getTime(),
                stringSeq: vm.dateSwimmPool.getTime(),
                barCode: vm.dateSwimmPool.getTime(),
                codTipoClnt: vm.codTipoClnt,
                tipoClnt: vm.tipoClnt,
                status: 'NUEVO',
                subTotal: parseFloat(vm.subTotal),
                iva: parseFloat(vm.iva),
                total: parseFloat(vm.totalTicket)
            };
        };

        vm.loadTypeOption = function(optionType) {
            vm.optionType = optionType;
            switch (vm.optionType) {
                case '1':
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    vm.newTicket = true;
                    vm.dateSwimmPool = new Date();
                    vm.codTipoClnt = '1';
                    vm.tipoClnt = 'ADULTOS';
                    vm.totalTicket = parseFloat((vm.totalTkAdulto).toFixed(2));
                    vm.subTotal = parseFloat((vm.totalTicket / 1.12).toFixed(2));
                    vm.iva = parseFloat((vm.subTotal * 0.12).toFixed(2));
                    _initializeTicket();
                    setInterval(vm.saveSwimmPool(), 500);
                break;
                case '2':
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    vm.newTicket = true;
                    vm.dateSwimmPool = new Date();
                    vm.codTipoClnt = '2';
                    vm.tipoClnt = 'NIÑOS (AS)';
                    vm.totalTicket = parseFloat((vm.totalTkMenor).toFixed(2));
                    vm.subTotal = parseFloat((vm.totalTicket / 1.12).toFixed(2));
                    vm.iva = parseFloat((vm.subTotal * 0.12).toFixed(2));
                    _initializeTicket();
                    setInterval(vm.saveSwimmPool(), 500);
                break;
                case '3':
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    vm.newTicket = true;
                    vm.dateSwimmPool = new Date();
                    vm.codTipoClnt = '3';
                    vm.tipoClnt = 'TERCERA EDAD';
                    vm.totalTicket = parseFloat((vm.totalTk3raEdad).toFixed(2));
                    vm.subTotal = parseFloat((vm.totalTicket / 1.12).toFixed(3));
                    vm.iva = parseFloat((vm.subTotal * 0.12).toFixed(3));
                    _initializeTicket();
                    setInterval(vm.saveSwimmPool(), 500);
                break;
                case '4':
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    var today = new Date();
                    $('#pickerSwimmDate').data("DateTimePicker").date(today);
                break;
                case '5':
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    vm.hoy = new Date();
                    setTimeout(function() {
                        document.getElementById("inputTp1").focus();
                    }, 200);
                break;
            };
        };

        vm.getTicketSold = function() {
            var dateOne = $('#pickerSwimmDate').data("DateTimePicker").date().toDate().getTime();
            dateOne -= 6399000;
            var dateTwo = $('#pickerSwimmDate').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;
            swimmingService.getSwimmPoolBySubIdAndDates(vm.subsidiaryId, dateOne, dateTwo).then(function(response) {
                vm.swimmList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveSwimmPool = function() {
            vm.loading = true;
            swimmingService.createSwimmPool(vm.swimmPool).then(function(response) {
                vm.newTicket = false;
                vm.savedSwimmPool = true;
                vm.barcode = response.barCode;
                vm.swimmPool = response;
                $('#barcode').JsBarcode(vm.barcode + "", {format:'CODE128', displayValue:true, fontSize:15, textAlign:"center", textPosition:"bottom",
                                                          textMargin:2, fontSize:20, background:"#ffffff", lineColor:"#000000", margin:10});
                vm.success = 'TICKET CREADO';
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.consultTkSwimmPool = function(ticket) {
            vm.loading = true;
            vm.selectedTypeOption = '0';
            swimmingService.getSwimmPoolById(ticket.id).then(function(response) {
                vm.swimmList = undefined;
                vm.savedSwimmPool = true;
                vm.barcode = response.barCode;
                vm.swimmPool = response;
                $('#barcode').JsBarcode(vm.barcode + "", {format:'CODE128', displayValue:true, fontSize:15, textAlign:"center", textPosition:"bottom",
                                                          textMargin:2, fontSize:20, background:"#ffffff", lineColor:"#000000", margin:10});
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.filterTkBarCode = function(event) {
            if (event.keyCode === 32 || event.charCode === 32) {
                if (vm.ticketBarCode.length === 13) {
                    swimmingService.getSwimmPoolBySubIdAndBarCodeActive(vm.subsidiaryId, vm.ticketBarCode).then(function(response) {
                        if (response.length === 0) {
                            vm.status = undefined;
                        } else {
                            vm.swimmPool = response;
                            vm.hoy = new Date();
                            vm.fechaTkResp = parseInt(vm.ticketBarCode);
                            vm.fechaTk = new Date(vm.fechaTkResp);
            
                            var anyoHoy = vm.hoy.getFullYear();
                            var mesHoy = vm.hoy.getMonth() + 1;
                            var diaHoy = vm.hoy.getDate();
                
                            var anyoFechaTk = vm.fechaTk.getFullYear();
                            var mesFechaTk = vm.fechaTk.getMonth() + 1;
                            var diaFechaTk = vm.fechaTk.getDate();
                
                            if (anyoFechaTk < anyoHoy) {
                                vm.status = 'TICKET NO PUEDE SER USADO: FECHA CADUCADA';
                            } else {
                                if (anyoFechaTk == anyoHoy && mesFechaTk < mesHoy) {
                                    vm.status = 'TICKET NO PUEDE SER USADO: FECHA CADUCADA';
                                } else {
                                    if (anyoFechaTk == anyoHoy && mesFechaTk == mesHoy && diaFechaTk < diaHoy) {
                                        vm.status = 'TICKET NO PUEDE SER USADO: FECHA CADUCADA';
                                    } else {
                                        if (anyoFechaTk == anyoHoy && mesFechaTk == mesHoy && diaFechaTk == diaHoy) {
                                            vm.status = 'TICKET VALIDO PARA INGRESAR A PISCINA';
                                        } else {
                                            vm.status = 'TICKET VALIDO PERO NO PUEDE SER USADO: FECHA POSTERIOR AL DIA DE HOY';
                                        };
                                    };
                                };
                            };
                        };
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                };
            };
            setTimeout(function() {
                document.getElementById("btnSave").focus();
            }, 200);
        };

        vm.validateAndSaveSwimmPool = function() {
            vm.loading = true;
            swimmingService.deactivateSwimmPool(vm.subsidiaryId, vm.ticketBarCode).then(function(response) {
                vm.success = "TICKET VALIDADO";
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
            _activate();
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
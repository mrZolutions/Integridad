'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:SwimmingCtrl
 * @description
 * # SwimmingCtrl
 * Controller of the integridadUiApp
 */

angular.module('integridadUiApp')
    .controller('SwimmingCtrl', function (_, $location, clientService, $localStorage, swimmingService,
                                          validatorService, countryListService, utilStringService, projectService) {
        var vm = this;
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.client = undefined;
        vm.clientList = undefined;
        vm.laQuinta = '758dea84-74f5-4209-b218-9b84c10621fc';
        vm.countryList = countryListService.getCountryList();
        vm.citiesList = countryListService.getCitiesEcuador();
        vm.userData = $localStorage.user;
        vm.totalTkAdulto = 5;
        vm.totalTkMenor = 3;
        vm.totalTk3raEdad = 3.5;

        function _activate() {
            vm.error = undefined;
            vm.usrClntId = $localStorage.user.subsidiary.userClient.id;
            vm.subsidiaryId = $localStorage.user.subsidiary.id;
            vm.swimmPool = undefined;
            vm.clientId = undefined;
            vm.clientSelected = undefined;
            vm.advertencia = undefined;
            vm.swimmList = undefined;
            vm.dateSwimmPool = undefined;
            vm.selectedTypeOption = undefined;
            vm.optionType = undefined;
            vm.newTicket = false;
            vm.newClient = undefined;
            vm.fechaTk = undefined;
            vm.iva = 0;
            vm.subTotal = 0;
            vm.totalTicket = 0;
            vm.savedSwimmPool = false;
            vm.tipoClnt = undefined;
            vm.status = undefined;
            vm.hoy = undefined;
            vm.ticketBarCode = undefined;
            vm.swimmPoolId = undefined;
            var today = new Date();
            $('#pickerSwimmDate').data("DateTimePicker").date(today);
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
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    clientService.getLazyByUserClientId(vm.usrClntId).then(function(response) {
                        vm.clientList = response;
                        vm.loading = false;
                        setTimeout(function() {
                            document.getElementById("inputTp0").focus();
                        }, 200);
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                break;
                case '2':
                    vm.success = undefined;
                    vm.selectedTypeOption = vm.optionType;
                    vm.hoy = new Date();
                    setTimeout(function() {
                        document.getElementById("inputTp1").focus();
                    }, 200);
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
                clntName: vm.clientSelected.name,
                clntIdent: vm.clientSelected.identification,
                status: 'NUEVO',
                subTotal: 0,
                iva: 0,
                total: 0
            };
        };

        vm.clientSelect = function(client) {
            vm.loading = true;
            vm.newTicket = true;
            vm.dateSwimmPool = new Date();
            vm.clientSelected = client;
            _initializeTicket();
            vm.loading = false;
        };

        vm.clientTkConsult = function(client) {
            vm.loading = true;
            vm.clientId = client.id;
            vm.clientSelected = client;
            swimmingService.getSwimmPoolByClientId(vm.clientId).then(function(response) {
                vm.swimmList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.calcIvaAndSubTotal = function() {
            switch(vm.tipoClnt) {
                case 'adulto':
                    vm.totalTicket = parseFloat((vm.totalTkAdulto).toFixed(2));
                    vm.subTotal = parseFloat((vm.totalTicket / 1.12).toFixed(2));
                    vm.iva = parseFloat((vm.subTotal * 0.12).toFixed(2));
                break;
                case 'menor':
                    vm.totalTicket = parseFloat((vm.totalTkMenor).toFixed(2));
                    vm.subTotal = parseFloat((vm.totalTicket / 1.12).toFixed(2));
                    vm.iva = parseFloat((vm.subTotal * 0.12).toFixed(2));
                break;
                case 'edad3ra':
                    vm.totalTicket = parseFloat((vm.totalTk3raEdad).toFixed(2));
                    vm.subTotal = parseFloat((vm.totalTicket / 1.12).toFixed(3));
                    vm.iva = parseFloat((vm.subTotal * 0.12).toFixed(3));
                break;
            };
        };

        vm.createClient = function() {
            vm.newClient = true;
            projectService.getNumberOfProjects(vm.usrClntId).then(function(response) {
                vm.success = undefined;
                vm.error = undefined
                var number = parseInt(response);
                vm.client = {
                    country: 'Ecuador',
                    city: 'Quito',
                    codApp: number + 1,
                    userClient: $localStorage.user.subsidiary.userClient
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.saveClient = function() {
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
                createCli();
            };
        };

        function createCli() {
            clientService.getClientByUserClientAndIdentification(vm.usrClntId, vm.client.identification).then(function(response) {
                if (response.length === 0) {
                    clientService.create(vm.client).then(function(response) {
                        vm.newClient = undefined;
                        vm.error = undefined;
                        vm.success = 'Registro realizado con exito';
                        setInterval(_activate(), 500);
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

        vm.cancelCreateClient = function() {
            vm.newClient = undefined;
            setTimeout(function() {
                document.getElementById("inputTp0").focus();
            }, 200);
        };

        vm.saveSwimmPool = function() {
            vm.loading = true;
            vm.swimmPool.subTotal = vm.subTotal;
            vm.swimmPool.iva = vm.iva;
            vm.swimmPool.total = vm.totalTicket;
            vm.swimmPool.tipoClnt = vm.tipoClnt;
            swimmingService.createSwimmPool(vm.swimmPool).then(function(response) {
                vm.newTicket = false;
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

        vm.consultTkSwimmPool = function(ticket) {
            vm.loading = true;
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
        };

        vm.validateAndSaveSwimmPool = function() {
            vm.loading = true;
            swimmingService.deactivateSwimmPool(vm.subsidiaryId, vm.ticketBarCode).then(function(response) {
                vm.success = "Ticket Validado";
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
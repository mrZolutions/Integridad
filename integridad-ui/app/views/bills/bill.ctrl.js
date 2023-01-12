
'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:BillCtrl
 * @description
 * # BillCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
    .controller('BillCtrl', function(_, $location, holderService, consumptionService,
                                     clientService, productService, authService, billService, warehouseService,
                                     cashierService, requirementService, utilSeqService, configCuentasService) {
        var vm = this;
        vm.searchForced = '';
        vm.error = undefined;
        vm.success = undefined;
        vm.loading = false;
        vm.isEmp = true;
        vm.prices = [
            {name: 'EFECTIVO', cod: 'cashPercentage', disc: 'cashDiscount'}, {name: 'MAYORISTA', cod: 'majorPercentage', disc: 'cashDiscount'},
            {name: 'CREDITO', cod: 'creditPercentage', disc: 'cardDiscount'}, {name: 'TARJETA', cod: 'cardPercentage', disc: 'cardDiscount'}
        ];
        vm.medList = [
            {code: 'efectivo', name: 'Efectivo'},
            {code: 'cheque', name: 'Cheque'},
            {code: 'cheque_posfechado', name: 'Cheque posfechado'},
            {code: 'tarjeta_credito', name: 'Tarjeta de crédito'},
            {code: 'tarjeta_debito', name: 'Tarjeta de débito'},
            {code: 'dinero_electronico_ec', name: 'Dinero electrónico'},
            {code: 'credito', name: 'Crédito'},
            // {code: 'transferencia', name: 'Transferencia'}
        ];
        vm.formList = [
            '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO',
            '15 - COMPENSACION DE DEUDAS',
            '16 - TARJETAS DE DEBITO',
            '17 - DINERO ELECTRONICO',
            '18 - TARJETA PREPAGO',
            '19 - TARJETA DE CREDITO',
            '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO',
            '21 - ENDOSO DE TITULOS'
        ];
        vm.creditCardList = [
            'DINNERS CLUB',
            'VISA',
            'MASTERCARD',
            'AMERICAN'
        ];
        vm.seqChanged = false;

        function _activate() {
            vm.mrZolutions = '3566a41d-3260-49ba-9ee4-00c7d54e46b3';
            vm.dental = '8770552e-f64d-4947-95fd-c3c4ed04e5c4';
            vm.lozada = '1f9c21d7-a485-482b-b5eb-e363728340b2';
            vm.novapiel = '9fb035e0-d903-48b6-b36b-788d6f57a58a';
            vm.jhonservic = '68c74f50-39d6-4204-b607-14b098a50934';
            vm.escuela = 'ddd1e6d8-a6c6-4a6d-8074-a0d704e25fb5';
            vm.rimpeIds = [vm.mrZolutions, vm.lozada, vm.dental, vm.novapiel, vm.jhonservic, vm.escuela];

            vm.searchForced = '';
            vm.clienteAConsultar = undefined;
            vm.billListCopy = undefined;
            vm.advertencia = false;
            vm.clientList = undefined;
            vm.error = undefined;
            vm.aux = undefined;
            vm.estado = undefined;
            vm.newBill = true;
            vm.newConsumptionOn = true;
            vm.newBuy = true;
            vm.billed = false;
            vm.clientSelected = undefined;
            vm.dateBill = undefined;
            vm.seqNumber = undefined;
            vm.csmSeqNumber = undefined;
            vm.productList = undefined;
            vm.productToAdd = undefined;
            vm.pagoTot = undefined;
            vm.quantity = undefined;
            vm.userClientId = undefined;
            vm.adicional = undefined;
            vm.detailList = undefined;
            vm.indexDetail = undefined;
            vm.priceType = vm.prices[0];
            vm.seqChanged = false;
            vm.quotations = undefined;
            vm.impuestosTotales = [];
            vm.items = [];
            vm.impuestoICE = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "3",
                "codigo_porcentaje": "2"
            };
            vm.impuestoIVA = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "2",
                "codigo_porcentaje": "2"
            };
            vm.impuestoIVAZero = {
                "base_imponible": 0.0,
                "valor": 0.0,
                "codigo": "2",
                "codigo_porcentaje": "0"
            };
            vm.medio = {};
            vm.pagos = [];
            vm.user = holderService.get();
            vm.userId = vm.user.id;
            vm.userClientId = vm.user.subsidiary.userClient.id;
            vm.subsidiaryId = vm.user.subsidiary.id;
            vm.subOnlineActive = vm.user.subsidiary.online;
            vm.userCashier = vm.user.cashier;
            vm.userSubsidiary = vm.user.subsidiary;

            if (vm.subOnlineActive) {
                vm.loading = true;
                clientService.getLazyByUserClientId(vm.userClientId).then(function(response) {
                    vm.clientList = response;
                    var finalConsumer = _.filter(vm.clientList, function(client){ return client.identification === '9999999999999'});
                    vm.clientSelect(finalConsumer[0]);
                    _getSeqNumber();
                    _initializeBill();
                    _initializeConsumption();
                    var today = new Date();
                    $('#pickerBillDate').data("DateTimePicker").date(today);
                    setTimeout(function() {
                        document.getElementById("input4").focus();
                    }, 500);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.advertencia = true;
            };

            configCuentasService.getConfigCuentaByUserClientAndOptionCode(vm.userClientId, 'FACTEF').then(function (response){
                vm.cuentaContableEfectivo = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getComprobanteCobroSeqNumber() {
            vm.numbAddedOne = parseInt(vm.user.cashier.compCobroNumberSeq) + 1;
            vm.comprobanteCobroSeq = vm.numbAddedOne;
            vm.comprobanteCobroStringSeq = utilSeqService._pad_with_zeroes(vm.numbAddedOne, 6);
        };

        function _getDailyCiSeqNumber() {
            vm.numberAddedOneDailySeq = parseInt(vm.user.cashier.dailyCiNumberSeq) + 1;
            vm.dailyCiSeq = vm.numberAddedOneDailySeq;
            vm.dailyCiStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOneDailySeq, 6);
        };

        vm.getDetailsOfBills = function() {
            vm.loading = true;
            billService.getDetailsOfBillsByUserClientId(vm.userClientId).then(function(response) {
                vm.detailList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.volver = function() {
            _activate();
        };

        vm.nuevaBill = function() {
            _activate();
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt(vm.userCashier.billNumberSeq) + 1;
            vm.seqNumberFirstPart = vm.userSubsidiary.threeCode + '-' + vm.userCashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
        };

        function _initializeBill() {
            vm.bill = {
                client: vm.clientSelected,
                userIntegridad: vm.user,
                subsidiary: vm.user.subsidiary,
                dateCreated: vm.dateBill.getTime(),
                discount: 0,
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                details: [],
                pagos: [],
                observation: vm.rimpeIds.includes(vm.user.subsidiary.userClient.id) ? 'CONTRIBUYENTE REGIMEN RIMPE' : '', 
            };
        };

        vm.filterBarCode = function(){
            if(vm.billBarCode.length === 13){
                productService.getLazyBySusidiaryIdBarCode(vm.user.subsidiary.id, vm.billBarCode).then(function(response) {
                    if(!_.isEmpty(response)) {
                        vm.quantity = 1;
                        vm.toAdd = response[0];
                        vm.toAddExistency = _.last(vm.toAdd.productBySubsidiaries).quantity;
                        vm.toAddDiscount = vm.toAdd[vm.priceType.disc];
                        vm.toAddPrice = vm.getCost(vm.toAdd[vm.priceType.cod], vm.toAdd.averageCost, vm.toAdd[vm.priceType.disc]);
                        setTimeout(function() {
                            var input = document.getElementById("productQuantity");
                            input.focus();
                            input.select();
                        }, 200);
                    };
                });
            };
        };

        vm.clientSelect = function(client) {
            vm.quotations = [];
            vm.companyData = vm.userSubsidiary;
            vm.dateBill = new Date();
            vm.clientSelected = client;
            vm.pagos = [];
            setTimeout(function() {
                document.getElementById("input4").focus();
            }, 500);
        };

        vm.clientSelectChanged = function(client) {
            vm.clienteAConsultar = undefined;
            vm.clientSelected = client;
            vm.bill.client = vm.clientSelected;
            vm.consumption.client = vm.clientSelected;
            vm.pagos = [];
            setTimeout(function() {
                document.getElementById("input4").focus();
            }, 500);
        };

        vm.clientConsult = function(client) {
            vm.loading = true;
            vm.clienteAConsultar = client;
            billService.getAllBillsByClientId(client.id).then(function(response) {
                vm.billListCopy = angular.copy(response);
                vm.billList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.clientChange = function() {
            vm.clientSelected = undefined;
            setTimeout(function() {
                document.getElementById("inputCl0").focus();
            }, 200);
        };

        function _getTotalSubtotal() {
            vm.bill.discount = 0;
            vm.bill.subTotal = 0.0;
            vm.bill.iva = 0.0;
            vm.bill.ivaZero = 0.0;
            vm.bill.ice = 0.0;
            vm.bill.baseTaxes = 0.0;
            vm.bill.baseNoTaxes = 0.0;
            //Calculos para Consumos
            vm.consumption.subTotal = 0;
            vm.consumption.iva = 0;
            vm.consumption.ivaZero = 0;
            vm.consumption.ice = 0;
            vm.consumption.baseTaxes = 0;
            vm.consumption.baseNoTaxes = 0;

            var discountWithIva = 0.0;
            var discountWithNoIva = 0.0;
            _.each(vm.bill.details, function(detail) {
                vm.bill.subTotal = parseFloat((parseFloat(vm.bill.subTotal) + parseFloat(detail.total)).toFixed(4));
                vm.consumption.subTotal = vm.bill.subTotal;
                var tot = parseFloat((detail.total).toFixed(4));
                if (vm.bill.discountPercentage) {
                    tot = parseFloat((detail.total).toFixed(4));
                };
                if (detail.product.iva) {
                    vm.bill.baseTaxes += parseFloat((detail.total).toFixed(4));
                    vm.consumption.baseTaxes = vm.bill.baseTaxes;
                    vm.bill.iva = parseFloat((parseFloat(vm.bill.iva) + (parseFloat(tot) * 0.12)).toFixed(4));
                    vm.consumption.iva = vm.bill.iva;
                    if (vm.bill.discountPercentage) {
                        discountWithIva = parseFloat((parseFloat(discountWithIva) + (parseFloat(detail.total) * parseFloat((vm.bill.discountPercentage) / 100))).toFixed(4));
                    };
                } else {
                    vm.bill.baseNoTaxes += parseFloat((detail.total).toFixed(4));
                    vm.consumption.baseNoTaxes = vm.bill.baseNoTaxes;
                    vm.bill.ivaZero = parseFloat((parseFloat(vm.bill.ivaZero) + parseFloat(tot)).toFixed(4));
                    vm.consumption.ivaZero = vm.bill.ivaZero;
                    if (vm.bill.discountPercentage) {
                        discountWithNoIva = parseFloat((parseFloat(discountWithNoIva) + ((parseFloat(vm.bill.discountPercentage) / 100) * parseFloat(detail.total))).toFixed(4));
                    };
                };
                if (detail.product.ice) {
                    vm.bill.ice = parseFloat((parseFloat(vm.bill.ice) + (parseFloat(tot) * 0.10)).toFixed(4));
                };
                if(detail.discountValue) vm.bill.discount += parseFloat(detail.discountValue);
            });

            vm.impuestoICE.base_imponible = parseFloat((vm.bill.subTotal).toFixed(4));
            vm.impuestoIVA.base_imponible = parseFloat((vm.bill.baseTaxes).toFixed(4));
            vm.impuestoIVAZero.base_imponible = parseFloat((vm.bill.baseNoTaxes).toFixed(4));
            vm.impuestoICE.valor = vm.bill.ice;
            vm.impuestoIVA.valor = vm.bill.iva;
            vm.impuestoIVAZero.valor = 0.0;
            vm.bill.baseTaxes = parseFloat((vm.bill.baseTaxes).toFixed(4));
            vm.bill.baseNoTaxes = parseFloat((vm.bill.baseNoTaxes).toFixed(4));
            vm.bill.total = parseFloat((parseFloat(vm.bill.baseTaxes)
                +  parseFloat(vm.bill.baseNoTaxes)
                +  parseFloat(vm.bill.iva)
                +  parseFloat(vm.bill.ice)).toFixed(2));
            vm.consumption.total = (parseFloat(vm.consumption.baseTaxes)
                + parseFloat(vm.consumption.baseNoTaxes)
                + parseFloat(vm.consumption.iva)).toFixed(2);
        };

        function _addDays(date, days) {
            var result = new Date(date);
            result.setDate(result.getDate() + days);
            return result.getTime();
        };

        vm.acceptNewSeq = function() {
            vm.seqErrorNumber = undefined;
            vm.loading = true;
            var stringSeq =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
            billService.getByStringSeq(stringSeq, vm.companyData.id).then(function(response) {
                if (response.length === 0) {
                    vm.seqNumber = stringSeq;
                    vm.numberAddedOne = parseInt(vm.seqNumberSecondPart);
                    vm.seqChanged = true;
                    $('#modalEditBillNumber').modal('hide');
                } else {
                    vm.seqErrorNumber = "NUMERO DE FACTURA YA EXISTENTE"
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.reCalculateTotal = function() {
            if (vm.bill.discountPercentage == null || vm.bill.discountPercentage == undefined || vm.bill.discountPercentage == '') {
                vm.bill.discountPercentage = 0;
            };
            _.map(vm.bill.details, function(detail) {
                detail.discount = parseFloat(vm.bill.discountPercentage) + parseFloat(detail.product[vm.priceType.disc]);
                var costEachCalculated = vm.getCost(detail.product[vm.priceType.cod], detail.product.averageCost, detail.product[vm.priceType.disc]);
                detail.costEach = costEachCalculated;
                var discountValue =  (parseFloat(detail.costEach) * parseFloat(detail.discount / 100)).toFixed(4);
                detail.discountValue = discountValue;
                detail.total = parseFloat((parseFloat(detail.quantity) * (parseFloat(detail.costEach) - discountValue)).toFixed(4));
            });
            _getTotalSubtotal();
        };

        vm.clientCreate = function() {
            $location.path('/clients/create');
        };

        function _filterProduct() {
            vm.totalPages = 0;
            var variable = vm.searchText? vm.searchText : null;
            if (variable == null) {
                var busqueda = variable;
            } else {
                var busqueda = variable.toUpperCase();
            };
            productService.getLazyBySusidiaryIdForBill(vm.user.subsidiary.id, vm.page, busqueda).then(function(response) {
                vm.loading = false;
                vm.totalPages = response.totalPages;
                vm.productList = [];
                for (var i = 0; i < response.content.length; i++) {
                    var productFound = _.find(vm.bill.details, function(detail) {
                        return detail.product.id === response.content[i].id;
                    });
                    if (productFound === undefined) {
                        var sub = _.find(response.content[i].productBySubsidiaries, function(s) {
                            return (s.subsidiary.id === vm.user.subsidiary.id && s.active === true);
                        });
                        if (sub) {
                            response.content[i].quantity = sub.quantity
                            vm.productList.push(response.content[i]);
                        };
                    };
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.filterEvent = function(event) {
            vm.page = 0;
            if (event.keyCode === 13 || event.charCode === 13) {
                _filterProduct();
            };
        };

        vm.filter = function() {
            vm.page = 0;
            _filterProduct();
        };

        vm.paginate = function(page) {
            vm.page = page;
            _filterProduct();
        };

        vm.getActiveClass = function(index) {
            var classActive = vm.page === index? 'active' : '';
            return classActive;
        };

        vm.range = function() {
            return new Array(vm.totalPages);
        };

        vm.addProduct = function() {
            vm.indexDetail = undefined;
            vm.loading = true;
            vm.errorQuantity = undefined;
            vm.page = 0;
            vm.searchText = undefined;
            setTimeout(function() {
                document.getElementById("prod011").focus();
            }, 200);
            _filterProduct();
        };

        vm.selectProductToAdd = function(productSelect) {
            if (vm.quantity == 0 || vm.quantity == undefined) {
                vm.quantity = 1;
            };
            if (productSelect.productType.code === 'SER') {
                productSelect.quantity = 1;
            };
            vm.productToAdd = angular.copy(productSelect);
            var costEachCalculated = vm.getCost(productSelect[vm.priceType.cod], productSelect.averageCost, productSelect[vm.priceType.disc]);
            vm.toAddDiscount = productSelect[vm.priceType.disc];
            vm.productToAdd.costEachCalculated = costEachCalculated;
        };

        vm.addProdBarCode = function() {
            if (vm.toAdd !== undefined) {
                vm.productToAdd = angular.copy(vm.toAdd);
                vm.selectProductToAdd(vm.productToAdd);
                vm.acceptProduct(true);
                vm.billBarCode = undefined;
                vm.toAdd = undefined;
            };
        };

        vm.cancelBarCode = function(){
            vm.billBarCode = undefined;
            vm.toAdd = undefined;
            vm.quantity = undefined;
        };

        vm.openModalPayMode = function(){
            $('#modalAddPago').modal('show');
                vm.medio = vm.medList[0];
                vm.loadMedio();
                var elementToSelect = document.getElementById("medioTotal");
                setTimeout(function() {
                    elementToSelect.focus();
                    elementToSelect.select();
                }, 500);
        }

        vm.openPayMode = function(event){
            if (event.keyCode === 102 || event.charCode === 102 || event.keyCode === 70 || event.charCode === 70) {
                vm.openModalPayMode();
            };
        };

        vm.fill = function(event){
            if (event.keyCode === 32 || event.charCode === 32) {
                if (vm.billBarCode.length < 13) {
                    vm.billBarCodeFixed = vm.billBarCode;
                    for (var i = vm.billBarCode.length; i < 13; i++) {
                        vm.billBarCodeFixed = vm.billBarCodeFixed.concat('0');
                    };
                    vm.billBarCode = vm.billBarCodeFixed.trim();
                    vm.filterBarCode();
                };
            };
        };

        vm.getTextCambio = function(){
            return vm.getCambio <= 0 ? 0 : vm.getCambio;
        };

        vm.acceptProduct = function(closeModal) {
            vm.errorQuantity = undefined;
            if (vm.bill.discountPercentage == null || vm.bill.discountPercentage == undefined) {
                vm.bill.discountPercentage = 0;
            };
            var discountDetail = vm.toAddDiscount ? parseFloat(vm.toAddDiscount) + parseFloat(vm.bill.discountPercentage) : parseFloat(vm.bill.discountPercentage);
            var discountValue = (vm.quantity * (vm.productToAdd.costEachCalculated) * (discountDetail / 100));
            var detail = {
                discountData: vm.toAddDiscount ? vm.toAddDiscount : 0,
                discount: discountDetail,
                product: angular.copy(vm.productToAdd),
                quantity: vm.quantity,
                costEach: vm.productToAdd.costEachCalculated,
                discountValue :  discountValue,
                total: parseFloat(((vm.quantity * vm.productToAdd.costEachCalculated) - discountValue).toFixed(4)),
                adicional: vm.adicional
            };
            if (vm.indexDetail !== undefined) {
                vm.bill.details[vm.indexDetail] = detail;
                vm.consumption.detailsConsumption[vm.indexDetail] = detail;
            } else {
                vm.bill.details.push(detail);
                vm.consumption.detailsConsumption.push(detail);
            };
            vm.productToAdd = undefined;
            vm.indexDetail = undefined;
            vm.quantity = undefined;
            vm.adicional = undefined;
            _getTotalSubtotal();
            if (closeModal) {
                $('#modalAddProduct').modal('hide');
                vm.toAdd = undefined;
                vm.toAddExistency = undefined;
                vm.toAddPrice = undefined;
                vm.billBarCode = undefined;
                document.getElementById("input4").focus();
            } else {
                var newProductList = _.filter(vm.productList, function(prod) {
                    return prod.id !== detail.product.id;
                });
                vm.productList = newProductList;
            };
            vm.toAddDiscount = undefined;
        };

        vm.getCost = function(textCost, averageCost, discount = 0) {
            var aC = 1 + ((parseFloat(textCost)) / 100);
            var discountC = (parseFloat(discount)) / 100;
            var cost = aC * averageCost;
            var costTotal = cost - (cost * discountC).toFixed(2);
            // return parseFloat(costTotal.toFixed(4));
            return parseFloat(cost.toFixed(4));
        };

        vm.editDetail = function(detail, index) {
            vm.indexDetail = index;
            vm.productToAdd = detail.product;
            vm.toAddDiscount = detail.discountData
            vm.quantity = detail.quantity;
            vm.adicional = detail.adicional;
            setTimeout(function() {
                document.getElementById("prod011").focus();
            }, 200);
            _getTotalSubtotal();
        };

        vm.removeDetail = function(index) {
            vm.bill.details.splice(index,1);
            vm.consumption.detailsConsumption.splice(index,1);
            _getTotalSubtotal();
        };

        vm.validateAdm = function() {
            vm.errorValidateAdm = undefined;
            var userAdm = vm.user.user;
            userAdm.password = vm.passwordAdm;
            authService.authUser(userAdm).then(function(response) {
                vm.loading = false;
                vm.isEmp = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.errorValidateAdm = error.data;
            });
        };

        vm.verifyUser = function() {
            vm.isEmp = vm.user.userType.code === 'EMP';
        };

        vm.loadMedio = function() {
            var payed = 0;
            _.each(vm.pagos, function(pago) {
                payed += parseFloat(pago.total);
            });
            if (vm.medio.code === 'efectivo' || vm.medio.code === 'dinero_electronico_ec' || vm.medio.code === 'transferencia') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
                // CAMBIO SRI POR CONFIRMAR
                // vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
            };
            if (vm.medio.code === 'credito') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                // CAMBIO SRI POR CONFIRMAR
                // vm.medio.payForm = '01 - SIN UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
                if (vm.priceType.name === 'CREDITO') {
                    vm.medio.statusPago = 'PENDIENTE';
                } else {
                    vm.medio.statusPago = 'PAGADO';
                };
            };
            if (vm.medio.code === 'cheque' || vm.medio.code === 'cheque_posfechado') {
                vm.medio.payForm = '20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO';
                vm.medio.statusPago = 'PAGADO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
            };
            if (vm.medio.code === 'tarjeta_credito' || vm.medio.code === 'tarjeta_debito') {
                vm.medio.payForm = '19 - TARJETA DE CREDITO';
                vm.medio.total = parseFloat((vm.bill.total - payed).toFixed(4));
                vm.medio.statusPago = 'PAGADO';
            };
        };

        vm.loadCredit = function() {
            var creditArray = [];
            var diasPlazo = parseInt(vm.medio.creditoIntervalos);
            var d = new Date();
            var statusCredits = 'PENDIENTE';
            var total = parseFloat((vm.bill.total / vm.medio.creditoNumeroPagos).toFixed(4));
            vm.seqNumberCredits = vm.seqNumber;
            vm.estado = statusCredits;
            for (var i = 1; i <= parseInt(vm.medio.creditoNumeroPagos); i++) {
                var credito = {
                    payNumber: i,
                    diasPlazo: diasPlazo,
                    fecha: _addDays(d, diasPlazo),
                    statusCredits: statusCredits,
                    documentNumber: vm.seqNumberCredits,
                    valor: total
                };
                diasPlazo += parseInt(vm.medio.creditoIntervalos);
                creditArray.push(credito);
            };
            vm.medio.credits = creditArray;
            vm.pagoTot = credito.total;
        };

        vm.getFechaCobro = function() {
            var d = new Date();
            vm.medio.fechaCobro = _addDays(d, parseInt(vm.medio.chequeDiasPlazo));
        };

        vm.addPago = function() {
            if(vm.medio.code === 'efectivo'){
                var credit = {
                    payNumber: 1,
	                diasPlazo: 1,
	                fecha: new Date().getTime(),
	                statusCredits: "PAGADO",
	                documentNumber: vm.seqNumber,
	                valor: vm.bill.total,
                };
                vm.medio.creditoNumeroPagos= 1,
                vm.medio.creditoIntervalos= 1,
                vm.medio.credits=[credit];
            }
            vm.pagos.push(angular.copy(vm.medio));
            vm.medio = {};
            setTimeout(function(){
                document.getElementById("processBillBtn").focus();
            }, 500);
        };

        vm.removePago = function(index) {
            vm.pagos.splice(index, 1);
        };

        vm.getTotalPago = function() {
            vm.aux = 0;
            vm.varPago = 0;
            if (vm.bill) {
                vm.getCambio = 0;
                _.each(vm.pagos, function(med) {
                    vm.varPago = parseFloat(parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2);
                });
                vm.getCambio = (vm.varPago - vm.bill.total).toFixed(2);
                vm.aux = (vm.varPago - vm.getCambio).toFixed(2);
            };
            return vm.varPago;
        };

        vm.getTotalPagoB = function() {
            vm.aux = 0;
            vm.varPago = 0;
            if (vm.bill) {
                vm.getCambio = 0;
                _.each(vm.pagos, function(med) {
                    vm.varPago = parseFloat((parseFloat(vm.varPago) + parseFloat(med.total)).toFixed(2));
                });
                vm.getCambioB = parseFloat((vm.varPago - vm.bill.total).toFixed(2));
                vm.aux = parseFloat((vm.varPago - vm.getCambio).toFixed(2));
            };
            return vm.varPago;
        };

        vm.billSelect = function(bill) {
            vm.loading = true;
            billService.getById(bill.id).then(function(response) {
                vm.detailList = undefined;
                vm.billList = undefined;
                vm.bill = response;
                vm.companyData = vm.user.subsidiary;
                vm.clientSelected = response.client;
                vm.pagos = response.pagos;
                var dateToShow = new Date(response.dateCreated);
                vm.seqNumber = response.stringSeq;
                $('#pickerBillDate').data("DateTimePicker").date(dateToShow);
                vm.newBill = false;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelBill = function() {
            _activate();
        };

        vm.getDateToPrint = function() {
            if (vm.bill != undefined) {
                return $('#pickerBillDate').data("DateTimePicker").date().toDate();
            };
        };

        vm.printToCartAndCancel = function(printBillId) {
            var innerContents = document.getElementById(printBillId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillId', 'width=400,height=500');
            popupWinindow.document.write('<html><head><title>printMatrixBillId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
            _activate();
        };

        vm.printToCart = function(printBillId) {
            var innerContents = document.getElementById(printBillId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillId', 'width=400,height=500');
            popupWinindow.document.write('<html><head><title>printMatrixBillId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
        };

        vm.printToCartTwo = function(printBillId) {
            var innerContents = document.getElementById(printBillId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixBillId', 'width=272,height=1000');
            popupWinindow.document.write('<html><head>');
            popupWinindow.document.write('</head><body style="font-family: Arial, Helvetica, sans-serif; font-size:medium; width: 100vw;">');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
        };

        vm.downloadBillTxtTM20 = function(billToDownloadEpsonTM20) {
            var b = document.body.appendChild(document.createElement("b"));
            b.href = "data:text/plain; charset=utf-8," + document.getElementById(billToDownloadEpsonTM20).innerText;
            b.download = "billDownloaded.txt";
            b.click();
            document.body.removeChild(b);
        };

        vm.billDeactivate = function() {
            vm.loading = true;
            var index = vm.billList.indexOf(vm.cancelaBill);
            if (vm.cancelaBill.creditNoteApplied === false) {
                billService.cancelBill(vm.cancelaBill).then(function(response) {
                    var index = vm.billList.indexOf(vm.cancelaBill);
                    if (index > -1) {
                        vm.billList.splice(index, 1);
                    };
                    vm.cancelaBill = undefined;
                    vm.success = "Factura de Venta Anulada";
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.loading = false;
                vm.error = "La Factura NO se Puede Anular debido a que presenta una Nota de Crédito";
            };
        };

        vm.getQuotations = function() {
            vm.loading = true;
            billService.getQuotationsByClientId(vm.clientSelected.id).then(function(response) {
                vm.quotations = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.selectQuotation = function(quotation) {
            vm.loading = true;
            billService.getById(quotation.id).then(function(response) {
                vm.bill = response;
                vm.bill.id = undefined;
                vm.bill.claveDeAcceso = undefined;

                _.each(vm.bill.details, function(item) {
                    vm.consumption.detailsConsumption.push(item);
                });
                _getTotalSubtotal();
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.getClaveAcceso = function() {
            vm.loading = true;
            vm.bill.dateCreated = $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime()
            $('#modalAddPago').modal('hide');
            vm.impuestosTotales = [];
            vm.bill.detailsKardex = [];
            if (vm.bill.observation === null || vm.bill.observation === undefined || vm.bill.observation === '') {
                vm.bill.observation = '--';
            };
            if (vm.impuestoIVA.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVA);
            };
            if (vm.impuestoIVAZero.base_imponible > 0) {
                vm.impuestosTotales.push(vm.impuestoIVAZero);
            };
            vm.bill.billSeq = vm.numberAddedOne;
            if (vm.bill.discountPercentage === undefined) {
                vm.bill.discountPercentage = 0;
            };
            _.each(vm.bill.details, function(det) {
                var costWithIva = parseFloat((det.total * 1.12).toFixed(4));
                var costWithIce = parseFloat((det.total * 1.10).toFixed(4));
                var impuestos = [];
                var impuesto = {};
                var detaAdic = {
                    "det": det.adicional
                };

                if (det.product.iva) {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.12).toFixed(4));
                    impuesto.tarifa = 12.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                } else {
                    impuesto.base_imponible = parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4));
                    impuesto.valor = parseFloat((parseFloat(impuesto.base_imponible) * 0.0000).toFixed(4));
                    impuesto.tarifa = 0.0;
                    impuesto.codigo = '2';
                    impuesto.codigo_porcentaje = '0';
                    impuestos.push(impuesto);
                };

                if (det.product.ice) {
                    impuesto.base_imponible = parseFloat((det.costEach).toFixed(4));
                    impuesto.valor = costWithIce;
                    impuesto.tarifa = 10.0;
                    impuesto.codigo = '3';
                    impuesto.codigo_porcentaje = '2';
                    impuestos.push(impuesto);
                };

                var item = {
                    "cantidad": det.quantity,
                    "codigo_principal": det.product.codeIntegridad,
                    "codigo_auxiliar": det.product.barCode,
                    "precio_unitario": parseFloat(det.costEach),
                    "descripcion": det.product.name,
                    "precio_total_sin_impuestos": parseFloat(((parseFloat(det.costEach) - (parseFloat(det.costEach) * parseFloat((vm.bill.discountPercentage / 100)))) * parseFloat(det.quantity)).toFixed(4)),
                    "descuento": parseFloat(((det.quantity * det.costEach) * parseFloat((vm.bill.discountPercentage) / 100)).toFixed(4)),
                    "unidad_medida": det.product.unitOfMeasurementFull,
                    "detalles_adicionales": detaAdic
                };

                if (!_.isEmpty(impuestos)) {
                    item.impuestos = impuestos;
                };

                vm.items.push(item);

                var kardex = {
                    bill: vm.bill.id,
                    product: det.product,
                    codeWarehouse: '--',
                    dateRegister: $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime(),
                    details: 'FACTURA-VENTA Nro. ' + vm.seqNumber,
                    observation: 'EGRESO',
                    detalle: '--',
                    prodCostEach: det.costEach,
                    prodName: det.product.name,
                    prodQuantity: det.quantity,
                    prodTotal: det.total,
                    subsidiaryId: vm.subsidiaryId,
                    userClientId: vm.userClientId,
                    userId: vm.userId
                };

                vm.bill.detailsKardex.push(kardex);
            });
            vm.bill.stringSeq = vm.seqNumber;
            vm.bill.priceType = vm.priceType.name;
            if (vm.estado === 'PENDIENTE') {
                vm.bill.saldo = (vm.bill.total).toString();
            } else {
                vm.bill.saldo = '0';
            };

            vm.bill.pagos = vm.pagos;
            if (vm.bill.discountPercentage === undefined) {
                vm.bill.discountPercentage = 0;
            };

            var req = requirementService.createRequirement(vm.clientSelected, vm.bill, vm.user, vm.impuestosTotales, vm.items, vm.pagos);
            if(vm.user.apiConnection) req.logo = vm.companyData.userClient.logo;
            var reqBill = {requirement : req, bill: vm.bill}

            vm.comprobanteCobro = {};
            vm.dailybookCi = {};

            if(vm.pagos.length === 1){
                if(vm.pagos[0].code === 'efectivo'){
                    vm.comprobanteCobro = {
                        client: vm.clientSelected,
                        userIntegridad: vm.user,
                        subsidiary: vm.user.subsidiary,
                        clientName: vm.clientSelected.name,
                        clientRuc: vm.clientSelected.identification,
                        subTotalDoce: 0,
                        iva: 0,
                        total: 0,
                        detailComprobanteCobro: []
                    };
                    
                    vm.itemBill = {
                        numCheque: '-',
                        cuenta: '-',
                        banco: '-',
                        tipoAbono: 'EFC',
                        totalAbono: vm.bill.total,
                        billNumber: vm.bill.stringSeq,
                        dateBill: vm.bill.dateCreated
                    };
        
                    _getComprobanteCobroSeqNumber();
        
                    vm.comprobanteCobro.detailComprobanteCobro.push(vm.itemBill);
                    vm.comprobanteCobro.billNumber = vm.bill.stringSeq;
                    vm.comprobanteCobro.dateComprobante = vm.bill.dateCreated;
                    vm.comprobanteCobro.dateComprobanteCreated = vm.bill.dateCreated;
                    vm.comprobanteCobro.comprobanteSeq = vm.comprobanteCobroSeq;
                    vm.comprobanteCobro.comprobanteStringSeq = vm.comprobanteCobroStringSeq;
                    vm.comprobanteCobro.comprobanteConcep = 'Cancela Fact. ' + vm.bill.stringSeq;
                    vm.comprobanteCobro.comprobanteEstado = 'PROCESADO';
                    vm.comprobanteCobro.total = vm.bill.total;
                    vm.comprobanteCobro.subTotalDoce = parseFloat((vm.bill.total / 1.12).toFixed(2));
                    vm.comprobanteCobro.iva = parseFloat((vm.bill.total * 0.12).toFixed(2));
                    vm.comprobanteCobro.paymentId = '-';

                    vm.dailybookCi = {
                        client: vm.clientSelected,
                        userIntegridad: vm.user,
                        subsidiary: vm.user.subsidiary,
                        clientProvName: vm.clientSelected.name,
                        subTotalDoce: 0,
                        iva: 0,
                        subTotalCero: 0,
                        total: 0,
                        detailDailybookContab: []
                    };
                    _getDailyCiSeqNumber();
                    vm.selectedTypeBook = '3';
                    vm.generalDetailCi_1 = vm.clientSelected.name + ' Cancela Facts. ' + vm.bill.stringSeq;
                    vm.typeContabCi = 'COMP. DE INGRESO';
                    vm.itema = {
                        typeContab: vm.typeContabCi,
                        codeConta: vm.clientSelected.codConta,
                        descrip: 'CLIENTES NO RELACIONADOS',
                        tipo: 'CREDITO (C)',
                        baseImponible: parseFloat(vm.bill.total),
                        name: vm.generalDetailCi_1,
                        haber: parseFloat(vm.bill.total)
                    };
                    vm.itema.numCheque = '--';
                    vm.itema.dailybookNumber = vm.dailyCiStringSeq;
                    vm.itema.userClientId = vm.userClientId;
                    vm.itema.dateDetailDailybook = vm.bill.dateCreated;
                    vm.dailybookCi.detailDailybookContab.push(vm.itema);
                    vm.generalDetailCi_2 = 'Cobro de Facts. ' + vm.bill.stringSeq + ' en EFECTIVO';
                    // Todo DEFINIR CAMPOS DE CODE CONTA Y DESCRIPCION
                    vm.itemb = {
                        typeContab: vm.typeContabCi,
                        codeConta: vm.cuentaContableEfectivo ? vm.cuentaContableEfectivo.code : '--',
                        descrip: vm.cuentaContableEfectivo ? vm.cuentaContableEfectivo.description : '--',
                        tipo: 'DEBITO (D)',
                        baseImponible: parseFloat(vm.bill.total),
                        name: vm.generalDetailCi_2,
                        deber: parseFloat(vm.bill.total)
                    };
                    vm.itemb.numCheque = '--';
                    vm.itemb.dailybookNumber = vm.dailyCiStringSeq;
                    vm.itemb.userClientId = vm.userClientId;
                    vm.itemb.dateDetailDailybook = vm.bill.dateCreated;
                    vm.dailybookCi.detailDailybookContab.push(vm.itemb);
                    vm.dailybookCi.codeTypeContab = vm.selectedTypeBook;
                    vm.dailybookCi.nameBank = '--';
                    vm.dailybookCi.billNumber = vm.bill.stringSeq;
                    vm.dailybookCi.numCheque = '--';
                    vm.dailybookCi.typeContab = vm.typeContabCi;
                    vm.dailybookCi.dailyCiSeq = vm.dailyCiSeq;
                    vm.dailybookCi.dailyCiStringSeq = vm.dailyCiStringSeq;
                    vm.dailybookCi.dailyCiStringUserSeq = 'PAGO GENERADO ' + vm.dailyCiStringSeq;
                    vm.dailybookCi.clientProvName = vm.clientSelected.name;
                    vm.dailybookCi.generalDetail = vm.generalDetailCi_1;
                    vm.dailybookCi.total = vm.bill.total;
                    vm.dailybookCi.iva = parseFloat((vm.bill.total * 0.12).toFixed(2));
                    vm.dailybookCi.subTotalDoce = parseFloat((vm.bill.total / 1.12).toFixed(2));
                    vm.dailybookCi.subTotalCero = 0;
                    vm.dailybookCi.dateRecordBook = vm.bill.dateCreated;
                    
                }
            }

            reqBill.comprobanteCobro = vm.comprobanteCobro;
            reqBill.dailybookCi = vm.dailybookCi;
            
            // 1 is typeDocument Bill **************!!!
            // billService.getClaveDeAccesoSaveBill(reqBill, vm.companyData.userClient.id, 1).then(function(resp) {
            billService.getClaveDeAccesoSaveBill(reqBill, vm.userId, 1).then(function(resp) {
              vm.bill.claveDeAcceso = resp.data.claveDeAcceso;
              vm.billed = true;
              vm.newBill = false;
              vm.newBuy = false;
              vm.user.cashier.billNumberSeq = vm.bill.billSeq;
              if(vm.comprobanteCobro.comprobanteSeq !== undefined){
                vm.user.cashier.compCobroNumberSeq = vm.comprobanteCobro.comprobanteSeq;
                vm.user.cashier.dailyCiNumberSeq = vm.dailybookCi.dailyCiSeq;
              }
              holderService.set(vm.user);
              vm.loading = false;
              setTimeout(function() {
                  //LOZADA cashier ids
                if(['ff2daf1f-047e-40b5-be28-ae79a326257a',
                    '34c5289c-3b1d-4920-8b4f-c4266ad3c0fe'].includes(vm.user.cashier.id)){
                    vm.printToCartTwo('printLRDosMillBillId')
                } else {
                    vm.user.cashier.specialPrint ? vm.printToCart('printMatrixBillId') : document.getElementById("printBtnBill").click();
                    
                }
                vm.nuevaBill();
                // document.getElementById("printBtnBill").click();
                // vm.printToCart('printMatrixBillId')
                
            }, 300);
              if (vm.seqChanged) {
                  cashierService.update(vm.user.cashier).then(function(resp) {
                      // cashier updated
                  }).catch(function(error) {
                      vm.loading = false;
                      vm.error = error.data;
                  });
              };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = "Error al obtener Clave de Acceso y Guardar Factura: " + error.data;
            });
        };

        vm.getIsAgentRetention = function() {
            if(vm.user.subsidiary.userClient.agentRetention){
                if(vm.user.subsidiary.userClient.id === 'e660b893-0c70-4985-b9d7-890938412ec4') {
                    return 'Agente de Retención mediante Resolución Nro. NAC-GTRRIOC21-00000001'
                }
                return 'Agente de Retención mediante Resolución Nro. NAC-DNCRASC20-00000001'
            }
            return '';
        }

        //Consumption Code
        function _getCsmSeqNumber() {
            vm.numberCsmAddedOne = parseInt(vm.user.cashier.csmNumberSeq) + 1;
            vm.csmSeqNumber = utilSeqService._pad_with_zeroes(vm.numberCsmAddedOne, 6);
        };

        function _initializeConsumption() {
            vm.consumption = {
                client: vm.clientSelected,
                userIntegridad: vm.user,
                subsidiary: vm.user.subsidiary,
                dateConsumption: vm.dateBill.getTime(),
                total: 0,
                subTotal: 0,
                iva: 0,
                ice: 0,
                baseNoTaxes: 0,
                baseTaxes: 0,
                detailsConsumption: []
            };
        };

        vm.saveConsumption = function(consumption) {
            vm.loading = true;
            $('#modalAddPago').modal('hide');
            _getCsmSeqNumber();
            warehouseService.getAllWarehouseByUserClientId(vm.userClientId).then(function(response) {
                vm.warehouseList = response;
                var observation = 'CONSUMO INTERNO';
                var finalWarehouse = _.filter(vm.warehouseList, function(warehouse) { return warehouse.subsidiary.id === vm.subsidiaryId});
                if(vm.bill.observation !== undefined && vm.bill.observation !== '' && vm.bill.observation !== null ){
                    observation = vm.bill.observation;
                }
                
                vm.consumption.warehouse = finalWarehouse[0];
                vm.consumption.dateConsumption = $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime();
                vm.consumption.csmSeq = parseInt(vm.numberCsmAddedOne);
                vm.consumption.csmNumberSeq = vm.csmSeqNumber;
                vm.consumption.clientName = vm.clientSelected.name;
                vm.consumption.codeWarehouse = '--';
                vm.consumption.nameSupervisor = '--';
                vm.consumption.observation = observation;
                vm.consumption.detailsKardex = [];
                _.each(vm.consumption.detailsConsumption, function(item) {
                    var kardex = {
                        consumption: consumption.id,
                        product: item.product,
                        codeWarehouse: '--',
                        dateRegister: $('#pickerBillDate').data("DateTimePicker").date().toDate().getTime(),
                        details: 'CONSUMO INTERNO Nro. ' + vm.csmSeqNumber,
                        observation: 'EGRESO',
                        detalle: vm.consumption.observation,
                        prodCostEach: item.costEach,
                        prodName: item.product.name,
                        prodQuantity: item.quantity,
                        prodTotal: item.total,
                        subsidiaryId: vm.subsidiaryId,
                        userClientId: vm.userClientId,
                        userId: vm.userId
                    };
                    vm.consumption.detailsKardex.push(kardex);
                });

                consumptionService.create(consumption).then(function(respConsumption) {
                    vm.user.cashier.csmNumberSeq = vm.numberCsmAddedOne;
                    holderService.set(vm.user);
                    vm.consumptionCreated = respConsumption;
                    vm.consumptioned = true;
                    vm.newBuy = false;
                    vm.newConsumptionOn = false;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.exit = function() {
            if(vm.clienteAConsultar) {
                vm.bill = undefined;
                vm.clientSelected = undefined;
                vm.billList = angular.copy(vm.billListCopy)
            } else {
                $location.path('/home');    
            }
            //$location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
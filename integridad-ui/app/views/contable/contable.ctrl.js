'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ContableCtrl
 * @description
 * # ContableCtrl
 * Controller of the template contable.tpl.html
 */
angular.module('integridadUiApp')
    .controller('ContableCtrl', function(_, $localStorage, cuentaContableService, providerService, eretentionService,
                                            utilSeqService, debtsToPayService, contableService, $location) {
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    
    vm.aux = undefined;
    vm.userData = $localStorage.user;
    vm.retentionId = undefined;
    vm.retentionNumber = undefined;
    vm.retentionDateCreated = undefined;
    vm.retentionTotal = undefined;
    vm.retenSelected = undefined;
    vm.retenTaxTypeFuente = undefined;
    vm.retenTaxTypeIva = undefined;
    vm.retenCodeFuente = undefined;
    vm.retenTotalFuente = undefined;
    vm.retenCodeIva = undefined;
    vm.retenTotalIva = undefined;

    vm.typeBookList = [
        {code: '1', type: 'CONTABILIDAD GENERAL'},
        {code: '2', type: 'COMPROBANTE DE EGRESO'},
        {code: '3', type: 'COMPROBANTE DE INGRESO'},
        {code: '4', type: 'CUENTA POR COBRAR'},
        {code: '5', type: 'CUENTA POR PAGAR'},
        {code: '6', type: 'FACTURACIÓN - VENTAS'},
        {code: '7', type: 'RETENCIONES'},
        {code: '8', type: 'NOTA DE CRÉDITO'},
        {code: '9', type: 'NOTA DE DÉBITO'},
        {code: '10', type: 'INVENTARIO'},
        {code: '11', type: 'TRANSFERENCIA'}
    ];

    function _activate() {
        vm.loading = true;
        vm.success = undefined;
        vm.today = new Date();
        vm.dailybookType = undefined;
        vm.typeContab = undefined;
        vm.contableList = [];
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
        
        //Contabilidad General
        vm.dailybookCgNew = undefined;
        vm.dailyCgSeq = undefined;
        vm.dailyCgStringSeq = undefined;
        vm.dailiedCg = false;
        vm.subIvaCg = undefined;
        vm.subTotalDoceCg = undefined;
        vm.subTotalCeroCg = undefined;
        vm.totalCg = undefined;
        vm.dailybookCgList = undefined;
        vm.templateDailybookCg = undefined;
        vm.dailybookCgCreated = undefined;
        vm.dailybookCgTemp = undefined;
        vm.generalDetailCg = undefined;
        
        //Cuentas por Pagar
        vm.dailybookCppNew = undefined;
        vm.dailyCppSeq = undefined;
        vm.dailyCppStringSeq = undefined;
        vm.dailiedCpp = false;
        vm.subIvaCpp = undefined;
        vm.subTotalDoceCpp = undefined;
        vm.subTotalCeroCpp = undefined;
        vm.totalCpp = undefined;
        vm.dailybookCppList = undefined;
        vm.dailybookCppCreated = undefined;
        vm.generalDetailCpp = undefined;
        
        vm.contable = undefined;
        vm.debtsToPayList = undefined;
        vm.debtsBillNumber = undefined;
        vm.debtsTotal = undefined;
        vm.provider = undefined;
        vm.providerId = undefined;
        vm.providerRuc = undefined;
        vm.providerName = undefined;
        vm.providerSelected = undefined;
        vm.providerList = undefined;
        vm.retentionId = undefined;
        vm.retentionNumber = undefined;
        vm.retentionDateCreated = undefined;
        vm.retentionTotal = undefined;
        vm.retenSelected = undefined;
        vm.retenTaxTypeFuente = undefined;
        vm.retenTaxTypeIva = undefined;
        vm.retenCodeFuente = undefined;
        vm.retenTotalFuente = undefined;
        vm.retenCodeIva = undefined;
        vm.retenTotalIva = undefined;
        vm.usrCliId = $localStorage.user.subsidiary.userClient.id;
        vm.selectedTypeBook = undefined;
        vm.error = undefined;
        vm.loading = false;
    };

    function _getDailyCgSeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.dailyCgNumberSeq) + 1;
        vm.dailyCgSeq = vm.numberAddedOne;
        vm.dailyCgStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    function _initializeDailybookCg() {
        vm.dailybookCg = {
            userIntegridad: $localStorage.user,
            subsidiary: $localStorage.user.subsidiary,
            subTotalDoce: 0.0,
            iva: 0.0,
            subTotalCero: 0.0,
            total: 0.0,
            detailDailybookCg: []
        };
    };

    function _getDailyCppSeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.dailyCppNumberSeq) + 1;
        vm.dailyCppSeq = vm.numberAddedOne;
        vm.dailyCppStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    function _initializeDailybookCpp() {
        vm.dailybookCpp = {
            userIntegridad: $localStorage.user,
            subsidiary: $localStorage.user.subsidiary,
            provider: vm.providerSelected,
            subTotalDoce: 0.0,
            iva: 0.0,
            subTotalCero: 0.0,
            total: 0.0,
            detailDailybookCpp: []
        }
    };

    vm.loadTypeDailybook = function() {
        switch (vm.dailybookType) {
            case '1':
                vm.selectedTypeBook = vm.dailybookType;
                vm.typeContab = 'CONTABILIDAD GENERAL';
                contableService.getDailybookCgByUserClientId(vm.usrCliId).then(function(response) {
                    vm.dailybookCgList = response;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            break;
            case '5':
                vm.selectedTypeBook = vm.dailybookType;
                vm.typeContab = 'CUENTA POR PAGAR CON RETENCIONES';
                providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                    vm.providerList = response;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            break;
        };
    };

    vm.cancel = function() {
        _activate();
    };

// Funciones para el Diario de Contabilidad General
    vm.addDailybookCg = function() {
        vm.templateDailybookCg = false;
        _getDailyCgSeqNumber();
        _initializeDailybookCg();
        var today = new Date();
        $('#pickerDateRecordBookCg').data("DateTimePicker").date(today);
        vm.dailybookCgTemp = true;
        vm.dailybookCgNew = true;
    };

    vm.putGeneralDetailCg = function() {
        if (vm.generalDetailCg == null || vm.generalDetailCg == undefined || vm.generalDetailCg == '') {
            vm.generalDetailCg = vm.dailybookCg.clientProvName + ' No: ' + vm.dailybookCg.dailyCgStringUserSeq;
        };
    };

    vm.getCuentasContablesCg = function() {
        if (vm.cuenta === true) {
            cuentaContableService.getCuentaContableByUserClient(vm.usrCliId).then(function(response) {
                vm.cuentaContableList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        } else {
            vm.cuentaContableList = undefined;
        };
    };

    vm.selectCuentasCg = function(cuenta) {
        vm.item = undefined;
        vm.item = {
            codeConta: cuenta.code,
            descrip: cuenta.description,
            name: cuenta.name
        };
    };

    vm.addItemCg = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCg.detailDailybookCg.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.item.tipo == 'DEBITO (D)') {
            vm.item.deber = vm.item.baseImponible;
        } else if (vm.item.tipo == 'CREDITO (C)') {
            vm.item.haber = vm.item.baseImponible;
        };
        vm.dailybookCg.detailDailybookCg.push(vm.item);
        vm.item = undefined;
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
    };

    vm.editItemCg = function(index) {
        vm.item = angular.copy(vm.dailybookCg.detailDailybookCg[index]);
        vm.indexEdit = index;
    };
  
    vm.deleteItemCg = function(index) {
        vm.dailybookCg.detailDailybookCg.splice(index, 1);
    };

    vm.getTotalDebitoCg = function() {
        var totalDebitoCg = 0;
        if (vm.dailybookCg) {
            _.each (vm.dailybookCg.detailDailybookCg, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebitoCg = (parseFloat(totalDebitoCg) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoDebitoCg = totalDebitoCg;
        return totalDebitoCg;
    };
  
    vm.getTotalCreditoCg = function() {
        var totalCreditoCg = 0;
        if (vm.dailybookCg) {
            _.each(vm.dailybookCg.detailDailybookCg, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCreditoCg = (parseFloat(totalCreditoCg) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoCreditoCg = totalCreditoCg;
        return totalCreditoCg;
    };
  
    vm.getTotalSaldoCg = function() {
        var totalSaldoCg = 0;
        totalSaldoCg = (parseFloat(vm.saldoCreditoCg) - parseFloat(vm.saldoDebitoCg)).toFixed(2);
        return totalSaldoCg;
    };

    vm.saveDailybookCg = function() {
        vm.loading = true;
        vm.dailybookCgTemp = false;
        vm.dailybookCg.codeTypeContab = vm.selectedTypeBook;
        vm.dailybookCg.typeContab = vm.typeContab;
        vm.dailybookCg.dailyCgSeq = vm.dailyCgSeq;
        vm.dailybookCg.dailyCgStringSeq = vm.dailyCgStringSeq;
        vm.dailybookCg.generalDetail = vm.generalDetailCg;
        vm.dailybookCg.dateRecordBook = $('#pickerDateRecordBookCg').data("DateTimePicker").date().toDate().getTime();
        contableService.createDailybookCg(vm.dailybookCg).then(function(response) {
            vm.dailybookCgNew = false;
            vm.dailybookCgCreated = response;
            $localStorage.user.cashier.dailyCgNumberSeq = vm.dailybookCg.dailyCgSeq;
            vm.dailiedCg = true;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getTotalDebitoCgCreated = function() {
        var totalDebito = 0.0;
        if (vm.dailybookCgCreated) {
            _.each(vm.dailybookCgCreated.detailDailybookCg, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebito = (parseFloat(totalDebito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalDebito;
    };

    vm.getTotalCreditoCgCreated = function() {
        var totalCredito = 0.0;
        if (vm.dailybookCgCreated) {
            _.each(vm.dailybookCgCreated.detailDailybookCg, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCredito = (parseFloat(totalCredito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalCredito;
    };

    vm.cancelDailybookCgCreated = function() {
        _activate();
    };

    vm.dailybookCgSelected = function(dailybookCg) {
        vm.loading = true;
        contableService.getDailybookCgById(dailybookCg.id).then(function(response) {
            vm.dailybookCgCreated = response;
            vm.dailybookCg = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

//Funciones para el Diario de Cuentas por Pagar
    vm.providerSelect = function(provider) {
        vm.success = undefined;
        vm.loading = true;
        vm.providerSelected = provider;
        vm.providerId = provider.id;
        vm.providerName = provider.razonSocial;
        _getDailyCppSeqNumber();
        _initializeDailybookCpp()
        vm.loading = false;
        var today = new Date();
        $('#pickerDateRecordBookCg').data("DateTimePicker").date(today);
        vm.dailybookCppNew = true;
    };

    vm.consultDailybookCpp = function(provider) {
        vm.success = undefined;
        vm.loading = true;
        contableService.getDailybookCppByProviderId(provider.id).then(function(response) {
            vm.dailybookCppList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getDebtsToPayByProvider = function() {
        vm.loading = true;
        debtsToPayService.getAllDebtsToPayByProviderId(vm.providerId).then(function(response) {
            vm.debtsToPayList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.debtsToPaySelected = function(debtsToPay) {
        vm.loading = true;
        $('#modalShowDebtsToPay').modal('hide');
        vm.debtsBillNumber = debtsToPay.billNumber;
        vm.debtsTotal = debtsToPay.total;
        vm.generalDetailCpp = vm.providerName + ' ' + 'Fc' + ' ' + vm.debtsBillNumber;
        vm.dailybookCpp.billNumber = vm.debtsBillNumber;
        vm.dailybookCpp.total = vm.debtsTotal;
        vm.loading = false;
    };

    vm.putGeneralDetailCpp = function() {
        if (vm.generalDetailCpp == null || vm.generalDetailCpp == undefined || vm.generalDetailCpp == '') {
            vm.generalDetailCpp = vm.providerName + ' ' + 'Fc' + ' ' + vm.dailybookCpp.billNumber;
        };
    };

    vm.getRetentionByProvider = function() {
        vm.loading = true;
        eretentionService.getAllRetentionsByProviderId(vm.providerId).then(function(response) {
          vm.retentionList = response;
          vm.loading = false;
        }).catch(function(error) {
          vm.loading = false;
          vm.error = error.data;
        });
    };

    vm.retentionSelected = function(retention) {
        vm.loading = true;
        $('#modalFindRetention').modal('hide');
        vm.retentionList = undefined;
        vm.retentionId = retention.id;
        vm.retentionNumber = retention.stringSeq;
        vm.retentionDateCreated = retention.dateCreated;
        eretentionService.getRetentionById(retention.id).then(function(response) {
            vm.retention = response;
            vm.totalRetention = 0;
            _.each(vm.retention.detailRetentions, function(det) {
                vm.totalRetention = vm.totalRetention + det.total;
            });
            _.map(vm.retention.detailRetentions, function(detail) {
                if (detail.taxType == 'RETENCION EN LA FUENTE') {
                    vm.retenCodeFuente = detail.code;
                    vm.retenTaxTypeFuente = detail.taxType;
                    vm.retenTotalFuente = detail.total;
                } else if (detail.taxType == 'RETENCION EN EL IVA') {
                    vm.retenCodeIva = detail.code;
                    vm.retenTaxTypeIva = detail.taxType;
                    vm.retenTotalIva = detail.total;
                };
            });
            vm.retentionTotal = parseFloat(vm.totalRetention);
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getCuentasContablesCpp = function() {
        if (vm.cuenta === true) {
            cuentaContableService.getCuentaContableByUserClient(vm.usrCliId).then(function(response) {
                vm.cuentaContableList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        } else {
            vm.cuentaContableList = undefined;
        };
    };

    vm.selectCuentasCpp = function(cuenta) {
        vm.itema = undefined;
        vm.itema = {
            codeConta: cuenta.code,
            descrip: cuenta.description,
            name: cuenta.name
        };
    };

    vm.addItemCpp = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCpp.detailDailybookCpp.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.itema.tipo == 'DEBITO (D)') {
            vm.itema.deber = vm.itema.baseImponible;
        } else if (vm.itema.tipo == 'CREDITO (C)') {
            vm.itema.haber = vm.itema.baseImponible;
        };
        vm.dailybookCpp.detailDailybookCpp.push(vm.itema);
        vm.itema = undefined;
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
    };

    vm.editItemCpp = function(index) {
        vm.itema = angular.copy(vm.dailybookCpp.detailDailybookCpp[index]);
        vm.indexEdit = index;
    };
  
    vm.deleteItemCpp = function(index) {
        vm.dailybookCpp.detailDailybookCpp.splice(index, 1);
    };

    vm.getTotalDebitoCpp = function() {
        var totalDebitoCpp = 0;
        if (vm.dailybookCpp) {
            _.each (vm.dailybookCpp.detailDailybookCpp, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebitoCpp = (parseFloat(totalDebitoCpp) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoDebitoCpp = totalDebitoCpp;
        return totalDebitoCpp;
    };
  
    vm.getTotalCreditoCpp = function() {
        var totalCreditoCpp = 0;
        if (vm.dailybookCpp) {
            _.each(vm.dailybookCpp.detailDailybookCpp, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCreditoCpp = (parseFloat(totalCreditoCpp) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoCreditoCpp = totalCreditoCpp;
        return totalCreditoCpp;
    };
  
    vm.getTotalSaldoCpp = function() {
        var totalSaldoCpp = 0;
        totalSaldoCpp = (parseFloat(vm.saldoCreditoCpp) - parseFloat(vm.saldoDebitoCpp)).toFixed(2);
        return totalSaldoCpp;
    };

    vm.addRetentionToDailybookCpp = function() {
        vm.retenSelected = true;
        // Usuario La Quinta
        if (vm.usrCliId === '758dea84-74f5-4209-b218-9b84c10621fc') {
            if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                switch(vm.retenCodeFuente) {
                    case '302':
                        vm.retenFteCodeContable = '2.01.07.01.001'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '303':
                        vm.retenFteCodeContable = '2.01.07.01.002'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '304':
                        vm.retenFteCodeContable = '2.01.07.01.003'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '307':
                        vm.retenFteCodeContable = '2.01.07.01.004'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '308':
                        vm.retenFteCodeContable = '2.01.07.01.005'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '309':
                        vm.retenFteCodeContable = '2.01.07.01.006'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '310':
                        vm.retenFteCodeContable = '2.01.07.01.007'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '312':
                        vm.retenFteCodeContable = '2.01.07.01.008'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '319':
                        vm.retenFteCodeContable = '2.01.07.01.009'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '320':
                        vm.retenFteCodeContable = '2.01.07.01.010'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '322':
                        vm.retenFteCodeContable = '2.01.07.01.011'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '323':
                        vm.retenFteCodeContable = '2.01.07.01.012'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '327':
                        vm.retenFteCodeContable = '2.01.07.01.013'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '328':
                        vm.retenFteCodeContable = '2.01.07.01.014'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '340':
                        vm.retenFteCodeContable = '2.01.07.01.015'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '341':
                        vm.retenFteCodeContable = '2.01.07.01.016'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '342':
                        vm.retenFteCodeContable = '2.01.07.01.017'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '343':
                        vm.retenFteCodeContable = '2.01.07.01.018'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '344':
                        vm.retenFteCodeContable = '2.01.07.01.019'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                };
            };
            
            if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                switch(vm.retenCodeIva) {
                    case '721':
                        vm.retenIvaCodeContable = '2.01.07.02.001'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '723':
                        vm.retenIvaCodeContable = '2.01.07.02.002'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '725':
                        vm.retenIvaCodeContable = '2.01.07.02.003'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '727':
                        vm.retenIvaCodeContable = '2.01.07.02.004'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '729':
                        vm.retenIvaCodeContable = '2.01.07.02.005'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '731':
                        vm.retenIvaCodeContable = '2.01.07.02.006'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                };
            };
        // Usuario Mr. Zolutions
        } else if (vm.usrCliId === '4907601b-6e54-4675-80a8-ab6503e1dfeb') {
            if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                switch(vm.retenCodeFuente) {
                    case '303':
                        vm.retenFteCodeContable = '2.01.07.03.303'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '304':
                        vm.retenFteCodeContable = '2.01.07.03.304'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '307':
                        vm.retenFteCodeContable = '2.01.07.03.307'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '309':
                        vm.retenFteCodeContable = '2.01.07.03.309'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '310':
                        vm.retenFteCodeContable = '2.01.07.03.310'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '312':
                        vm.retenFteCodeContable = '2.01.07.03.312'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '319':
                        vm.retenFteCodeContable = '2.01.07.03.319'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '320':
                        vm.retenFteCodeContable = '2.01.07.03.320'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '322':
                        vm.retenFteCodeContable = '2.01.07.03.322'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '323':
                        vm.retenFteCodeContable = '2.01.07.03.323'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '325':
                        vm.retenFteCodeContable = '2.01.07.03.325'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '327':
                        vm.retenFteCodeContable = '2.01.07.03.327'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '342':
                        vm.retenFteCodeContable = '2.01.07.03.342'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '344':
                        vm.retenFteCodeContable = '2.01.07.03.344'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                };
            };
  
            if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                switch(vm.retenCodeIva) {
                    case '721':
                        vm.retenIvaCodeContable = '2.01.07.02.721'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '723':
                        vm.retenIvaCodeContable = '2.01.07.02.723'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '725':
                        vm.retenIvaCodeContable = '2.01.07.02.725'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '727':
                        vm.retenIvaCodeContable = '2.01.07.02.727'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '729':
                        vm.retenIvaCodeContable = '2.01.07.02.729'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '731':
                        vm.retenIvaCodeContable = '2.01.07.02.731'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCpp; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                };
            };
        };

        vm.itemRetentionFuente = undefined;
        vm.itemRetentionFuente = { codeConta: vm.retenFteCodeContable, descrip: vm.retenFteDescContable,
                                    tipo: 'CREDITO (C)', baseImponible: vm.retenFteValor, name: vm.retenFteNombContable, haber: vm.retenFteValor
                                 };
        vm.itemRetentionIVA = undefined;
        vm.itemRetentionIVA = { codeConta: vm.retenIvaCodeContable, descrip: vm.retenIvaDescContable,
                                tipo: 'CREDITO (C)', baseImponible: vm.retenIvaValor, name: vm.retenIvaNombContable, haber: vm.retenIvaValor
                              };
  
        if (vm.retenCodeFuente != null) {
            vm.dailybookCpp.detailDailybookCpp.push(vm.itemRetentionFuente);
        };
        
        if (vm.retenCodeIva != null) {
            vm.dailybookCpp.detailDailybookCpp.push(vm.itemRetentionIVA);
        };

        vm.itemRetentionFuente = undefined;
        vm.itemRetentionIVA = undefined;
        vm.retenCodeFuente = undefined;
        vm.retenCodeIva = undefined;
    };

    vm.addIvaAndProvider = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCpp.detailDailybookCpp.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.retentionId == null) {
            vm.retentionTotal = 0;
        };
        //Selección de las Cuentas Contables por defecto dependiendo del Cliente
        if (vm.usrCliId === '758dea84-74f5-4209-b218-9b84c10621fc') {
            vm.ivaContable = '1.01.05.01.001';
            vm.provContable = '2.01.03.01.001';
        } else if (vm.usrCliId === '4907601b-6e54-4675-80a8-ab6503e1dfeb') {
            vm.ivaContable = '1.01.05.02.001';
            vm.provContable = '2.01.03.01.001';
        } else {
            vm.ivaContable = '1.01.01.01';
            vm.provContable = '2.01.01.01';
        };
      
        vm.subIva = parseFloat((vm.item.baseImponible * 0.1200).toFixed(2));
        vm.subTotalDoce = vm.item.baseImponible;
        vm.subTotalCero = vm.dailybookCpp.total - vm.subTotalDoce - vm.subIva;
        vm.itemIva = {
            codeConta: vm.ivaContable,
            descrip: 'IVA EN COMPRAS',
            tipo: 'DEBITO (D)',
            baseImponible: vm.subIva,
            name: vm.generalDetailCpp,
            deber: vm.subIva
        };
        vm.itemProvider = {
            codeConta: vm.provContable,
            descrip: 'PROVEEDORES LOCALES',
            tipo: 'CREDITO (C)',
            baseImponible: vm.dailybookCpp.total - vm.retentionTotal,
            name: vm.generalDetailCpp,
            haber: vm.dailybookCpp.total - vm.retentionTotal
        };
        if (vm.item.baseImponible == 0) {
            vm.dailybookCpp.detailDailybookCpp.push(vm.itemProvider);
        } else {
            vm.dailybookCpp.detailDailybookCpp.push(vm.itemIva);
            vm.dailybookCpp.detailDailybookCpp.push(vm.itemProvider);
        };
    };

    vm.saveDailybookCpp = function() {
        vm.loading = true;
        vm.providerSelected = undefined;
        vm.dailybookCpp.codeTypeContab = vm.selectedTypeBook;
        vm.dailybookCpp.typeContab = vm.typeContab;
        vm.dailybookCpp.dailyCppSeq = vm.dailyCppSeq;
        vm.dailybookCpp.dailyCppStringSeq = vm.dailyCppStringSeq;
        vm.dailybookCpp.clientProvName = vm.providerName;
        vm.dailybookCpp.generalDetail = vm.generalDetailCpp;
        vm.dailybookCpp.iva = vm.subIva;
        vm.dailybookCpp.subTotalDoce = vm.subTotalDoce;
        vm.dailybookCpp.subTotalCero = vm.subTotalCero;
        vm.dailybookCpp.dateRecordBook = $('#pickerDateRecordBookCpp').data("DateTimePicker").date().toDate().getTime();
        if (vm.retentionId == null) {
            vm.dailybookCpp.retentionId = 'SIN RETENCIÓN';
            vm.dailybookCpp.retentionNumber = 0;
            vm.dailybookCpp.retentionDateCreated = 0;
            vm.dailybookCpp.retentionTotal = 0.0;
        } else {
            vm.dailybookCpp.retentionId = vm.retentionId;
            vm.dailybookCpp.retentionNumber = vm.retentionNumber;
            vm.dailybookCpp.retentionDateCreated = vm.retentionDateCreated;
            vm.dailybookCpp.retentionTotal = vm.retentionTotal;
        };
        contableService.createDailybookCpp(vm.dailybookCpp).then(function(response) {
            vm.dailybookCppNew = false;
            vm.dailybookCppCreated = response;
            $localStorage.user.cashier.dailyCppNumberSeq = vm.dailybookCpp.dailyCppSeq;
            vm.dailiedCpp = true;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getTotalDebitoCppCreated = function() {
        var totalDebito = 0.0;
        if (vm.dailybookCppCreated) {
            _.each(vm.dailybookCppCreated.detailDailybookCpp, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebito = (parseFloat(totalDebito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalDebito;
    };

    vm.getTotalCreditoCppCreated = function() {
        var totalCredito = 0.0;
        if (vm.dailybookCppCreated) {
            _.each(vm.dailybookCppCreated.detailDailybookCpp, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCredito = (parseFloat(totalCredito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalCredito;
    };

    vm.cancelDailybookCppCreated = function() {
        _activate();
    };

    vm.dailybookCppSelected = function(dailybookCpp) {
        vm.loading = true;
        vm.dailybookCppList = undefined;
        contableService.getDailybookCppById(dailybookCpp.id).then(function(response) {
            vm.dailybookCppCreated = response;
            vm.dailybookCpp = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.exit = function() {
        $location.path('/home');
    };

    (function initController() {
        _activate();
    })();
});
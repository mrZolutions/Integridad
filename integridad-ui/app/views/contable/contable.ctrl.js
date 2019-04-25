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
        
        //Contabilidad General Cg
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

        //Comprobante de Egreso Ce
        vm.dailybookCeNew = undefined;
        vm.dailyCeSeq = undefined;
        vm.dailyCeStringSeq = undefined;
        vm.dailiedCe = false;
        vm.subIvaCe = undefined;
        vm.subTotalDoceCe = undefined;
        vm.subTotalCeroCe = undefined;
        vm.totalCe = undefined;
        vm.dailybookCeList = undefined;
        vm.dailybookCeCreated = undefined;
        vm.generalDetailCe = undefined;
        vm.numCheque = undefined;
        
        //Cuentas por Pagar Cpp
        vm.dailybookCxPNew = undefined;
        vm.dailyCxPSeq = undefined;
        vm.dailyCxPStringSeq = undefined;
        vm.dailiedCxP = false;
        vm.subIvaCxP = undefined;
        vm.subTotalDoceCxP = undefined;
        vm.subTotalCeroCxP = undefined;
        vm.totalCxP = undefined;
        vm.dailybookCxPList = undefined;
        vm.dailybookCxPCreated = undefined;
        vm.generalDetailCxP = undefined;
        
        vm.contable = undefined;
        vm.debtsToPayList = undefined;
        vm.debtsBillNumber = undefined;
        vm.debtsTotal = undefined;
        vm.provider = undefined;
        vm.providerId = undefined;
        vm.providerRuc = undefined;
        vm.providerName = undefined;
        vm.providerCeSelected = undefined;
        vm.providerCxPSelected = undefined;
        vm.providerCxPList = undefined;
        vm.providerCeList = undefined;
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
            detailDailybookContab: []
        };
    };

    function _getDailyCeSeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.dailyCeNumberSeq) + 1;
        vm.dailyCeSeq = vm.numberAddedOne;
        vm.dailyCeStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    function _initializeDailybookCe() {
        vm.dailybookCe = {
            userIntegridad: $localStorage.user,
            subsidiary: $localStorage.user.subsidiary,
            provider: vm.providerCeSelected,
            subTotalDoce: 0.0,
            iva: 0.0,
            subTotalCero: 0.0,
            total: 0.0,
            detailDailybookContab: []
        };
    };

    function _getDailyCxPSeqNumber() {
        vm.numberAddedOne = parseInt($localStorage.user.cashier.dailyCppNumberSeq) + 1;
        vm.dailyCxPSeq = vm.numberAddedOne;
        vm.dailyCxPStringSeq = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 6);
    };

    function _initializeDailybookCxP() {
        vm.dailybookCxP = {
            userIntegridad: $localStorage.user,
            subsidiary: $localStorage.user.subsidiary,
            provider: vm.providerCxPSelected,
            subTotalDoce: 0.0,
            iva: 0.0,
            subTotalCero: 0.0,
            total: 0.0,
            detailDailybookContab: []
        };
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
            case '2':
                vm.selectedTypeBook = vm.dailybookType;
                vm.typeContab = 'COMPROBANTE DE EGRESO';
                providerService.getLazyByUserClientId(vm.usrCliId).then(function(response) {
                    vm.providerCeList = response;
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
                    vm.providerCxPList = response;
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
        vm.error = undefined;
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
            cuentaContableService.getCuentaContableByUserClientNoBank(vm.usrCliId).then(function(response) {
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
            typeContab: vm.typeContab,
            codeConta: cuenta.code,
            descrip: cuenta.description,
            name: cuenta.name
        };
    };

    vm.addItemCg = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCg.detailDailybookContab.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.item.tipo == 'DEBITO (D)') {
            vm.item.deber = vm.item.baseImponible;
        } else if (vm.item.tipo == 'CREDITO (C)') {
            vm.item.haber = vm.item.baseImponible;
        };
        vm.item.numCheque =  '--';
        vm.dailybookCg.detailDailybookContab.push(vm.item);
        vm.item = undefined;
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
    };

    vm.editItemCg = function(index) {
        vm.item = angular.copy(vm.dailybookCg.detailDailybookContab[index]);
        vm.indexEdit = index;
    };
  
    vm.deleteItemCg = function(index) {
        vm.dailybookCg.detailDailybookContab.splice(index, 1);
    };

    vm.getTotalDebitoCg = function() {
        var totalDebitoCg = 0;
        if (vm.dailybookCg) {
            _.each (vm.dailybookCg.detailDailybookContab, function(detail) {
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
            _.each(vm.dailybookCg.detailDailybookContab, function(detail) {
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
        vm.dailybookCg.total = vm.saldoDebitoCg;
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
            _.each(vm.dailybookCgCreated.detailDailybookContab, function(detail) {
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
            _.each(vm.dailybookCgCreated.detailDailybookContab, function(detail) {
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

//Funciones para el Comprobante de Egreso
    vm.providerCeSelect = function(provider) {
        vm.success = undefined;
        vm.error = undefined;
        vm.loading = true;
        vm.providerCeSelected = provider;
        vm.providerId = provider.id;
        vm.providerName = provider.razonSocial;
        _getDailyCeSeqNumber();
        _initializeDailybookCe()
        vm.loading = false;
        var today = new Date();
        $('#pickerDateRecordBookCe').data("DateTimePicker").date(today);
        vm.dailybookCeNew = true;
    };

    vm.consultDailybookCe = function(provider) {
        vm.success = undefined;
        vm.error = undefined;
        vm.loading = true;
        contableService.getDailybookCeByProviderId(provider.id).then(function(response) {
            vm.dailybookCeList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getDebtsToPayByProviderCe = function() {
        vm.loading = true;
        debtsToPayService.getAllDebtsToPayByProviderId(vm.providerId).then(function(response) {
            vm.debtsToPayList = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.debtsToPaySelectedCe = function(debtsToPay) {
        vm.loading = true;
        $('#modalShowDebtsToPayCe').modal('hide');
        vm.debtsBillNumber = debtsToPay.billNumber;
        vm.debtsTotal = debtsToPay.total;
        vm.generalDetailCe = vm.providerName + ' ' + 'Fc' + ' ' + vm.debtsBillNumber;
        vm.dailybookCe.billNumber = vm.debtsBillNumber;
        vm.dailybookCe.total = vm.debtsTotal;
        //Selección de las Cuentas Contables por defecto dependiendo del Cliente
        if (vm.usrCliId === '758dea84-74f5-4209-b218-9b84c10621fc') {
            vm.provContable = '2.01.03.01.001';
        } else if (vm.usrCliId === '4907601b-6e54-4675-80a8-ab6503e1dfeb') {
            vm.provContable = '2.01.03.01.001';
        } else if (vm.usrCliId === '1e2049c3-a3bc-4231-a0de-dded8020dc1b') {
            vm.provContable = '2.12.10.101';
        } else {
            vm.provContable = '2.01.01.01';
        };
        vm.itemProvider = {
            typeContab: vm.typeContab,
            codeConta: vm.provContable,
            descrip: 'PROVEEDORES LOCALES',
            tipo: 'DEBITO (D)',
            baseImponible: parseFloat((vm.dailybookCe.total).toFixed(2)),
            name: vm.generalDetailCe,
            deber: parseFloat((vm.dailybookCe.total).toFixed(2))
        };
        vm.itemProvider.numCheque =  '--';
        vm.dailybookCe.detailDailybookContab.push(vm.itemProvider);
        vm.loading = false;
    };

    vm.putGeneralDetailCe = function() {
        if (vm.generalDetailCe == null || vm.generalDetailCe == undefined || vm.generalDetailCe == '') {
            vm.generalDetailCe = vm.providerName + ' ' + 'Fc' + ' ' + vm.dailybookCe.billNumber;
            //Selección de las Cuentas Contables por defecto dependiendo del Cliente
            if (vm.usrCliId === '758dea84-74f5-4209-b218-9b84c10621fc') {
                vm.provContable = '2.01.03.01.001';
            } else if (vm.usrCliId === '4907601b-6e54-4675-80a8-ab6503e1dfeb') {
                vm.provContable = '2.01.03.01.001';
            } else if (vm.usrCliId === '1e2049c3-a3bc-4231-a0de-dded8020dc1b') {
                vm.provContable = '2.12.10.101';
            } else {
                vm.provContable = '2.01.01.01';
            };
            vm.itemProvider = {
                typeContab: vm.typeContab,
                codeConta: vm.provContable,
                descrip: 'PROVEEDORES LOCALES',
                tipo: 'DEBITO (D)',
                baseImponible: parseFloat(vm.dailybookCe.total),
                name: vm.generalDetailCe,
                deber: parseFloat(vm.dailybookCe.total)
            };
            vm.itemProvider.numCheque =  '--';
            vm.dailybookCe.detailDailybookContab.push(vm.itemProvider);
        };
    };

    vm.getCuentasContablesCe = function() {
        if (vm.cuenta === true) {
            cuentaContableService.getCuentaContableByUserClientAndBank(vm.usrCliId).then(function(response) {
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

    vm.selectCuentasCe = function(cuenta) {
        vm.itemb = undefined;
        vm.detailitemb = cuenta.description;
        vm.itemb = {
            typeContab: vm.typeContab,
            codeConta: cuenta.code,
            tipo: cuenta.accountType,
            descrip: cuenta.description,
            name: vm.detailitemb + ' ' + 'Cancela Fc' + ' ' + vm.dailybookCe.billNumber
        };
        vm.subTotalDoceCe = parseFloat((vm.dailybookCe.total / 1.1200).toFixed(2));
        vm.subIvaCe = parseFloat((vm.dailybookCe.total * 0.1200).toFixed(2));
        vm.subTotalCeroCe = 0.0;
    };

    vm.addItemCe = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCe.detailDailybookContab.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.itemb.tipo == 'CREDITO (C)') {
            vm.itemb.haber = vm.itemb.baseImponible;
        };
        vm.itemb.numCheque =  vm.numCheque;
        vm.dailybookCe.detailDailybookContab.push(vm.itemb);
        vm.itemb = undefined;
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
    };

    vm.editItemCe = function(index) {
        vm.itemb = angular.copy(vm.dailybookCe.detailDailybookContab[index]);
        vm.indexEdit = index;
    };
  
    vm.deleteItemCe = function(index) {
        vm.dailybookCe.detailDailybookContab.splice(index, 1);
    };

    vm.getTotalDebitoCe = function() {
        var totalDebitoCe = 0;
        if (vm.dailybookCe) {
            _.each (vm.dailybookCe.detailDailybookContab, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebitoCe = (parseFloat(totalDebitoCe) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoDebitoCe = totalDebitoCe;
        return totalDebitoCe;
    };
  
    vm.getTotalCreditoCe = function() {
        var totalCreditoCe = 0;
        if (vm.dailybookCe) {
            _.each(vm.dailybookCe.detailDailybookContab, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCreditoCe = (parseFloat(totalCreditoCe) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoCreditoCe = totalCreditoCe;
        return totalCreditoCe;
    };
  
    vm.getTotalSaldoCe = function() {
        var totalSaldoCe = 0;
        totalSaldoCe = (parseFloat(vm.saldoCreditoCe) - parseFloat(vm.saldoDebitoCe)).toFixed(2);
        return totalSaldoCe;
    };

    vm.dailybookCeSelected = function(dailybookCe) {
        vm.loading = true;
        vm.dailybookCeList = undefined;
        contableService.getDailybookCeById(dailybookCe.id).then(function(response) {
            vm.dailybookCeCreated = response;
            vm.dailybookCe = response;
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getTotalDebitoCeCreated = function() {
        var totalDebito = 0.0;
        if (vm.dailybookCeCreated) {
            _.each(vm.dailybookCeCreated.detailDailybookContab, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebito = (parseFloat(totalDebito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalDebito;
    };

    vm.getTotalCreditoCeCreated = function() {
        var totalCredito = 0.0;
        if (vm.dailybookCeCreated) {
            _.each(vm.dailybookCeCreated.detailDailybookContab, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCredito = (parseFloat(totalCredito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalCredito;
    };

    vm.saveDailybookCe = function() {
        vm.loading = true;
        vm.providerCeSelected = undefined;
        vm.dailybookCe.codeTypeContab = vm.selectedTypeBook;
        vm.dailybookCe.nameBank = vm.detailitemb;
        vm.dailybookCe.numCheque = vm.numCheque;
        vm.dailybookCe.typeContab = vm.typeContab;
        vm.dailybookCe.dailyCeSeq = vm.dailyCeSeq;
        vm.dailybookCe.dailyCeStringSeq = vm.dailyCeStringSeq;
        vm.dailybookCe.clientProvName = vm.providerName;
        vm.dailybookCe.generalDetail = vm.generalDetailCe;
        vm.dailybookCe.iva = vm.subIvaCe;
        vm.dailybookCe.subTotalDoce = vm.subTotalDoceCe;
        vm.dailybookCe.subTotalCero = vm.subTotalCeroCe;
        vm.dailybookCe.dateRecordBook = $('#pickerDateRecordBookCe').data("DateTimePicker").date().toDate().getTime();
        contableService.getDailybookCeByUserClientIdAndProvIdAndBillNumber(vm.usrCliId, vm.providerId, vm.dailybookCe.billNumber).then(function(response) {
            if (response.length === 0) {
                contableService.createDailybookCe(vm.dailybookCe).then(function(response) {
                    vm.dailybookCeNew = false;
                    vm.dailybookCeCreated = response;
                    $localStorage.user.cashier.dailyCeNumberSeq = vm.dailybookCe.dailyCeSeq;
                    vm.dailiedCe = true;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.error = 'Ya Existe un Comprobante de Pago para esta Factura';
            };
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

//Funciones para el Diario de Cuentas por Pagar
    vm.providerCxPSelect = function(provider) {
        vm.success = undefined;
        vm.error = undefined;
        vm.loading = true;
        vm.providerCxPSelected = provider;
        vm.providerId = provider.id;
        vm.providerName = provider.razonSocial;
        _getDailyCxPSeqNumber();
        _initializeDailybookCxP()
        vm.loading = false;
        var today = new Date();
        $('#pickerDateRecordBookCxP').data("DateTimePicker").date(today);
        vm.dailybookCxPNew = true;
    };

    vm.consultDailybookCxP = function(provider) {
        vm.success = undefined;
        vm.error = undefined;
        vm.loading = true;
        contableService.getDailybookCxPByProviderId(provider.id).then(function(response) {
            vm.dailybookCxPList = response;
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
        vm.generalDetailCxP = vm.providerName + ' ' + 'Fc' + ' ' + vm.debtsBillNumber;
        vm.dailybookCxP.billNumber = vm.debtsBillNumber;
        vm.dailybookCxP.total = vm.debtsTotal;
        vm.loading = false;
    };

    vm.putGeneralDetailCxP = function() {
        if (vm.generalDetailCxP == null || vm.generalDetailCxP == undefined || vm.generalDetailCxP == '') {
            vm.generalDetailCxP = vm.providerName + ' ' + 'Fc' + ' ' + vm.dailybookCxP.billNumber;
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

    vm.getRetentionByProviderAndDocumentNumber = function() {
        vm.loading = true;
        eretentionService.getRetentionsByProviderIdAndDocumentNumber(vm.providerId, vm.debtsBillNumber).then(function(response) {
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

    vm.getCuentasContablesCxP = function() {
        if (vm.cuenta === true) {
            cuentaContableService.getCuentaContableByUserClientNoBank(vm.usrCliId).then(function(response) {
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

    vm.selectCuentasCxP = function(cuenta) {
        vm.itema = undefined;
        vm.itema = {
            typeContab: vm.typeContab,
            codeConta: cuenta.code,
            descrip: cuenta.description,
            name: cuenta.name
        };
        vm.subTotalDoceCxP = parseFloat((vm.dailybookCxP.total / 1.1200).toFixed(2));
    };

    //Obtiene la Base Imponible para el tipo de IVA 12
    vm.getBaseImponibleCodigo_1 = function() {
        var totalDebito = vm.subTotalDoceCxP;
        if (vm.dailybookCxP) {
            _.each (vm.dailybookCxP.detailDailybookContab, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebito = (parseFloat(totalDebito) - parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalDebito;
    };

    vm.addItemCxP = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCxP.detailDailybookContab.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.itema.tipo == 'DEBITO (D)') {
            vm.itema.deber = vm.itema.baseImponible;
        } else if (vm.itema.tipo == 'CREDITO (C)') {
            vm.itema.haber = vm.itema.baseImponible;
        };
        vm.itema.numCheque =  '--';
        vm.dailybookCxP.detailDailybookContab.push(vm.itema);
        vm.itema = undefined;
        vm.cuenta = undefined;
        vm.cuentaContableList = undefined;
    };

    vm.editItemCxP = function(index) {
        vm.itema = angular.copy(vm.dailybookCxP.detailDailybookContab[index]);
        vm.indexEdit = index;
    };
  
    vm.deleteItemCxP = function(index) {
        vm.dailybookCxP.detailDailybookContab.splice(index, 1);
    };

    vm.getTotalDebitoCxP = function() {
        var totalDebitoCxP = 0;
        if (vm.dailybookCxP) {
            _.each (vm.dailybookCxP.detailDailybookContab, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebitoCxP = (parseFloat(totalDebitoCxP) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoDebitoCxP = totalDebitoCxP;
        return totalDebitoCxP;
    };
  
    vm.getTotalCreditoCxP = function() {
        var totalCreditoCxP = 0;
        if (vm.dailybookCxP) {
            _.each(vm.dailybookCxP.detailDailybookContab, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCreditoCxP = (parseFloat(totalCreditoCxP) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        vm.saldoCreditoCxP = totalCreditoCxP;
        return totalCreditoCxP;
    };
  
    vm.getTotalSaldoCxP = function() {
        var totalSaldoCxP = 0;
        totalSaldoCxP = (parseFloat(vm.saldoCreditoCxP) - parseFloat(vm.saldoDebitoCxP)).toFixed(2);
        return totalSaldoCxP;
    };

    vm.addRetentionToDailybookCxP = function() {
        vm.retenSelected = true;
        // Usuario La Quinta
        if (vm.usrCliId === '758dea84-74f5-4209-b218-9b84c10621fc') {
            if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                switch(vm.retenCodeFuente) {
                    case '302':
                        vm.retenFteCodeContable = '2.01.07.01.001'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '303':
                        vm.retenFteCodeContable = '2.01.07.01.002'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '304':
                        vm.retenFteCodeContable = '2.01.07.01.003'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '307':
                        vm.retenFteCodeContable = '2.01.07.01.004'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '308':
                        vm.retenFteCodeContable = '2.01.07.01.005'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '309':
                        vm.retenFteCodeContable = '2.01.07.01.006'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '310':
                        vm.retenFteCodeContable = '2.01.07.01.007'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '312':
                        vm.retenFteCodeContable = '2.01.07.01.008'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '319':
                        vm.retenFteCodeContable = '2.01.07.01.009'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '320':
                        vm.retenFteCodeContable = '2.01.07.01.010'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '322':
                        vm.retenFteCodeContable = '2.01.07.01.011'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '323':
                        vm.retenFteCodeContable = '2.01.07.01.012'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '327':
                        vm.retenFteCodeContable = '2.01.07.01.013'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '328':
                        vm.retenFteCodeContable = '2.01.07.01.014'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '340':
                        vm.retenFteCodeContable = '2.01.07.01.015'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '341':
                        vm.retenFteCodeContable = '2.01.07.01.016'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '342':
                        vm.retenFteCodeContable = '2.01.07.01.017'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '343':
                        vm.retenFteCodeContable = '2.01.07.01.018'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '344':
                        vm.retenFteCodeContable = '2.01.07.01.019'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                };
            };
            
            if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                switch(vm.retenCodeIva) {
                    case '721':
                        vm.retenIvaCodeContable = '2.01.07.02.001'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '723':
                        vm.retenIvaCodeContable = '2.01.07.02.002'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '725':
                        vm.retenIvaCodeContable = '2.01.07.02.003'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '727':
                        vm.retenIvaCodeContable = '2.01.07.02.004'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '729':
                        vm.retenIvaCodeContable = '2.01.07.02.005'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '731':
                        vm.retenIvaCodeContable = '2.01.07.02.006'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                };
            };
        // Usuario Mr. Zolutions
        } else if (vm.usrCliId === '4907601b-6e54-4675-80a8-ab6503e1dfeb') {
            if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                switch(vm.retenCodeFuente) {
                    case '303':
                        vm.retenFteCodeContable = '2.01.07.03.303'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '304':
                        vm.retenFteCodeContable = '2.01.07.03.304'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '307':
                        vm.retenFteCodeContable = '2.01.07.03.307'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '309':
                        vm.retenFteCodeContable = '2.01.07.03.309'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '310':
                        vm.retenFteCodeContable = '2.01.07.03.310'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '312':
                        vm.retenFteCodeContable = '2.01.07.03.312'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '319':
                        vm.retenFteCodeContable = '2.01.07.03.319'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '320':
                        vm.retenFteCodeContable = '2.01.07.03.320'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '322':
                        vm.retenFteCodeContable = '2.01.07.03.322'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '323':
                        vm.retenFteCodeContable = '2.01.07.03.323'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '325':
                        vm.retenFteCodeContable = '2.01.07.03.325'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '327':
                        vm.retenFteCodeContable = '2.01.07.03.327'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '342':
                        vm.retenFteCodeContable = '2.01.07.03.342'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '344':
                        vm.retenFteCodeContable = '2.01.07.03.344'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                };
            };
  
            if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                switch(vm.retenCodeIva) {
                    case '721':
                        vm.retenIvaCodeContable = '2.01.07.02.721'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '723':
                        vm.retenIvaCodeContable = '2.01.07.02.723'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '725':
                        vm.retenIvaCodeContable = '2.01.07.02.725'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '727':
                        vm.retenIvaCodeContable = '2.01.07.02.727'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '729':
                        vm.retenIvaCodeContable = '2.01.07.02.729'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '731':
                        vm.retenIvaCodeContable = '2.01.07.02.731'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                };
            };
        // Usuario Catedral
        } else if (vm.usrCliId === '1e2049c3-a3bc-4231-a0de-dded8020dc1b') {
            if (vm.retenTaxTypeFuente == 'RETENCION EN LA FUENTE') {
                switch(vm.retenCodeFuente) {
                    case '302':
                        vm.retenFteCodeContable = '2.12.40.201'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '303':
                        vm.retenFteCodeContable = '2.12.40.202'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '304':
                        vm.retenFteCodeContable = '2.12.40.203'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '307':
                        vm.retenFteCodeContable = '2.12.40.204'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '308':
                        vm.retenFteCodeContable = '2.12.40.205'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '309':
                        vm.retenFteCodeContable = '2.12.40.206'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '310':
                        vm.retenFteCodeContable = '2.12.40.207'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '312':
                        vm.retenFteCodeContable = '2.12.40.208'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '319':
                        vm.retenFteCodeContable = '2.12.40.209'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '320':
                        vm.retenFteCodeContable = '2.12.40.210'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '322':
                        vm.retenFteCodeContable = '2.12.40.211'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '323':
                        vm.retenFteCodeContable = '2.12.40.212'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '327':
                        vm.retenFteCodeContable = '2.12.40.213'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '328':
                        vm.retenFteCodeContable = '2.12.40.214'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '340':
                        vm.retenFteCodeContable = '2.12.40.215'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '341':
                        vm.retenFteCodeContable = '2.12.40.216'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '342':
                        vm.retenFteCodeContable = '2.12.40.217'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '343':
                        vm.retenFteCodeContable = '2.12.40.218'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                    case '344':
                        vm.retenFteCodeContable = '2.12.40.219'; vm.retenFteDescContable = 'RETENCIÓN EN FUENTE';
                        vm.retenFteNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenFteValor = parseFloat(vm.retenTotalFuente);
                    break;
                };
            };
            
            if (vm.retenTaxTypeIva == 'RETENCION EN EL IVA') {
                switch(vm.retenCodeIva) {
                    case '725':
                        vm.retenIvaCodeContable = '2.12.40.103'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '729':
                        vm.retenIvaCodeContable = '2.12.40.104'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                    case '731':
                        vm.retenIvaCodeContable = '2.12.40.105'; vm.retenIvaDescContable = 'RETENCIÓN EN IVA';
                        vm.retenIvaNombContable = 'RET:' + ' ' + vm.generalDetailCxP; vm.retenIvaValor = parseFloat(vm.retenTotalIva);
                    break;
                };
            };
        };

        vm.itemRetentionFuente = undefined;
        vm.itemRetentionFuente = { typeContab: vm.typeContab, codeConta: vm.retenFteCodeContable, descrip: vm.retenFteDescContable,
                                    tipo: 'CREDITO (C)', baseImponible: vm.retenFteValor, name: vm.retenFteNombContable, haber: vm.retenFteValor
                                 };
        vm.itemRetentionIVA = undefined;
        vm.itemRetentionIVA = { typeContab: vm.typeContab, codeConta: vm.retenIvaCodeContable, descrip: vm.retenIvaDescContable,
                                tipo: 'CREDITO (C)', baseImponible: vm.retenIvaValor, name: vm.retenIvaNombContable, haber: vm.retenIvaValor
                              };
  
        if (vm.retenCodeFuente != null) {
            vm.itemRetentionFuente.numCheque = '--';
            vm.dailybookCxP.detailDailybookContab.push(vm.itemRetentionFuente);
        };
        
        if (vm.retenCodeIva != null) {
            vm.itemRetentionIVA.numCheque = '--';
            vm.dailybookCxP.detailDailybookContab.push(vm.itemRetentionIVA);
        };

        vm.itemRetentionFuente = undefined;
        vm.itemRetentionIVA = undefined;
        vm.retenCodeFuente = undefined;
        vm.retenCodeIva = undefined;
    };

    vm.addIvaAndProvider = function() {
        if (vm.indexEdit !== undefined) {
            vm.dailybookCxP.detailDailybookContab.splice(vm.indexEdit, 1);
            vm.indexEdit = undefined;
        };
        if (vm.retentionId == null) {
            vm.retentionTotal = 0;
        };
        //Selección de las Cuentas Contables por defecto dependiendo del Cliente
        if (vm.usrCliId === '758dea84-74f5-4209-b218-9b84c10621fc') {
            vm.ivaContable = '1.01.05.01.001'; vm.provContable = '2.01.03.01.001';
        } else if (vm.usrCliId === '4907601b-6e54-4675-80a8-ab6503e1dfeb') {
            vm.ivaContable = '1.01.05.02.001'; vm.provContable = '2.01.03.01.001';
        } else if (vm.usrCliId === '1e2049c3-a3bc-4231-a0de-dded8020dc1b') {
            vm.ivaContable = '1.14.10.201'; vm.provContable = '2.12.10.101';
        } else {
            vm.ivaContable = '1.01.01.01';
            vm.provContable = '2.01.01.01';
        };
      
        vm.subIva = parseFloat((vm.item.baseImponible * 0.1200).toFixed(2));
        vm.subTotalDoce = vm.item.baseImponible;
        vm.subTotalCero = vm.dailybookCxP.total - vm.subTotalDoce - vm.subIva;
        vm.itemIva = {
            typeContab: vm.typeContab,
            codeConta: vm.ivaContable,
            descrip: 'IVA EN COMPRAS',
            tipo: 'DEBITO (D)',
            baseImponible: vm.subIva,
            name: vm.generalDetailCxP,
            deber: vm.subIva
        };
        vm.itemIva.numCheque = '--';
        vm.itemProvider = {
            typeContab: vm.typeContab,
            codeConta: vm.provContable,
            descrip: 'PROVEEDORES LOCALES',
            tipo: 'CREDITO (C)',
            baseImponible: parseFloat((vm.dailybookCxP.total - vm.retentionTotal).toFixed(2)),
            name: vm.generalDetailCxP,
            haber: parseFloat((vm.dailybookCxP.total - vm.retentionTotal).toFixed(2))
        };
        vm.itemProvider.numCheque = '--';
        if (vm.item.baseImponible == 0) {
            vm.dailybookCxP.detailDailybookContab.push(vm.itemProvider);
        } else {
            vm.dailybookCxP.detailDailybookContab.push(vm.itemIva);
            vm.dailybookCxP.detailDailybookContab.push(vm.itemProvider);
        };
    };

    vm.saveDailybookCxP = function() {
        vm.loading = true;
        vm.providerCxPSelected = undefined;
        vm.dailybookCxP.codeTypeContab = vm.selectedTypeBook;
        vm.dailybookCxP.typeContab = vm.typeContab;
        vm.dailybookCxP.dailycxpSeq = vm.dailyCxPSeq;
        vm.dailybookCxP.dailycxpStringSeq = vm.dailyCxPStringSeq;
        vm.dailybookCxP.clientProvName = vm.providerName;
        vm.dailybookCxP.generalDetail = vm.generalDetailCxP;
        vm.dailybookCxP.iva = vm.subIva;
        vm.dailybookCxP.subTotalDoce = vm.subTotalDoce;
        vm.dailybookCxP.subTotalCero = vm.subTotalCero;
        vm.dailybookCxP.dateRecordBook = $('#pickerDateRecordBookCxP').data("DateTimePicker").date().toDate().getTime();
        if (vm.retentionId == null) {
            vm.dailybookCxP.retentionId = 'SIN RETENCIÓN';
            vm.dailybookCxP.retentionNumber = 0;
            vm.dailybookCxP.retentionDateCreated = 0;
            vm.dailybookCxP.retentionTotal = 0.0;
        } else {
            vm.dailybookCxP.retentionId = vm.retentionId;
            vm.dailybookCxP.retentionNumber = vm.retentionNumber;
            vm.dailybookCxP.retentionDateCreated = vm.retentionDateCreated;
            vm.dailybookCxP.retentionTotal = vm.retentionTotal;
        };
        contableService.getDailybookCxPByUserClientIdAndProvIdAndBillNumber(vm.usrCliId, vm.providerId, vm.dailybookCxP.billNumber).then(function(response) {
            if (response.length === 0) {
                contableService.createDailybookCxP(vm.dailybookCxP).then(function(response) {
                    vm.dailybookCxPNew = false;
                    vm.dailybookCxPCreated = response;
                    $localStorage.user.cashier.dailyCppNumberSeq = vm.dailybookCxP.dailycxpSeq;
                    vm.dailiedCxP = true;
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                vm.error = 'Ya Existe un Diario de CxP para esta Factura';
            };
            vm.loading = false;
        }).catch(function(error) {
            vm.loading = false;
            vm.error = error.data;
        });
    };

    vm.getTotalDebitoCxPCreated = function() {
        var totalDebito = 0.0;
        if (vm.dailybookCxPCreated) {
            _.each(vm.dailybookCxPCreated.detailDailybookContab, function(detail) {
                if (detail.tipo === 'DEBITO (D)') {
                    totalDebito = (parseFloat(totalDebito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalDebito;
    };

    vm.getTotalCreditoCxPCreated = function() {
        var totalCredito = 0.0;
        if (vm.dailybookCxPCreated) {
            _.each(vm.dailybookCxPCreated.detailDailybookContab, function(detail) {
                if (detail.tipo === 'CREDITO (C)') {
                    totalCredito = (parseFloat(totalCredito) + parseFloat(detail.baseImponible)).toFixed(2);
                };
            });
        };
        return totalCredito;
    };

    vm.cancelDailybookCxPCreated = function() {
        _activate();
    };

    vm.dailybookCxPSelected = function(dailybookCxP) {
        vm.loading = true;
        vm.dailybookCxPList = undefined;
        contableService.getDailybookCxPById(dailybookCxP.id).then(function(response) {
            vm.dailybookCxPCreated = response;
            vm.dailybookCxP = response;
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
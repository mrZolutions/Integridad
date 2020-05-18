'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProvidersCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('ProvidersCtrl', function(_, holderService, $location, providerService, utilStringService, dateService, eretentionService,
                                            utilSeqService, paymentDebtsService, validatorService, optionConfigCuentasService) {

        var vm = this;
        vm.error = undefined;
        vm.success = undefined;

        vm.loading = false;
        vm.userData = holderService.get();
        vm.subOnlineActive = vm.userData.subsidiary.online;
        vm.provider = undefined;
        vm.providerToUse = undefined;
        vm.providerList = undefined;
        vm.retentionList = undefined;
        vm.optionsList = undefined;
        vm.purchasesOption = 'PROV';
        
        vm.providerType = [
            'PROVEEDORES LOCALES O NACIONALES 01',
            'PROVEEDORES DEL EXTERIOR 02',
        ];

        vm.documentType = [
            {code: '01', name: 'Factura'},
            {code: '02', name: 'Nota o boleta de venta'},
            {code: '03', name: 'Liquidación de compra de Bienes o Prestación de servicios'},
            {code: '04', name: 'Nota de crédito'},
            {code: '05', name: 'Nota de débito'},
            {code: '06', name: 'Guías de Remisión'},
            {code: '07', name: 'Comprobante de Retención'},
            {code: '08', name: 'Boletos o entradas a espectáculos públicos'},
            {code: '09', name: 'Tiquetes o vales emitidos por máquinas registradoras'},
            {code: '11', name: 'Pasajes expedidos por empresas de aviación'},
            {code: '12', name: 'Documentos emitidos por instituciones financieras'},
            {code: '15', name: 'Comprobante de venta emitido en el Exterior'},
            {code: '16', name: 'Formulario Único de Exportación (FUE) o Declaración Aduanera Única (DAU) o Declaración Andina de Valor (DAV)'},
            {code: '18', name: 'Documentos autorizados utilizados en ventas excepto N/C N/D'},
            {code: '19', name: 'Comprobantes de Pago de Cuotas o Aportes'},
            {code: '20', name: 'Documentos por Servicios Administrativos emitidos por Inst. del Estado'},
            {code: '21', name: 'Carta de Porte Aéreo'},
            {code: '22', name: 'RECAP'},
            {code: '23', name: 'Nota de Crédito TC'},
            {code: '24', name: 'Nota de Débito TC'},
            {code: '41', name: 'Comprobante de venta emitido por reembolso'},
            {code: '42', name: 'Documento retención presuntiva y retención emitida por propio vendedor o por intermediario'},
            {code: '43', name: 'Liquidación para Explotación y Exploracion de Hidrocarburos'},
            {code: '44', name: 'Comprobante de Contribuciones y Aportes'},
            {code: '45', name: 'Liquidación por reclamos de aseguradoras'}
        ];

        vm.supportType = [
            {code: '01', name: 'Crédito Tributario para declaración de IVA (servicios y bienes distintos de inventarios y activos fijos)'},
            {code: '02', name: 'Costo o Gasto para declaración de IR (servicios y bienes distintos de inventarios y activos fijos)'},
            {code: '03', name: 'Activo Fijo - Crédito Tributario para declaración de IVA'},
            {code: '04', name: 'Activo Fijo - Costo o Gasto para declaración de IR'},
            {code: '05', name: 'Liquidación Gastos de Viaje, hospedaje y alimentación Gastos IR (a nombre de empleados y no de la empresa)'},
            {code: '06', name: 'Inventario - Crédito Tributario para declaración de IVA'},
            {code: '07', name: 'Inventario - Costo o Gasto para declaración de IR'},
            {code: '08', name: 'Valor pagado para solicitar Reembolso de Gasto (intermediario)'},
            {code: '09', name: 'Reembolso por Siniestros'},
            {code: '10', name: 'Distribución de Dividendos, Beneficios o Utilidades'},
            {code: '11', name: 'Convenios de débito o recaudación para IFI´s'},
            {code: '12', name: 'Impuestos y retenciones presuntivos'},
            {code: '13', name: 'Valores reconocidos por entidades del sector público a favor de sujetos pasivos'},
            {code: '14', name: 'Valores facturados por socios a operadoras de transporte (que no constituyen gasto de dicha operadora)'},
            {code: '00', name: 'Casos especiales cuyo sustento no aplica en las opciones anteriores'}
        ];

        vm.formPayment = [
            {code: '01', name: 'SIN UTILIZACION DEL SISTEMA FINANCIERO'},
            {code: '02', name: 'CHEQUE PROPIO'},
            {code: '03', name: 'CHEQUE CERTIFICADO'},
            {code: '04', name: 'CHEQUE DE GERENCIA'},
            {code: '05', name: 'CHEQUE DEL EXTERIOR'},
            {code: '06', name: 'DÉBITO DE CUENTA'},
            {code: '07', name: 'TRANSFERENCIA PROPIO BANCO'},
            {code: '08', name: 'TRANSFERENCIA OTRO BANCO NACIONAL'},
            {code: '09', name: 'TRANSFERENCIA  BANCO EXTERIOR'},
            {code: '10', name: 'TARJETA DE CRÉDITO NACIONAL'},
            {code: '11', name: 'TARJETA DE CRÉDITO INTERNACIONAL'},
            {code: '12', name: 'GIRO'},
            {code: '13', name: 'DEPOSITO EN CUENTA (CORRIENTE/AHORROS)'},
            {code: '14', name: 'ENDOSO DE INVERSIÒN'},
            {code: '15', name: 'COMPENSACIÓN DE DEUDAS'},
            {code: '16', name: 'TARJETA DE DÉBITO'},
            {code: '17', name: 'DINERO ELECTRÓNICO'},
            {code: '18', name: 'TARJETA PREPAGO'},
            {code: '19', name: 'TARJETA DE CRÉDITO'},
            {code: '20', name: 'OTROS CON UTILIZACION DEL SISTEMA FINANCIERO'},
            {code: '21', name: 'ENDOSO DE TÍTULOS'}
        ];

        vm.purchaseType = [
            {code: 'B', name: 'BIENES'},
            {code: 'S', name: 'SERVICIOS'},
            {code: 'M', name: 'MATERIA PRIMA'},
            {code: 'C', name: 'CONSUMIBLES'},
            {code: 'R', name: 'REEMBOLSO DE GASTOS'},
            {code: 'TAE', name: 'TIKETS AEREOS'}
        ];

        vm.ctasctables = [
            {code: '1.01.05.01.01', desc: 'IVA EN COMPRAS', tipo:'DEBITO (D)', name: 'DEFINIDA PARA TODAS LAS COMPRAS'},
            {code: '2.01.03.01.01', desc: 'PROVEEDORES LOCALES', tipo:'CREDITO (C)', name: 'DEFINIDA PARA TODOS LOS PROVEEDORES'},
            {code: '6.1.03.03', desc: 'SUMINISTROS MATERIALES', tipo:'DEBITO (D)', name: 'COMPRAS PARA INVENTARIO'},
            {code: '6.2.04.04', desc: 'SUMINISTROS DE OFICINA QUITO', tipo:'DEBITO (D)', name: 'SUMINISTROS DE PAPELERIA-COMPUTACION- OTROS'},
            {code: '6.1.03.17', desc: 'SERVICIOS BASICOS COCA', tipo:'DEBITO (D)', name: 'PAGOS BASE COCA - AGUA - LUZ- TELEFONO - INTERNET - OTROS'},
            {code: '6.1.03.18', desc: 'MATERIALES PARA OBRAS', tipo:'DEBITO (D)', name: 'COMPRA DE MATERIALES Y HERRAMIENTAS PARA TRABAJOS- INCLUYE FERRETERIA'},
            {code: '6.03.100.3', desc: 'COMPRAS DE INVENTARIOS', tipo:'DEBITO (D)', name: 'RESORTES - VALVULAS - ACOPLES'}
        ];

        function _activate() {
            vm.today = new Date();
            vm.success = undefined;
            vm.provider = undefined;
            vm.providerId = undefined;
            vm.providerName = undefined;
            vm.providerToUse = undefined;
            vm.totalTotalR = undefined;
            vm.reportStatementProviderSelected = undefined;
            vm.usrCliId = vm.userData.subsidiary.userClient.id;
            vm.providerList = [];
            
            switch(true) {
                case $location.path().includes('/retention'): vm.purchasesOption = 'RETN'; break;
                case $location.path().includes('/reports'): vm.purchasesOption = 'REPT'; break;
            }

            providerService.getLazyByUserClientId(vm.userData.subsidiary.userClient.id).then(function(response) {
                vm.providerList = response;
                vm.loading = false;
                setTimeout(function() {
                    document.getElementById("inputPr0").focus();
                }, 200);
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
            optionConfigCuentasService.getOptionConfigCuentas().then(function(response) {
                vm.optionsList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function create() {
            providerService.getProviderByUserClientIdAndRuc(vm.usrCliId, vm.provider.ruc).then(function(response) {
                if (response.length === 0) {
                    providerService.create(vm.provider).then(function(responseProv) {
                        _activate();
                        vm.error = undefined;
                        vm.success = 'Registro realizado con exito';
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.error = 'El Proveedor Ya Existe';
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function update() {
            providerService.update(vm.provider).then(function(response) {
                _activate();
                if (vm.provider.active) {
                    vm.success = 'Registro actualizado con exito';
                } else {
                    vm.success = 'Registro eliminado con exito';
                };
                vm.error = undefined;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getSeqNumber() {
            vm.numberAddedOne = parseInt(vm.userData.cashier.retentionNumberSeq) + 1;
            vm.seqNumberFirstPart = vm.userData.subsidiary.threeCode + '-' + vm.userData.cashier.threeCode;
            vm.seqNumberSecondPart = utilSeqService._pad_with_zeroes(vm.numberAddedOne, 9);
            vm.seqNumber =  vm.seqNumberFirstPart + '-' + vm.seqNumberSecondPart;
        };

        vm.providerCreate = function() {
            vm.error = undefined;
            vm.success = undefined;
            vm.provider = {
                codeIntegridad: vm.providerList.length + 1,
                active: true,
                userClient: vm.userData.subsidiary.userClient
            };
        };

        vm.editProvider = function(providerEdit) {
            vm.error = undefined;
            vm.success = undefined;
            vm.provider = providerEdit;
        };

        vm.disableSave = function() {
            if (vm.provider) {
                return utilStringService.isAnyInArrayStringEmpty([
                    vm.provider.codeIntegridad,
                    vm.provider.ruc,
                    vm.provider.rucType,
                    vm.provider.name,
                    vm.provider.razonSocial,
                    vm.provider.country,
                    vm.provider.city,
                    vm.provider.address1,
                    vm.provider.contact,
                    vm.provider.providerType
                ]);
            };
        };

        vm.cancel = function() {
            vm.provider = undefined;
            vm.retentionList = undefined;
        };

        vm.register = function() {
            var idValid = true;
            if (vm.provider.rucType === 'CED') {
                idValid = validatorService.isCedulaValid(vm.provider.ruc);
            } else if (vm.provider.rucType === 'RUC') {
                idValid = validatorService.isRucValid(vm.provider.ruc);
            } else if (vm.provider.rucType === 'IEX') {
                idValid = true;
            };
            
            //if (vm.provider.ruc.length > 10) {
            //    idValid = validatorService.isRucValid(vm.provider.ruc);
            //} else {
            //    idValid = validatorService.isCedulaValid(vm.provider.ruc);
            //};

            if (!idValid) {
                vm.error = 'Identificacion invalida';
            } else {
                if (vm.provider.id) {
                    update();
                } else {
                    create();
                };
            };
        };

        vm.remove = function() {
            vm.provider.active=false;
            update();
        };

        vm.createRetention = function(prov) {
            var today = new Date();
            vm.retentionCreated = false;
            vm.providerId = prov.id;
            vm.retention = {
                provider: prov,
                typeRetention: undefined,
                items: [],
                ejercicio: ('0' + (today.getMonth() + 1)).slice(-2) + '/' +today.getFullYear()
            };
            $('#pickerBillDateRetention').data("DateTimePicker").date(today);
            $('#pickerBillDateDocumentRetention').data("DateTimePicker").date(today);
            $('#pickerBillDateDocumentRetention').on("dp.change", function (data) {
                vm.retention.ejercicio = ('0' + ($('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getMonth() + 1)).slice(-2) + '/' +$('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getFullYear();
            });
            _getSeqNumber();
        };

        vm.providerConsultRetention = function(prov) {
            vm.loading = true;
            vm.providerName = prov.name;
            eretentionService.getAllRetentionsByProviderId(prov.id).then(function(response) {
                vm.retentionList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.statementProvider = function(provider) {
            vm.loading = true;
            vm.reportStatementProviderSelected = provider;
            vm.providerName = provider.name;
            vm.providerId = provider.id;
            vm.loading = false;
        };

        vm.getStatementProviderReport = function() {
            vm.loading = true;
            vm.isProductReportList = '1';
            vm.reportList = undefined;
            var dateTwo = $('#pickerStatePDateTwo').data("DateTimePicker").date().toDate().getTime();
            dateTwo += 86399000;

            paymentDebtsService.getStatementProviderReport(vm.providerId, dateTwo).then(function(response) {
                vm.reportList = response;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.exportExcel = function() {
            var dataReport = [];
            if (vm.isProductReportList === '1') {
                _.each(vm.reportList, function(statementReport) {
                    var data = {
                        RUC: statementReport.ruc,
                        PROVEEDOR: statementReport.providerName,
                        FACTURA: statementReport.billNumber,
                        FECHA_COMPRA: statementReport.fechCompra !== null ? new Date(statementReport.fechCompra) : statementReport.fechCompra,
                        FECHA_VENCE: statementReport.fechVence !== null ? new Date(statementReport.fechVence) : statementReport.fechVence,
                        FECHA_PAGO: statementReport.fechPago !== null ? new Date(statementReport.fechPago) : statementReport.fechPago,
                        DOC_INTR: statementReport.debtsSeq,
                        RET_NRO: statementReport.retenNumber,
                        DETALLE: statementReport.detalle,
                        OBSERV: statementReport.observacion,
                        MOD_PGO: statementReport.modePayment,
                        CHQ_NRO: statementReport.numCheque,
                        TOTAL: parseFloat(statementReport.total.toFixed(2)),
                        V_ABONO: parseFloat(statementReport.valorAbono.toFixed(2)),
                        V_RETEN: parseFloat(statementReport.valorReten.toFixed(2)),
                        SALDO: parseFloat(statementReport.saldo.toFixed(2))
                    };
        
                    dataReport.push(data);
                });
            };
      
            var ws = XLSX.utils.json_to_sheet(dataReport);
            /* add to workbook */
            var wb = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(wb, ws, "Estado de Cuenta");
      
            /* write workbook and force a download */
            XLSX.writeFile(wb, "EstadoCtaProveedor.xlsx");
        };

        vm.getPercentageTable = function() {
            vm.tablePercentage = undefined;
            var typeSelected = vm.retention.typeRetention === '2' ? 'IVA' : 'FUENTE';
            vm.tablePercentage = _.filter(vm.optionsList, function(opt){ return opt.type === typeSelected; });

        };

        vm.selecPercentage = function(percentage) {
            vm.baseImponibleItem = undefined;
            vm.item = undefined;
            vm.item = {
                codigo: parseInt(vm.retention.typeRetention),
                fecha_emision_documento_sustento: dateService.getIsoDate($('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate()),
                numero_documento_sustento: vm.retention.numero,
                codigo_porcentaje: percentage.codigoDatil,
                codigo_porcentaje_integridad: percentage.codigo,
                porcentaje: percentage.percentage,
                tipo_documento_sustento: vm.docType
            };
        };

        vm.addItem = function() {
            vm.item.valor_retenido = (parseFloat(vm.item.base_imponible) * (parseFloat(vm.item.porcentaje)/100)).toFixed(2);
            if (vm.indexEdit !== undefined) {
                vm.retention.items.splice(vm.indexEdit, 1);
                vm.indexEdit = undefined
            };
            vm.retention.retentionSeq = vm.seqNumber;
            vm.retention.items.push(vm.item);
            vm.item = undefined;
            vm.retention.typeRetention = undefined;
            vm.tablePercentage = undefined;
        };

        vm.editItem = function(index) {
            vm.item = angular.copy(vm.retention.items[index]);
            vm.indexEdit = index;
        };

        vm.editItemTaxes = function(index) {
            vm.item = angular.copy(vm.debtsToPay.items[index]);
            vm.indexEdit = index;
        };

        vm.deleteItem = function(index) {
            vm.retention.items.splice(index, 1);
        };

        vm.getTotalRetenciones = function() {
            var totalRetorno = 0;
            if (vm.retention) {
                _.each(vm.retention.items, function(detail) {
                    totalRetorno = (parseFloat(totalRetorno) + parseFloat(detail.valor_retenido)).toFixed(2);
                });
                vm.retention.total = totalRetorno;
            };
            
            return totalRetorno;
        };

        vm.previsualisation = function() {
            vm.retention.documentDate = $('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getTime();
            vm.retention.stringSeq = vm.retention.retentionSeq;
            vm.retention.detailRetentions = [];
            vm.retention.ejercicioFiscal = vm.retention.ejercicio;
            vm.retention.documentNumber = vm.retention.documentNumber;
            vm.totalRetention = 0;
            _.each(vm.retention.items, function(item) {
                vm.totalRetention = parseFloat(vm.totalRetention + item.valor_retenido);
            });
        };

        vm.retentionSelected = function(retention) {
            vm.loading = true;
            vm.error = undefined;
            vm.retentionList = undefined;
            eretentionService.getRetentionById(retention.id).then(function(response) {
                vm.retentionCreated = true;
                vm.retention = response;
                vm.totalRetention = 0;
                _.each(vm.retention.detailRetentions, function(detail) {
                    vm.totalRetention = vm.totalRetention + detail.total;
                });
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.retentionDeactivate = function() {
            vm.loading = true;
            vm.error = undefined;
            var index = vm.retentionList.indexOf(vm.deactivateRetention);
            eretentionService.cancelRetention(vm.deactivateRetention).then(function(response) {
                var index = vm.retentionList.indexOf(vm.deactivateRetention);
                if (index > -1) {
                    vm.retentionList.splice(index, 1);
                };
                vm.deactivateRetention = undefined;
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.printToCart = function(printRetentionId) {
            var innerContents = document.getElementById(printRetentionId).innerHTML;
            var popupWinindow = window.open('', 'printMatrixRetentionId', 'width=400,height=500');
            popupWinindow.document.write('<html><head><title>printMatrixRetentionId</title>');
            popupWinindow.document.write('</head><body>');
            popupWinindow.document.write(innerContents);
            popupWinindow.document.write('</body></html>');
            popupWinindow.print();
            popupWinindow.close();
        };

        vm.downloadRetentionTxtTM20 = function(retentionToDownloadEpsonTM20) {
            var a = document.body.appendChild(document.createElement("a"));
            a.href = "data:text/plain; charset=utf-8," + document.getElementById(retentionToDownloadEpsonTM20).innerText;
            a.download = "retentionDownloaded.txt";
            a.click();
            document.body.removeChild(a);
        };

        vm.getClaveAcceso = function() {
            vm.loading = true;
            var totalRetent = 0;
            eretentionService.getRetentionByProviderIdAndDocumentNumber(vm.providerId, vm.retention.numero).then(function(response) {
                if (response.length === 0) {
                    var eRet = eretentionService.createERetention(vm.retention, vm.userData);
                    eretentionService.getClaveDeAcceso(eRet, vm.userData.subsidiary.userClient.id).then(function(resp) {
                        var obj = JSON.parse(resp.data);
                        //var obj = {clave_acceso: '1234560', id:'id12345'};
                        if (obj.errors === undefined) {
                            vm.retention.claveDeAcceso = obj.clave_acceso;
                            vm.retention.idSri = obj.id;
                            vm.retention.stringSeq = vm.retention.retentionSeq;
                            vm.retention.retSeq = eRet.secuencial;
                            vm.retention.ejercicioFiscal = vm.retention.ejercicio;
                            vm.retention.documentNumber = vm.retention.numero;
                            vm.retention.documentDate = $('#pickerBillDateDocumentRetention').data("DateTimePicker").date().toDate().getTime();
                            vm.retention.userIntegridad = vm.userData;
                            vm.retention.subsidiary = vm.userData.subsidiary;
                            vm.retention.typeDocument = vm.docType;
                            vm.retention.detailRetentions = [];
                            _.each(vm.retention.items, function(item) {
                                var detail = {
                                    taxType: item.codigo === String(1) ? 'RETENCION EN LA FUENTE' : 'RETENCION EN EL IVA',
                                    code: item.codigo_porcentaje_integridad,
                                    baseImponible: item.base_imponible,
                                    percentage: item.porcentaje,
                                    total: item.valor_retenido
                                };
                                vm.retention.detailRetentions.push(detail);
                            });
                            eretentionService.create(vm.retention).then(function(respRetention) {
                                vm.retention = respRetention;
                                vm.totalRetention = 0;
                                _.each(vm.retention.detailRetentions, function(detail) {
                                    vm.totalRetention = parseFloat(vm.totalRetention + detail.total);
                                });
                                vm.retentionCreated = true;
                                vm.userData.cashier.retentionNumberSeq = parseInt(vm.userData.cashier.retentionNumberSeq) + 1;
                                holderService.set(vm.userData);
                                vm.loading = false;
                            }).catch(function(error) {
                                vm.loading = false;
                                vm.error = error;
                            });
                        } else {
                            vm.loading = false;
                            vm.error = "Error al obtener Clave de Acceso: " + JSON.stringify(obj.errors);
                        };
                    }).catch(function(error) {
                        vm.loading = false;
                        vm.error = error.data;
                    });
                } else {
                    vm.error = 'Ya Existe una Retención para esta Factura';
                };
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        vm.cancelRetentionCreated = function() {
            vm.retentionCreated = false;
            vm.retention = undefined
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});
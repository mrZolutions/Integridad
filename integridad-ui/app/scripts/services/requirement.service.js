angular
    .module('app.services')
    .service('requirementService', function(dateService) {
        function getTipyCode(code) {
            tipyIdCode = {
                RUC: '04',
                CED: '05',
                IEX: '08'
            };
            return tipyIdCode[code];
        };

        this.createRequirement = function(clientSelected, bill, user, impuestosTotales, items, pagos) {
            var pagosDatil = [];
            var credito = undefined;
            _.each(pagos, function(pago){
                if (pago.code !== 'credito') {
                    pago.medio = pago.code;
                    pagosDatil.push(pago);
                } else {
                    if (credito === undefined) {
                        credito = {};
                    };
                    var maxDateCred = _.max(pago.credits, function(cred) { return cred.fecha; });
                    credito.monto = pago.total;
                    var dateToFormat = new Date(maxDateCred.fecha);
                    var yyyy = dateToFormat.getFullYear().toString();
                    var mm = (dateToFormat.getMonth()+1).toString();
                    var dd  = dateToFormat.getDate().toString();
                    var mmChars = mm.split('');
                    var ddChars = dd.split('');
                    credito.fecha_vencimiento = yyyy + '-' + (mmChars[1]?mm: "0" + mmChars[0]) + '-' + (ddChars[1]?dd: "0" + ddChars[0]);
                };
            });
            var req = {
                "ambiente": 2,
                "tipo_emision": 1,
                "secuencial": bill.billSeq,
                "fecha_emision": dateService.getIsoDate($('#pickerBillDate').data("DateTimePicker").date().toDate()),
                "emisor": {
                    "ruc": user.cashier.subsidiary.userClient.ruc,
                    "obligado_contabilidad": user.cashier.subsidiary.userClient.contab,
                    "contribuyente_especial": "",
                    "nombre_comercial": user.cashier.subsidiary.userClient.name,
                    "razon_social": user.cashier.subsidiary.userClient.name,
                    "direccion": user.cashier.subsidiary.userClient.address1,
                    "agent_retention": user.cashier.subsidiary.userClient.agentRetention,
                    "retention_data": user.cashier.subsidiary.userClient.retentionData,
                    "retention_data_long": user.cashier.subsidiary.userClient.retentionDataLong,
                    "microempresa": user.cashier.subsidiary.userClient.microempresa,
                    "rimpe": user.cashier.subsidiary.userClient.rimpe,
                    "establecimiento": {
                        "punto_emision": user.cashier.threeCode,
                        "codigo": user.cashier.subsidiary.threeCode,
                        "direccion": user.cashier.subsidiary.address1
                    }
                },
                "moneda":"USD",
                "totales": {
                    "total_sin_impuestos": parseFloat((bill.subTotal).toFixed(4)),
                    "impuestos": impuestosTotales,
                    "importe_total": parseFloat((bill.total).toFixed(4)),
                    "propina": 0.0,
                    "descuento": parseFloat((bill.discount).toFixed(4))
                },
                "comprador": {
                    "email": clientSelected.email,
                    "identificacion": clientSelected.identification,
                    "tipo_identificacion": getTipyCode(clientSelected.typeId),
                    "razon_social": clientSelected.name,
                    "direccion": clientSelected.address,
                    "telefono": clientSelected.phone
                },
                "items": items,
                "valor_retenido_iva": 0,
                "valor_retenido_renta": 0,
            };
            if (user.cashier.subsidiary.userClient.testMode) {
                req.ambiente = 1;
            };
            if (user.cashier.subsidiary.userClient.espTemp === 'A-1') {
                req.comprador.email = req.comprador.email + ', facturacionelecppe2018@gmail.com, facturacionppecoca@gmail.com';
                var infoAdicional = {
                    info1: 'ORDEN DE COMPRA: ' + bill.ordenDecompra,
                    info2: 'OTI/OTIR: ' + bill.otir
                };
                req.informacion_adicional = infoAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-2') {
                req.comprador.email = req.comprador.email + ', ferrelozada@yahoo.com';
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-3') {
                if(user.cashier.subsidiary.userClient.codeIntegridad == 'VSEP-01') {
                    req.comprador.email = req.comprador.email + ', gerencia@equiposdentalesecuador.com';
                } else {
                    req.comprador.email = req.comprador.email + ', facturacion@mrzolutions.com';
                }
                
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-4') {
                req.comprador.email = req.comprador.email + ', facturacion@catedral.com.ec';
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-QTA') {
                req.comprador.email = req.comprador.email + ', facturaslaquintarm@hotmail.com';
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-TRANS') {
                req.comprador.email = req.comprador.email + ', facturacion@transportelospuentessa.com';
                var informAdicional = {
                    observ: ' ' + bill.observation,
                    placa: bill.placa,
                    punto_emision: user.cashier.threeCode,
                    telefono: user.phone,
                    ruc:user.ruc,
                    email: user.email,
                    name: user.firstName + ' ' + user.lastName,
                    direccion: user.address1, 
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-ZIP') {
                req.comprador.email = req.comprador.email + ', contabilidad@ziponlineshop.com';
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-ESC') {
                req.comprador.email = req.comprador.email + ', facturasgbs17h00420@gmail.com';
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-5') {
                req.comprador.email = req.comprador.email + ', farmacia.novapiel@gmail.com';
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-6') {
                req.comprador.email = req.comprador.email + ', ' + user.email;
                var informAdicional = {
                    observ: ' ' + bill.observation
                };
                req.informacion_adicional = informAdicional;
            };

            if (credito !== undefined) {
                req.credito = credito;
            };
            if (!_.isEmpty(pagosDatil)) {
                req.pagos = pagosDatil;
            };
            return req;
        };
});
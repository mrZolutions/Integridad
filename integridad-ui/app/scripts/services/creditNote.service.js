angular
    .module('app.services')
    .service('creditNoteService', function(dateService, securityService) {
        function getTipyCode(code) {
            tipyIdCode = {
                RUC: '04',
                CED: '05',
                IEX: '08'
            };
            return tipyIdCode[code];
        };

        this.createRequirement = function(clientSelected, bill, user, impuestosTotales, items) {
            var req = {
                "ambiente": 2,
                "tipo_emision": 1,
                "secuencial": bill.creditSeq,
                "fecha_emision": dateService.getIsoDate($('#pickerCreditNoteDate').data("DateTimePicker").date().toDate()),
                "fecha_emision_documento_modificado": dateService.getIsoDate(new Date(bill.dateCreated)),
                "numero_documento_modificado":bill.stringSeq,
                "tipo_documento_modificado": '01',
                "motivo": bill.motivo,
                "emisor": {
                    "ruc": user.cashier.subsidiary.userClient.ruc,
                    "obligado_contabilidad": true,
                    "contribuyente_especial": "",
                    "nombre_comercial": user.cashier.subsidiary.userClient.name,
                    "razon_social": user.cashier.subsidiary.userClient.name,
                    "direccion": user.cashier.subsidiary.userClient.address1,
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
                    "importe_total": parseFloat((bill.total).toFixed(4))
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
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-4') {
                req.comprador.email = req.comprador.email + ', facturacion@catedral.com.ec';
            } else if (user.cashier.subsidiary.userClient.espTemp === 'A-QTA') {
                req.comprador.email = req.comprador.email + ', facturaslaquintarm@hotmail.com';
            }else if (user.cashier.subsidiary.userClient.espTemp === 'A-3') {
                req.comprador.email = req.comprador.email + ', facturacion@mrzolutions.com';
            };
            return req;
        };

        this.getClaveDeAcceso = function(req, id) {
            return securityService.post('/creditnote/clave_acceso/' + id, req).then(function successCallback(response) {
                return response;
            });
        };

        this.create = function(bill) {
            return securityService.post('/creditnote', bill).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCreditsNoteByClientId = function(id) {
            return securityService.get('/creditnote/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCreditNoteById = function(id) {
            return securityService.get('/creditnote/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCreditNotesByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/creditnote/rep/creditnote/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
angular
    .module('app.services')
    .service('contableService', function(securityService) {
        //Diario Contabilidad General
        this.createDailybookCg = function(dailybookCg) {
            return securityService.post('/contable/dailycg', dailybookCg).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCgById = function(id) {
            return securityService.get('/contable/dailycg/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCg = function(dailybookCg) {
            return securityService.put('/contable/dailycg', dailybookCg).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCgByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailycg/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };
        //Comprobante de Egreso
        this.createDailybookCe = function(dailybookCe) {
            return securityService.post('/contable/dailyce', dailybookCe).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeById = function(id) {
            return securityService.get('/contable/dailyce/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCe = function(dailybookCe) {
            return securityService.put('/contable/dailyce', dailybookCe).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByProviderId = function(id) {
            return securityService.get('/contable/dailyce/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailyce/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByUserClientIdWithNoProvider = function(userclientId) {
            return securityService.get('/contable/dailyce/userclient/noprovider/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByUserClientIdAndProvIdAndBillNumber = function(userClientId, provId, billNumber) {
            return securityService.get('/contable/dailyce/userclient/' + userClientId + '/' + provId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };
        //Comprobante de Ingreso
        this.createDailybookCi = function(dailybookCi) {
            return securityService.post('/contable/dailyci', dailybookCi).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiById = function(id) {
            return securityService.get('/contable/dailyci/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCi = function(dailybookCi) {
            return securityService.put('/contable/dailyci', dailybookCi).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByClientId = function(id) {
            return securityService.get('/contable/dailyci/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailyci/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByUserClientIdWithNoClient = function(userclientId) {
            return securityService.get('/contable/dailyci/userclient/noclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByUserClientIdAndClientIdAndBillNumber = function(userClientId, clientId, billNumber) {
            return securityService.get('/contable/dailyci/userclient/' + userClientId + '/' + clientId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };
        //Diario Cuentas por Pagar
        this.createDailybookCxP = function(dailybookCxP) {
            return securityService.post('/contable/dailycxp', dailybookCxP).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPById = function(id) {
            return securityService.get('/contable/dailycxp/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCxP = function(dailybookCxP) {
            return securityService.put('/contable/dailycxp', dailybookCxP).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPByProviderId = function(id) {
            return securityService.get('/contable/dailycxp/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailycxp/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPByUserClientIdAndProvIdAndBillNumber = function(userClientId, provId, billNumber) {
            return securityService.get('/contable/dailycxp/userclient/' + userClientId + '/' + provId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };

        //Comprobante de Factura-Venta
        this.createDailybookFv = function(dailybookFv) {
            return securityService.post('/contable/dailyfv', dailybookFv).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvById = function(id) {
            return securityService.get('/contable/dailyfv/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookFv = function(dailybookFv) {
            return securityService.put('/contable/dailyfv', dailybookFv).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByClientId = function(id) {
            return securityService.get('/contable/dailyfv/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailyfv/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByUserClientIdWithNoClient = function(userclientId) {
            return securityService.get('/contable/dailyfv/userclient/noclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByUserClientIdAndClientIdAndBillNumber = function(userClientId, clientId, billNumber) {
            return securityService.get('/contable/dailyfv/userclient/' + userClientId + '/' + clientId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };
});
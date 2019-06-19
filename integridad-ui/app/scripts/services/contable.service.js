angular
    .module('app.services')
    .service('contableService', function(securityService) {
        //Diario Contabilidad General
        this.createDailybookCg = function(dailybookCg) {
            return securityService.post('/dailycg', dailybookCg).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCgById = function(id) {
            return securityService.get('/dailycg/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCg = function(dailybookCg) {
            return securityService.put('/dailycg', dailybookCg).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCgByUserClientId = function(userclientId) {
            return securityService.get('/dailycg/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };
        //Comprobante de Egreso
        this.createDailybookCe = function(dailybookCe) {
            return securityService.post('/dailyce', dailybookCe).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeById = function(id) {
            return securityService.get('/dailyce/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCe = function(dailybookCe) {
            return securityService.put('/dailyce', dailybookCe).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByProviderId = function(id) {
            return securityService.get('/dailyce/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByUserClientId = function(userclientId) {
            return securityService.get('/dailyce/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByUserClientIdWithNoProvider = function(userclientId) {
            return securityService.get('/dailyce/userclient/noprovider/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCeByUserClientIdAndProvIdAndBillNumber = function(userClientId, provId, billNumber) {
            return securityService.get('/dailyce/userclient/' + userClientId + '/' + provId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };
        //Comprobante de Ingreso
        this.createDailybookCi = function(dailybookCi) {
            return securityService.post('/dailyci', dailybookCi).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiById = function(id) {
            return securityService.get('/dailyci/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCi = function(dailybookCi) {
            return securityService.put('/dailyci', dailybookCi).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByClientId = function(id) {
            return securityService.get('/dailyci/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByUserClientId = function(userclientId) {
            return securityService.get('/dailyci/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByUserClientIdWithNoClient = function(userclientId) {
            return securityService.get('/dailyci/userclient/noclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCiByUserClientIdAndClientIdAndBillNumber = function(userClientId, clientId, billNumber) {
            return securityService.get('/dailyci/userclient/' + userClientId + '/' + clientId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };
        //Diario Cuentas por Pagar
        this.createDailybookCxP = function(dailybookCxP) {
            return securityService.post('/dailycxp', dailybookCxP).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPById = function(id) {
            return securityService.get('/dailycxp/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCxP = function(dailybookCxP) {
            return securityService.put('/dailycxp', dailybookCxP).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPByProviderId = function(id) {
            return securityService.get('/dailycxp/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPByUserClientId = function(userclientId) {
            return securityService.get('/dailycxp/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCxPByUserClientIdAndProvIdAndBillNumber = function(userClientId, provId, billNumber) {
            return securityService.get('/dailycxp/userclient/' + userClientId + '/' + provId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };

        //Comprobante de Factura-Venta
        this.createDailybookFv = function(dailybookFv) {
            return securityService.post('/dailyfv', dailybookFv).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvById = function(id) {
            return securityService.get('/dailyfv/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookFv = function(dailybookFv) {
            return securityService.put('/dailyfv', dailybookFv).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByClientId = function(id) {
            return securityService.get('/dailyfv/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByUserClientId = function(userclientId) {
            return securityService.get('/dailyfv/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByUserClientIdWithNoClient = function(userclientId) {
            return securityService.get('/dailyfv/userclient/noclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookFvByUserClientIdAndClientIdAndBillNumber = function(userClientId, clientId, billNumber) {
            return securityService.get('/dailyfv/userclient/' + userClientId + '/' + clientId + '/' + billNumber).then(function successCallback(response) {
                return response.data;
            });
        };
});
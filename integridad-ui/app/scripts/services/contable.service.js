angular
    .module('app.services')
    .service('contableService', function(securityService) {
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
});
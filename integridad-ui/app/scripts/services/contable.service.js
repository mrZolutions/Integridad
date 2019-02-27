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
});
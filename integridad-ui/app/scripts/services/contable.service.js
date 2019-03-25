angular
    .module('app.services')
    .service('contableService', function(securityService) {
        this.createDailybookCg = function(dailybook) {
            return securityService.post('/contable/dailycg', dailybook).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCgById = function(id) {
            return securityService.get('/contable/dailycg/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCg = function(dailybook) {
            return securityService.put('/contable/dailycg', dailybook).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCgByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailycg/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.createDailybookCpp = function(dailybook) {
            return securityService.post('/contable/dailycpp', dailybook).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCppById = function(id) {
            return securityService.get('/contable/dailycpp/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateDailybookCpp = function(dailybook) {
            return securityService.put('/contable/dailycpp', dailybook).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDailybookCppByUserClientId = function(userclientId) {
            return securityService.get('/contable/dailycpp/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };
});
angular
    .module('app.services')
    .service('cellarService', function(securityService) {
        this.create = function(cellar) {
            return securityService.post('/cellar', cellar).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getAllCellarsPendingOfWarehouse = function (id) {
            return securityService.get('/cellar/warehouse/pending/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllCellarById = function(id) {
            return securityService.get('/cellar/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCellarsByProviderId = function(id) {
            return securityService.get('/cellar/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCellarsByProviderIdAndNoCN = function(id) {
            return securityService.get('/cellar/provider/nocrednot/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.validateCellar = function(cellar) {
            return securityService.put('/cellar', cellar).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDetailsOfCellarsByUserClientId = function(id) {
            return securityService.get('/cellar/details/details/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getByUserClientIdAndDatesActives = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/cellar/rep/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getByUserClientIdAndBillNumberActive = function(userClientId, billNum) {
            return securityService.get('/cellar/userclient/' + userClientId + '/' + billNum).then(function successCallback(response) {
                return response.data;
            });
        };
});
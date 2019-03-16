angular
    .module('app.services')
    .service('consumptionService', function(securityService) {
        this.create = function(consumption) {
            return securityService.post('/consumption', consumption).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllConsumptionById = function(id) {
            return securityService.get('/consumption/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllConsumptionByClientId = function(id) {
            return securityService.get('/consumption/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getActivesByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/consumption/rep/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
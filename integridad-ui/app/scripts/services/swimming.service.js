angular
    .module('app.services')
    .service('swimmingService', function(securityService) {
        this.createSwimmPool = function(swimmPool) {
            return securityService.post('/swimm', swimmPool).then(function successCallback(response) {
                return response.data;
            });
        };

        this.validateSwimmPool = function(subId, barCode) {
            return securityService.put('/swimm/' + subId + '/' + barCode).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateSwimmPool = function(swimmPool) {
            return securityService.put('/swimm', swimmPool).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolById = function(id) {
            return securityService.get('/swimm/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolActivesBySubIdAndDates = function(subId, dateOne, dateTwo) {
            return securityService.get('/swimm/rep/' + subId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolActivesBySubIdAndBarCode = function(subId, barCode) {
            return securityService.get('/swimm/barcode/active/' + subId + '/' + barCode).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllSwimmPoolBySubIdAndBarCode = function(subId, barCode) {
            return securityService.get('/swimm/barcode/all/' + subId + '/' + barCode).then(function successCallback(response) {
                return response.data;
            });
        };
});
angular
    .module('app.services')
    .service('swimmingService', function(securityService) {
        this.createSwimmPool = function(swimmPool) {
            return securityService.post('/swimm', swimmPool).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateSwimmPool = function(subId, barCode) {
            return securityService.put('/swimm/' + subId + '/' + barCode).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolById = function(id) {
            return securityService.get('/swimm/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolByClientId = function(id) {
            return securityService.get('/swimm/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolBySubIdAndBarCodeActive = function(subId, barCode) {
            return securityService.get('/swimm/barcode/active/' + subId + '/' + barCode).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getSwimmPoolBySubIdAndBarCodeAll = function(subId, barCode) {
            return securityService.get('/swimm/barcode/all/' + subId + '/' + barCode).then(function successCallback(response) {
                return response.data;
            });
        };
});
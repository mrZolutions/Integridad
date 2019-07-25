angular
    .module('app.services')
    .service('creditNoteCellarService', function(dateService, securityService) {
        this.create = function(cellar) {
            return securityService.post('/crednotcellar', cellar).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCreditsNoteCellarByProviderId = function(id) {
            return securityService.get('/crednotcellar/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCreditNoteCellarById = function(id) {
            return securityService.get('/crednotcellar/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
});
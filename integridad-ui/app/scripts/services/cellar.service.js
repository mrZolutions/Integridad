angular
    .module('app.services')
    .service('cellarService', function(securityService) {
        this.create = function(cellar) {
            return securityService.post('/cellar', cellar).then(function successCallback(response) {
                return response.data;
            });
        };
});
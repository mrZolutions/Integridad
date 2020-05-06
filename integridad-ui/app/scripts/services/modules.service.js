angular
    .module('app.services')
    .service('modulesService', function (securityService) {
        this.getAll = function() {
            return securityService.get('/modules').then(function successCallback(response) {
                return response.data;
            });
        };
});
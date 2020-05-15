angular
    .module('app.services')
    .service('processCashierService', function (securityService) {
        this.save = function(processCashier) {
            return securityService.post('/processCashier', processCashier).then(function successCallback(response) {
                return response.data;
            });
        };
});
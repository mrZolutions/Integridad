angular
    .module('app.services')
    .service('paymentDebtsService', function(securityService) {
        this.create = function(paymentDebts) {
            return securityService.post('/paymentdebts', paymentDebts).then(function successCallback(response) {
                return response.data;
            });
        };
});
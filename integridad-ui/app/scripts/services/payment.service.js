angular
    .module('app.services')
    .service('paymentService', function(securityService) {
        this.create = function(payment) {
            return securityService.post('/payment', payment).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getAllPaymentsByUserClientId = function(userClientId) {
            return securityService.get('/payment/rep/ccresreport/' + userClientId).then(function successCallback(response) {
                return response.data;
            });
        };
});
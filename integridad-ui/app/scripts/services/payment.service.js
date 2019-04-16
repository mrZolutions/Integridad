angular
    .module('app.services')
    .service('paymentService', function(securityService) {
        this.create = function(payment) {
            return securityService.post('/payment', payment).then(function successCallback(response) {
                return response.data;
            });
        };
        
        this.getAllPaymentsByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/payment/rep/ccresreport/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
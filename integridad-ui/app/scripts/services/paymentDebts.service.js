angular
    .module('app.services')
    .service('paymentDebtsService', function(securityService) {
        this.create = function(paymentDebts) {
            return securityService.post('/paymentdebts', paymentDebts).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentsDebtsByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/paymentdebts/rep/ccrespdreport/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
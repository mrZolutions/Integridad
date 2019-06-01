angular
    .module('app.services')
    .service('paymentDebtsService', function(securityService) {
        this.createPaymentsDebts = function(paymentDebts) {
            return securityService.post('/paymentdebts', paymentDebts).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentsDebtsByUserClientIdWithBankAndNroDocument = function(userClientId, banco, nrodoc) {
            return securityService.get('/paymentdebts/userclient/' + userClientId + '/' + banco + '/' + nrodoc).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentsDebtsByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/paymentdebts/rep/ccrespdreport/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getStatementProviderReport = function(id, dateTwo) {
            return securityService.get('/paymentdebts/rep/statement/' + id + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
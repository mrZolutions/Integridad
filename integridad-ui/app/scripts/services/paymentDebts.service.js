angular
    .module('app.services')
    .service('paymentDebtsService', function(securityService) {
        this.createPaymentDebts = function(paymentDebts) {
            return securityService.post('/paymentdebts', paymentDebts).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivatePaymentDebts = function(paymentDebts) {
            return securityService.put('/paymentdebts', paymentDebts).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentDebtsByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/paymentdebts/rep/ccrespdreport/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getStatementProviderReport = function(id, dateTwo) {
            return securityService.get('/paymentdebts/rep/statement/' + id + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentDebtsByUserClientIdWithBankAndNroDocument = function(userClientId, banco, nrodoc) {
            return securityService.get('/paymentdebts/userclient/' + userClientId + '/' + banco + '/' + nrodoc).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentDebtsByProviderId = function(id) {
            return securityService.get('/paymentdebts/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
});
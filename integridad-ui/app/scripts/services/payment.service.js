angular
    .module('app.services')
    .service('paymentService', function(securityService) {
        this.createPayment = function(payment) {
            return securityService.post('/payment', payment).then(function successCallback(response) {
                return response.data;
            });
        };

        this.createPaymentList = function(payments) {
            return securityService.post('/payment/paymentList', payments).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivatePayment = function(payment) {
            return securityService.put('/payment', payment).then(function successCallback(response) {
                return response.data;
            });
        };
        
        this.getAllPaymentsByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/payment/rep/ccresreport/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentsByUserClientIdWithBankAndNroDocument = function(userClientId, banco, nrodoc) {
            return securityService.get('/payment/userclient/' + userClientId + '/' + banco + '/' + nrodoc).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getPaymentsByClientId = function(id) {
            return securityService.get('/payment/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
});
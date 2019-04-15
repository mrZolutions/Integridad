angular
    .module('app.services')
    .service('debtsToPayService', function(securityService) {
        this.create = function(debtsToPay) {
            return securityService.post('/debts', debtsToPay).then(function successCallback(response) {
                return response.data;
            });
        };

        this.update = function(debtsToPay) {
            return securityService.put('/debts', debtsToPay).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllDebtsToPayByProviderId = function(id) {
            return securityService.get('/debts/debts/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDebtsToPayWithSaldoByProviderId = function(id) {
            return securityService.get('/debts/debts/provider/credit/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getById = function(id) {
            return securityService.get('/debts/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDebtsToPayByDebtsSeqAndSubId = function(stringSeq, subsidiaryId) {
            return securityService.get('/debts/seq/' + stringSeq + '/' + subsidiaryId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.cancelDebtsToPay = function(debtsToPay) {
            return securityService.put('/debts', debtsToPay).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDebtsToPayByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/debts/rep/purchases/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getDebtsToPayByBillNumberAndAuthoNumber = function(userClientId, billNumber, authoNumber) {
            return securityService.get('/debts/userclient/' + userClientId + '/' + billNumber + '/' + authoNumber).then(function successCallback(response) {
                return response.data;
            });
        };
});
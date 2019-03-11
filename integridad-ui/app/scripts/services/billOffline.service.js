angular
    .module('app.services')
    .service('billOfflineService', function(securityService) {

        this.createBillOffline = function(bill, type) {
            return securityService.post('/billOff/' + type, bill).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByClientId = function(id) {
            return securityService.get('/billOff/bill/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByClientIdWithSaldo = function(id) {
            return securityService.get('/billOff/bill/client/saldo/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineById = function(id) {
            return securityService.get('/billOff/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByStringSeq = function(stringSeq, subsidiaryId) {
            return securityService.get('/bill/seq/' + stringSeq + '/' + subsidiaryId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineActivesByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/bill/rep/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/bill/rep/sales/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});

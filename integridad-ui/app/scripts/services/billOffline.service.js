angular
    .module('app.services')
    .service('billOfflineService', function(securityService) {
        this.createBillOffline = function(requirement, type) {
            return securityService.post('/billoffline/' + type, requirement).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByClientId = function(id) {
            return securityService.get('/billoffline/bill/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllBillsOfflineByClientIdWithSaldo = function(id) {
            console.log('yaffffffffffffffff 2')
            return securityService.get('/billoffline/bill/client/saldo/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByClientIdWithSaldo = function(id) {
            return securityService.get('/billoffline/bill/client/saldo/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineById = function(id) {
            return securityService.get('/billoffline/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByStringSeq = function(stringSeq, subsidiaryId) {
            return securityService.get('/billoffline/seq/' + stringSeq + '/' + subsidiaryId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getProductsSoldByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/billoffline/rep/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBillsOfflineByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/billoffline/rep/sales/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateBillOffline = function(billOffline) {
            return securityService.put('/billoffline', billOffline).then(function successCallback(response) {
              return response.data;
            });
        };
});
angular
    .module('app.services')
    .service('eretentionClientService', function(securityService) {
        this.createRetentionClient = function(retentionClient) {
            return securityService.post('/retenclient', retentionClient).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateRetentionClient = function(retentionClient) {
            return securityService.put('/retenclient', retentionClient).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getRetentionClientById = function(id) {
            return securityService.get('/retenclient/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getRetentionClientByBillId = function(id) {
            return securityService.get('/retenclient/bill/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getRetentionClientByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/retenclient/rep/retenclient/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getRetentionClientByBillIdAndDocumentNumber = function(id, docnumber) {
            return securityService.get('/retenclient/retcli/' + id + '/' + docnumber).then(function successCallback(response) {
                return response.data;
            });
        };
});
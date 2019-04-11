angular
    .module('app.services')
    .service('eretentionClientService', function(securityService) {
        this.create = function(retentionClient) {
            return securityService.post('/retenclient', retentionClient).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getRetentionClientByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
            return securityService.get('/retenclient/rep/retenclient/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
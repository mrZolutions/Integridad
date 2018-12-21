angular
    .module('app.services')
    .service('creditsDebtsService', function(securityService) { 
        this.getAllCreditsDebtsOfDebtsToPayByDebtsToPayId = function(id) {
            return securityService.get('/creditsdebts/creditsdebts/debts/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllCreditsDebtsPendingOfDebtsToPayByUserClientId = function(userClientId, dateTwo) {
            return securityService.get('/creditsdebts/rep/pendingreport/' + userClientId + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
});
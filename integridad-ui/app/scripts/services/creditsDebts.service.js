angular
    .module('app.services')
    .service('creditsDebtsService', function(securityService) { 
        this.getAllCreditsDebtsOfDebtsToPayByDebtsToPayId = function(id) {
            return securityService.get('/creditsdebts/creditsdebts/debts/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
});
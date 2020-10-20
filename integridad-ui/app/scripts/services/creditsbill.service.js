angular
    .module('app.services')
    .service('creditsbillService', function(securityService) { 
        this.getAllCreditsOfBillById = function(id) {
            return securityService.get('/creditsbybill/credits/bill/' + id).then(function successCallback(response) {
                console.log(response)
                return response.data;
            });
        };
        this.getAllCreditsOfBillByUserClientId = function(userClientId, dateTwo) {
            return securityService.get('/creditsbybill/rep/pendingreport/' + userClientId + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getAllCreditsOfBillByUserClientIdResume = function(userClientId, dateTwo) {
            return securityService.get('/creditsbybill/rep/pendingreport/resume/' + userClientId + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getAllPayedOfBillByUserClientId = function(userClientId) {
            return securityService.get('/creditsbybill/rep/payedreport/' + userClientId).then(function successCallback(response) {
                return response.data;
            });
        };
});
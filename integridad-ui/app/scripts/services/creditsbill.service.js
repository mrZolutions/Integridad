angular
    .module('app.services')
    .service('creditsbillService', function(securityService){
        
        this.getAllCreditsOfBillById = function(id){
            return securityService.get('/creditsbybill/credits/bill/'+ id).then(function successCallback(response){
                return response.data;
            });
        };

        this.getAllCreditsOfBillByUserClientId = function(userClientId){
            return securityService.get('/creditsbybill/rep/creditsreport/'+ userClientId).then(function successCallback(response){
                return response.data;
            });
        };
});
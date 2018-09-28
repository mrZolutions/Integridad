angular
    .module('app.services')
    .service('creditsbillService', function (securityService) {
        
        this.getAllCreditsOfBillById = function(id) {
            return securityService.get('/credits_by_bill/bill/credits/'+ id).then(function successCallback(response){
                return response.data;
            });
        };
    });
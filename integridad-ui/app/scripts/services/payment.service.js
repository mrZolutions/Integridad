angular
    .module('app.services')
    .service('paymentService', function(securityService){

        this.create = function(payment){
            return securityService.post('/payment', payment).then(function successCallback(response){
                return response.data;
            });
        };
});
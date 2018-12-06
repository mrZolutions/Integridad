angular
  .module('app.services')
  .service('debtsToPayService', function(securityService) {
    this.create = function(debtsToPay) {
        return securityService.post('/debts', debtsToPay).then(function successCallback(response) {
            return response.data;
        });
    };
});
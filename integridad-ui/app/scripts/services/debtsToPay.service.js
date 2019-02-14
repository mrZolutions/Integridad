angular
  .module('app.services')
  .service('debtsToPayService', function(securityService) {
    this.create = function(debtsToPay) {
      return securityService.post('/debts', debtsToPay).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getAllDebtsToPayByProviderId = function(id) {
      return securityService.get('/debts/debts/provider/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getById = function(id) {
      return securityService.get('/debts/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.cancelDebtsToPay = function(debtsToPay) {
      return securityService.put('/debts', debtsToPay).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getDebtsToPayByUserClientIdAndDates = function(userClientId, dateOne, dateTwo) {
      return securityService.get('/debts/rep/purchases/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
        return response.data;
      });
    };
});
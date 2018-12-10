angular
  .module('app.services')
  .service('cuentaContableService', function(securityService) {
    this.getAll = function() {
      return securityService.get('/cuenta_contable').then(function successCallback(response) {
        return response.data;
      });
    };

    this.getByType = function(typ) {
      return securityService.get('/cuenta_contable/cuenta/type/' + typ).then(function successCallback(response) {
        return response.data;
      });
    };

    this.create = function(cuenta) {
      return securityService.post('/cuenta_contable', cuenta).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazyByUserClientId = function(id) {
      return securityService.get('/cuenta_contable/lazy/client/' + id).then(function successCallback(response) {
        return response.data;
      });
    };
});

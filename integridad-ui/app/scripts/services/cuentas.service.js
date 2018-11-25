angular
  .module('app.services')
  .service('cuentasService', function (securityService) {

    this.create = function (cuenta) {
      return securityService.post('/cuenta_contable', cuenta).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazyByUserClientId = function (id) {
      return securityService.get('/cuenta_contable/lazy/client/'+id).then(function successCallback(response) {
        return response.data;
      });
    };

  });

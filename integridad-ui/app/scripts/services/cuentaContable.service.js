angular
  .module('app.services')
  .service('cuentaContableService', function (securityService) {

    this.getAll = function() {
      return securityService.get('/cuenta_contable').then(function successCallback(response) {
        return response.data;
      });
    };

  });

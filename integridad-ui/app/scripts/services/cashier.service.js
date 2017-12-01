angular
  .module('app.services')
  .service('cashierService', function (securityService) {

    this.getBySubsidiaryId = function (id) {
      return securityService.get('/cashier/subsidiary/'+id).then(function successCallback(response) {
        return response.data;
      });
    };

  });

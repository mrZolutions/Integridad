angular
  .module('app.services')
  .service('clientService', function (securityService) {

    this.create = function (client) {
      return securityService.post('/client', client).then(function successCallback(response) {
        return response.data;
      });
    };

  });

angular
  .module('app.services')
  .service('providerService', function (securityService) {

    this.create = function (provider) {
      return securityService.post('/provider', provider).then(function successCallback(response) {
        return response.data;
      });
    };

    this.update = function (provider) {
      return securityService.put('/provider', provider).then(function successCallback(response) {
        return response.data;
      });
    };
    //
    // this.delete = function (productId) {
    //   return securityService.delete('/product/' + productId).then(function successCallback(response) {
    //     return response.data;
    //   });
    // };

    this.getById = function (id) {
      return securityService.get('/provider/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazy = function () {
      return securityService.get('/provider/lazy').then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazyByUserClientId = function (id) {
      return securityService.get('/provider/lazy/client/'+id).then(function successCallback(response) {
        return response.data;
      });
    };

  });

angular
  .module('app.services')
  .service('brandService', function (securityService) {

    this.getBrandsLazy = function (projectId) {
      return securityService.get('/brand/actives_lazy/'+projectId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.create = function (brand) {
      return securityService.post('/brand', brand).then(function successCallback(response) {
        return response.data;
      });
    };

  });

angular
  .module('app.services')
  .service('productTypeService', function (securityService) {

    this.getproductTypes = function () {
      return securityService.get('/product_type/actives').then(function successCallback(response) {
        return response.data;
      });
    };

  });

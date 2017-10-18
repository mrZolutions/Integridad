angular
  .module('app.services')
  .service('productService', function (securityService) {

    this.create = function (product) {
      return securityService.post('/product', product).then(function successCallback(response) {
        return response.data;
      });
    };

    this.update = function (product) {
      return securityService.put('/product', product).then(function successCallback(response) {
        return response.data;
      });
    };

    this.delete = function (productId) {
      return securityService.delete('/product/' + productId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getById = function (id) {
      return securityService.get('/product/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazy = function () {
      return securityService.get('/product/actives').then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazyByProjectId = function (projectId) {
      return securityService.get('/product/actives/user_client/'+projectId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazyBySusidiaryId = function (subsidiaryId) {
      return securityService.get('/product/actives/subsidiary/'+subsidiaryId).then(function successCallback(response) {
        return response.data;
      });
    };
  });
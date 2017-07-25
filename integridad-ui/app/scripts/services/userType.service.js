angular
  .module('app.services')
  .service('userTypeService', function (securityService) {

    this.getUserTypes = function () {
      return securityService.get('/user_type/lazy').then(function successCallback(response) {
        return response.data;
      });
    };

  });

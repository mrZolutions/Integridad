angular
  .module('app.services')
  .service('subsidiaryService', function (securityService) {

    this.getByProjectId = function (id) {
      return securityService.get('/subsidiary/user_client/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

  });

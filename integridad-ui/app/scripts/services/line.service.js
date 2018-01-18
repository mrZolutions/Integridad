angular
  .module('app.services')
  .service('lineService', function (securityService) {

    this.getLinesLazy = function (projectId) {
      return securityService.get('/line/actives_lazy/'+projectId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.create = function (line) {
      return securityService.post('/line', line).then(function successCallback(response) {
        return response.data;
      });
    };

    this.update = function (line) {
      return securityService.put('/line', line).then(function successCallback(response) {
        return response.data;
      });
    };

  });

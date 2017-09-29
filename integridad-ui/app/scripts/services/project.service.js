angular
  .module('app.services')
  .service('projectService', function (securityService) {

    this.create = function (project) {
      return securityService.post('/project', project).then(function successCallback(response) {
        return response.data;
      });
    };

    this.update = function (project) {
      return securityService.put('/project', project).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getLazy = function () {
      return securityService.get('/project/lazy').then(function successCallback(response) {
        return response.data;
      });
    };

    this.getById = function (id) {
      return securityService.get('/project/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getNumberOfProjects = function (id) {
      return securityService.get('/project//clients_number/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

  });

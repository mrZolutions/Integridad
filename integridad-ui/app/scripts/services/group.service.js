angular
  .module('app.services')
  .service('groupService', function (securityService) {

    this.getGroupsByLineLazy = function (lineId) {
      return securityService.get('/group_line/actives_lazy/'+lineId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.create = function (group) {
      return securityService.post('/group_line', group).then(function successCallback(response) {
        return response.data;
      });
    };

  });

angular
  .module('app.services')
  .service('subgroupService', function (securityService) {

    this.getSubGroupsByGroupLazy = function (groupId) {
      return securityService.get('/sub_group/actives_lazy/'+groupId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.create = function (sugroup) {
      return securityService.post('/sub_group', sugroup).then(function successCallback(response) {
        return response.data;
      });
    };

    this.update = function (sugroup) {
      return securityService.put('/sub_group', sugroup).then(function successCallback(response) {
        return response.data;
      });
    };

  });

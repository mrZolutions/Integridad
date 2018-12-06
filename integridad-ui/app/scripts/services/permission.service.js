angular
  .module('app.services')
  .service('permissionService', function(securityService) {
    this.getPermissions = function(userType) {
      return securityService.post('/user_type_permission/user_type', userType).then(function successCallback(response) {
        return response.data;
      });
    };
});

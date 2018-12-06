angular
  .module('app.services')
  .factory('authorizationService', function($localStorage) {
    return {
      isLoggedIn : function() {
          return($localStorage.user)? $localStorage.user : false;
      }
    };
});

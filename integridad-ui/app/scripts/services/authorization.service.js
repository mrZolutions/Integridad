angular
    .module('app.services')
    .factory('authorizationService', function(holderService) {
        return {
            isLoggedIn : function() {
                var user = holderService.get();
                return(user)? user : false;
            }
        };
});

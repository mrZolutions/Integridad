var app = angular.module('integridadUiApp', ['app.routes', 'app.core', 'app.directives']);
app.run(['$rootScope', '$location', 'authorizationService', '$localStorage',
 function ($rootScope, $location, authorizationService, $localStorage) {
   $rootScope.$on('$routeChangeStart', function (event) {
     if($location.path() !== '/' && !$location.path().includes("/active/")){
       if (!authorizationService.isLoggedIn()) {
            $location.path('/');
        }
     }
   });
}]);

var app = angular.module('integridadUiApp', ['app.routes', 'app.core', 'app.directives']);
app.run(['$rootScope', '$location', 'authorizationService', function($rootScope, $location, authorizationService) {
    $rootScope.$on('$routeChangeStart', function (event) {
        if (!$location.path().includes("/activate/") && !$location.path().includes("/invoice/")) {
            if (!authorizationService.isLoggedIn()) {
                $location.path('/');
            };
        };
    });
}]);
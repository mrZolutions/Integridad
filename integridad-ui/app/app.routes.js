angular
  .module('app.routes', ['ngRoute'])
  .config(routes);

function routes($routeProvider) {
  $routeProvider
    .when('/', {
      templateUrl: 'views/main.html',
      controller: 'MainCtrl',
      controllerAs: 'vm'
    }).when('/activate/:idUSer/:validate', {
      templateUrl: 'views/activate/activate.tpl.html',
      controller: 'ActivateCtrl',
      controllerAs: 'vm'
    }).when('/home', {
      templateUrl: 'views/home/home.tpl.html',
      controller: 'HomeCtrl',
      controllerAs: 'vm'
    }).otherwise({
    redirectTo: '/'
  });
}

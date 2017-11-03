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
    }).when('/users', {
      templateUrl: 'views/users/users.tpl.html',
      controller: 'UsersCtrl',
      controllerAs: 'vm'
    }).when('/users/edit', {
      templateUrl: 'views/users/edit.tpl.html',
      controller: 'UserEditCtrl',
      controllerAs: 'vm'
    }).when('/clients', {
      templateUrl: 'views/clients/clients.tpl.html',
      controller: 'ClientsCtrl',
      controllerAs: 'vm'
    }).when('/clients/:create', {
      templateUrl: 'views/clients/clients.tpl.html',
      controller: 'ClientsCtrl',
      controllerAs: 'vm'
    }).when('/bills/bill', {
      templateUrl: 'views/bills/bill.tpl.html',
      controller: 'BillCtrl',
      controllerAs: 'vm'
    }).when('/projects/projects', {
      templateUrl: 'views/projects/projects.tpl.html',
      controller: 'ProjectsCtrl',
      controllerAs: 'vm'
    }).when('/products/products', {
      templateUrl: 'views/products/products.tpl.html',
      controller: 'ProductsCtrl',
      controllerAs: 'vm'
    }).when('/products/products/:subsidiaryId', {
      templateUrl: 'views/products/products.tpl.html',
      controller: 'ProductsCtrl',
      controllerAs: 'vm'
    }).otherwise({
    redirectTo: '/'
  });
}

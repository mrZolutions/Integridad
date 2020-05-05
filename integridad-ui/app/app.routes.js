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
        }).when('/provider/providers', {
            templateUrl: 'views/providers/providers.tpl.html',
            controller: 'ProvidersCtrl',
            controllerAs: 'vm'
        }).when('/report/sales', {
            templateUrl: 'views/reports/sales.tpl.html',
            controller: 'ReportSalesCtrl',
            controllerAs: 'vm'
        }).when('/note/credit', {
            templateUrl: 'views/note/creditNote.tpl.html',
            controller: 'CreditNoteCtrl',
            controllerAs: 'vm'
        }).when('/quotation/quotation', {
            templateUrl: 'views/quotation/quotation.tpl.html',
            controller: 'QuotationCtrl',
            controllerAs: 'vm'
        }).when('/cuentas', {
            templateUrl: 'views/cuentas/cuentas.tpl.html',
            controller: 'CuentasContablesCtrl',
            controllerAs: 'vm'
        }).when('/cobrar', {
            templateUrl: 'views/cobrar/cobrar.tpl.html',
            controller: 'CuentasCobrarCtrl',
            controllerAs: 'vm'
        }).when('/produc', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            controllerAs: 'vm'
        }).when('/entry', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            // templateUrl: 'views/inventories/entry.tpl.html',
            // controller: 'EntryCtrl',
            controllerAs: 'vm'
        }).when('/contab', {
            templateUrl: 'views/contable/contable.tpl.html',
            controller: 'ContableCtrl',
            controllerAs: 'vm'
        }).when('/pagar', {
            templateUrl: 'views/debtstopay/debtstopay.tpl.html',
            controller: 'DebtsToPayCtrl',
            controllerAs: 'vm'
        }).when('/billsOffline', {
            templateUrl: 'views/billsOffline/billOffline.tpl.html',
            controller: 'BillOfflineCtrl',
            controllerAs: 'vm'
        }).when('/swimming', {
            templateUrl: 'views/swimming/swimming.tpl.html',
            controller: 'SwimmingCtrl',
            controllerAs: 'vm'
        }).when('/config', {
            templateUrl: 'views/config/config.tpl.html',
            controller: 'ConfigCtrl',
            controllerAs: 'vm'
        }).otherwise({
            redirectTo: '/'
    });
};
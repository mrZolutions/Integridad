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
        }).when('/cotizacion', {
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
        }).when('/bills/nat', {
            templateUrl: 'views/bills/nat.tpl.html',
            controller: 'NatCtrl',
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
        }).when('/provider/retention', {
            templateUrl: 'views/providers/providers.tpl.html',
            controller: 'ProvidersCtrl',
            controllerAs: 'vm'
        }).when('/provider/reports', {
            templateUrl: 'views/providers/providers.tpl.html',
            controller: 'ProvidersCtrl',
            controllerAs: 'vm'
        }).when('/provider/nat', {
            templateUrl: 'views/providers/nat.tpl.html',
            controller: 'NatRetentionCtrl',
            controllerAs: 'vm'
        }).when('/purcahses/bill', {
            templateUrl: 'views/debtstopay/debtstopay.tpl.html',
            controller: 'DebtsToPayCtrl',
            controllerAs: 'vm'
        }).when('/report/sales', {
            templateUrl: 'views/reports/sales.tpl.html',
            controller: 'ReportSalesCtrl',
            controllerAs: 'vm'
        }).when('/note/credit', {
            templateUrl: 'views/note/creditNote.tpl.html',
            controller: 'CreditNoteCtrl',
            controllerAs: 'vm'
        }).when('/creditNoteSale', {
            templateUrl: 'views/note/creditNote.tpl.html',
            controller: 'CreditNoteCtrl',
            controllerAs: 'vm'
        }).when('/creditNoteManual', {
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
        }).when('/retentionClient', {
            templateUrl: 'views/cobrar/cobrar.tpl.html',
            controller: 'CuentasCobrarCtrl',
            controllerAs: 'vm'
        }).when('/produc', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            controllerAs: 'vm'
        }).when('/inventories/report', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            controllerAs: 'vm'
        }).when('/entry', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            controllerAs: 'vm'
        }).when('/adjustment', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            controllerAs: 'vm'
        }).when('/egress', {
            templateUrl: 'views/production/production.tpl.html',
            controller: 'ProductionCtrl',
            controllerAs: 'vm'
        }).when('/contab', {
            templateUrl: 'views/contable/contable.tpl.html',
            controller: 'ContableCtrl',
            controllerAs: 'vm'
        }).when('/general', {
            templateUrl: 'views/contable/contable.tpl.html',
            controller: 'ContableCtrl',
            controllerAs: 'vm'
        }).when('/salesBill', {
            templateUrl: 'views/contable/contable.tpl.html',
            controller: 'ContableCtrl',
            controllerAs: 'vm'
        }).when('/dailyIncome', {
            templateUrl: 'views/contable/contable.tpl.html',
            controller: 'ContableCtrl',
            controllerAs: 'vm'
        }).when('/dailyOutcome', {
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
        }).when('/opencash', {
            templateUrl: 'views/cashierProcess/open.tpl.html',
            controller: 'OpenCashierCtrl',
            controllerAs: 'vm'
        }).when('/invoice/:id/:acceso', {
            templateUrl: 'views/openinvoice/invoice.tpl.html',
            controller: 'InvoiceCtrl',
            controllerAs: 'vm'
        }).when('/retention/:id/:acceso', {
            templateUrl: 'views/openretention/retention.tpl.html',
            controller: 'RetentionCtrl',
            controllerAs: 'vm'
        }).when('/guias', {
            templateUrl: 'views/guides/guides.tpl.html',
            controller: 'GuidesCtrl',
            controllerAs: 'vm'
        }).otherwise({
            redirectTo: '/'
    });
};
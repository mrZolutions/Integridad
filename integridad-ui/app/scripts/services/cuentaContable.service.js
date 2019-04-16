angular
    .module('app.services')
    .service('cuentaContableService', function(securityService) {
        this.getAll = function() {
            return securityService.get('/cuenta_contable').then(function successCallback(response) {
                return response.data;
            });
        };
    
        this.getCuentaContableByType = function(id, typ) {
            return securityService.get('/cuenta_contable/type/' + id + '/' + typ).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getAllTypeAndAccountType = function(typ, atyp) {
            return securityService.get('/cuenta_contable/cuenta/typeacc/' + typ + '/' + atyp).then(function successCallback(response) {
                return response.data;
            });
        };

        this.create = function(cuenta) {
            return securityService.post('/cuenta_contable', cuenta).then(function successCallback(response) {
                return response.data;
            });
        };

        this.update = function(cuenta) {
            return securityService.put('/cuenta_contable', cuenta).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getCuentaContableByUserClient = function(id) {
            return securityService.get('/cuenta_contable/userclient/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
});

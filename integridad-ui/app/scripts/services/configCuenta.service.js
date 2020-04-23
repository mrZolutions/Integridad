angular
    .module('app.services')
    .service('configCuentasService', function(securityService) {

        this.save = function(configCuentas, userClientId) {
            return securityService.post('/configCuentas/' + userClientId, configCuentas).then(function successCallback(response) {
                return response.data;
            });
        };

        this.create = function(configCuenta) {
            return securityService.post('/configCuentas', configCuenta).then(function successCallback(response) {
                return response.data;
            });
        };

        this.update = function(configCuenta) {
            return securityService.put('/configCuentas', configCuenta).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getConfigCuentaByUserClient = function(id) {
            return securityService.get('/configCuentas/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getConfigCuentaByUserClientAndOptionCode = function(userClientId, code) {
            return securityService.get('/configCuentas//userClient/'+ userClientId +'/code/' + code).then(function successCallback(response) {
                return response.data;
            });
        };

});
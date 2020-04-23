angular
    .module('app.services')
    .service('optionConfigCuentasService', function(securityService) {

        this.getOptionConfigCuentas = function() {
            return securityService.get('/optionConfigCuentas').then(function successCallback(response) {
                return response.data;
            });
        };

});
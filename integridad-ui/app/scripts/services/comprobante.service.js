angular
    .module('app.services')
    .service('comprobanteService', function(securityService) {
        //Comprobante de Cobro
        this.createComprobanteCobro = function(comprobanteCobro) {
            return securityService.post('/compcobro', comprobanteCobro).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getComprobanteCobroById = function(id) {
            return securityService.get('/compcobro/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getComprobanteCobroByUserClientId = function(userclientId) {
            return securityService.get('/compcobro/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getComprobanteCobroByClientId = function(id) {
            return securityService.get('/compcobro/client/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateComprobanteCobro = function(comprobanteCobro) {
            return securityService.put('/compcobro', comprobanteCobro).then(function successCallback(response) {
                return response.data;
            });
        };

        //Comprobante de Pago
        this.createComprobantePago = function(comprobantePago) {
            return securityService.post('/comppago', comprobantePago).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getComprobantePagoById = function(id) {
            return securityService.get('/comppago/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getComprobantePagoByUserClientId = function(userclientId) {
            return securityService.get('/comppago/userclient/' + userclientId).then(function successCallback(response) {
                return response.data;
            });
        };
        this.getComprobantePagoByProviderId = function(id) {
            return securityService.get('/comppago/provider/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.deactivateComprobantePago = function(comprobantePago) {
            return securityService.put('/comppago', comprobantePago).then(function successCallback(response) {
                return response.data;
            });
        };
});
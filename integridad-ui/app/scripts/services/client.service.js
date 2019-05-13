angular
    .module('app.services')
    .service('clientService', function(securityService) {
        this.create = function(client) {
            return securityService.post('/client', client).then(function successCallback(response) {
                return response.data;
            });
        };

        this.update = function(client) {
            return securityService.put('/client', client).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazy = function() {
            return securityService.get('/client/lazy').then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazyByProjectId = function(id) {
            return securityService.get('/client/lazy/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getClientByUserClientAndIdentification = function(userClientId, identification) {
            return securityService.get('/client/userclient/' + userClientId + '/' + identification).then(function successCallback(response) {
                return response.data;
            });
        };
});
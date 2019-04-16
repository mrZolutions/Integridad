angular
    .module('app.services')
    .service('warehouseService', function(securityService) {
        this.getAllWarehouseById = function(id) {
            return securityService.get('/warehouse/' + id).then(function successCallback(response) {
              return response.data;
            });
        };

        this.getAllWarehouseByUserClientId = function(id) {
            return securityService.get('/warehouse/userclient/' + id).then(function successCallback(response) {
                return response.data;
            });
        };
});
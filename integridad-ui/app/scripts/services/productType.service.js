angular
    .module('app.services')
    .service('productTypeService', function(securityService) {
        this.getproductTypesLazy = function() {
            return securityService.get('/product_type/actives_lazy').then(function successCallback(response) {
                return response.data;
            });
        };
});
angular
    .module('app.services')
    .service('productService', function(securityService) {
        this.create = function(product) {
            return securityService.post('/product', product).then(function successCallback(response) {
                return response.data;
            });
        };

        this.update = function(product) {
            return securityService.put('/product', product).then(function successCallback(response) {
                return response.data;
            });
        };

        this.delete = function(productId) {
            return securityService.delete('/product/' + productId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getById = function(id) {
            return securityService.get('/product/' + id).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazy = function() {
            return securityService.get('/product/actives').then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazyByUserClientId = function(projectId) {
            return securityService.get('/product/actives/user_client/' + projectId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazyBySusidiaryId = function(subsidiaryId, page, variable) {
            return securityService.get('/product/actives/subsidiary/' + subsidiaryId + '/' + page + '?var=' + variable).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getProductsForExistencyReport = function(userClientId) {
            return securityService.get('/product/rep/existency/' + userClientId).then(function successCallback(response) {
                return response.data;
            });
        };
        
        this.getLazyBySusidiaryIdForBill = function(subsidiaryId, page, variable) {
            return securityService.get('/product/actives/subsidiary/bill/' + subsidiaryId + '/' + page + '?var=' + variable).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getProductsBySusidiaryId = function(userClientId) {
            return securityService.get('/product/allproducts/' + userClientId).then(function successCallback(response) {
                return response.data;
            });
        };
});
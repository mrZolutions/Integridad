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

        this.updateEdited = function(product) {
            return securityService.put('/product/edted', product).then(function successCallback(response) {
                return response.data;
            });
        };

        this.remove = function(productWrapper) {
            return securityService.put('/product/remove', productWrapper).then(function successCallback(response) {
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

        this.getLazyBySusidiaryId = function(subsidiaryId, page, variable, lineId) {
            return securityService.get('/product/actives/subsidiary/' + subsidiaryId + '/' + page + '/' + lineId + '?var=' + variable).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazyBySusidiaryIdBarCode = function(subsidiaryId, variable) {
            return securityService.get('/product/actives/subsidiary/bar/' + subsidiaryId + '?var=' + variable).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getProductsForExistencyReport = function(userClientId) {
            return securityService.get('/product/rep/existency/' + userClientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getProductsForExistencyReportV2 = function(userClientId) {
            return securityService.get('/product/rep/existency/V2/' + userClientId).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getProductsForExistencyCatReport = function(userClientId) {
            return securityService.get('/product/rep/existency/cat/' + userClientId).then(function successCallback(response) {
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

        this.getProdByUserClientIdAndCodeIntegActive = function(userClientId, code) {
            return securityService.get('/product/prod/' + userClientId + '/' + code).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLastCodeByUserClientIdActive = function(userClientId) {
            return securityService.get('/product/active/code/user_client/' + userClientId ).then(function successCallback(response) {
                return response.data;
            });
        };

        //Reporte de Kardex por Producto
        this.getKardexActivesByUserClientIdAndProductIdAndDates = function(id, prodID, dateOne, dateTwo) {
            return securityService.get('/product/kardex/rep/' + id + '/' + prodID + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
                return response.data;
            });
        };

        //Ajuste Kardex por Producto
        this.createKardex = function(kardex) {
            return securityService.post('/product/kardex/setprodkar', kardex).then(function successCallback(response) {
                return response.data;
            });
        };

        this.createList = function(productos) {
            return securityService.post('/product/list', productos).then(function successCallback(response) {
                return response.data;
            });
        };
});

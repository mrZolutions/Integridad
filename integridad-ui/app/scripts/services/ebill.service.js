angular
    .module('app.services')
    .service('ebillService', function($http) {
        var baseUrl = 'https://link.datil.co/invoices/issue';
        // var baseUrl = 'http://localhost:8080//integridad/v1';
        var config = {};
        config.headers = {
            'Content-Type':'application/json',
            'X-Key': '734de6ccec5c4e688a84f7ab06620baf',
            'X-Password': '12345'
        };

        // this.get = function (path) {
        //     return $http.get(baseUrl + path);
        // };
        //
        // this.delete = function (path) {
        //     return $http.delete(baseUrl + path);
        // };

        this.post = function(path, data) {
            var url = baseUrl + path;
            return $http.post(url, data, config);
        };

        // this.put = function (path, data) {
        //     var url = baseUrl + path;
        //     return $http.put(url, data, config);
        // };
});
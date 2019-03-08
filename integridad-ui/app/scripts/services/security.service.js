angular
  .module('app.services')
  .service('securityService', function ($http, $base64) {

    var baseUrl = 'https://integridad.herokuapp.com/integridad/v1';
    //var baseUrl = 'http://localhost:8080//integridad/v1';
    var config = {};
    var authdata = $base64.encode("integridaduser:e3tkwy");
    config.headers = {
      'Content-Type':'application/json',
      'Authorization': 'Basic ' + authdata
    };

    this.get = function (path) {
      var url = baseUrl + path;
      return $http.get(url, config);
    };

    this.delete = function (path) {
      var url = baseUrl + path;
      return $http.delete(url, config);
    };

    this.post = function (path, data) {
      var url = baseUrl + path;
      return $http.post(url, data, config);
    };

    this.put = function (path, data) {
      var url = baseUrl + path;
      return $http.put(url, data, config);
    };

});

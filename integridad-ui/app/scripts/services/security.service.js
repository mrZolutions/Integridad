angular
  .module('app.services')
  .service('securityService', function ($http, $base64) {

    // var baseUrl = 'https://trailers-nrg-api.herokuapp.com/trailers/v2';
    var baseUrl = 'http://localhost:8080//integridad/v1';
    var config = {};
    var authdata = $base64.encode("dan:12345");
    config.headers = {
      'Content-Type':'application/json',
      'Authorization': 'Basic ' + authdata
    };

    this.get = function (path) {
      return $http.get(baseUrl + path);
    };

    this.delete = function (path) {
      return $http.delete(baseUrl + path);
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

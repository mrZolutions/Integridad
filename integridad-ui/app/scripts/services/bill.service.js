angular
  .module('app.services')
  .service('billService', function (securityService) {

    this.getIsoDate = function(dateBillPre){
      var dateReturn = new Date(dateBillPre.getTime() - (5*60*60*1000));
      return dateReturn.toISOString().toString();
    };


    this.getClaveDeAcceso = function (req) {
      return securityService.post('/bill/clave_acceso/', req).then(function successCallback(response) {
        console.log(response)
        return response;
      });
    };

    this.create = function (bill) {
      return securityService.post('/bill', bill).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getByClientId = function (id) {
      return securityService.get('/bill/client/'+ id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getById = function (id) {
      return securityService.get('/bill/'+ id).then(function successCallback(response) {
        return response.data;
      });
    };

  });

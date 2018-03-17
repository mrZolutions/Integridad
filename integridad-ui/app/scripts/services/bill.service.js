angular
  .module('app.services')
  .service('billService', function (securityService) {

    this.getClaveDeAcceso = function (req, id) {
      return securityService.post('/bill/clave_acceso/'+id, req).then(function successCallback(response) {
        // console.log(response)
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

    this.getByStringSeq = function (stringSeq, subsidiaryId) {
      return securityService.get('/bill/seq/'+ stringSeq + '/'+ subsidiaryId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getByUserClientAndDates = function (userClientId, dateOne, dateTwo) {
      return securityService.get('/bill/rep/'+ userClientId + '/' + dateOne + '/'+ dateTwo).then(function successCallback(response) {
        return response.data;
      });
    };

  });

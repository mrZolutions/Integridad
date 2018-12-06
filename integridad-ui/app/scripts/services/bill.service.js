angular
  .module('app.services')
  .service('billService', function(securityService) {

    this.getClaveDeAcceso = function(req, id) {
      return securityService.post('/bill/clave_acceso/' + id, req).then(function successCallback(response) {
        // console.log(response)
        return response;
      });
    };

    this.create = function(bill, type) {
      return securityService.post('/bill/' + type, bill).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getBillsByClientId = function(id) {
      return securityService.get('/bill/bill/client/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getCreditsBillsByClientId = function(id) {
      return securityService.get('/bill/bill/credits/client/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.cancelBill = function(bill) {
      return securityService.put('/bill', bill).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getById = function(id) {
      return securityService.get('/bill/' + id).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getByStringSeq = function(stringSeq, subsidiaryId) {
      return securityService.get('/bill/seq/' + stringSeq + '/' + subsidiaryId).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getActivesByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
      return securityService.get('/bill/rep/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getAllByUserClientAndDates = function(userClientId, dateOne, dateTwo) {
      return securityService.get('/bill/rep/sales/' + userClientId + '/' + dateOne + '/' + dateTwo).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getQuotationsByClientId = function(id) {
      return securityService.get('/bill/quotation/client/' + id).then(function successCallback(response) {
        return response.data;
      });
    };
});

angular
  .module('app.services')
  .service('eretentionClientService', function(securityService) {
    this.create = function(retentionClient) {
        return securityService.post('/retenclient', retentionClient).then(function successCallback(response) {
          return response.data;
        });
    };
});
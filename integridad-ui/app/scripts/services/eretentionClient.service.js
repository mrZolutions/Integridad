angular
  .module('app.services')
  .service('eretentionClientService', function (_, dateService, securityService) {

    this.create = function (retentionClient) {
        return securityService.post('/retenclient', retentionClient).then(function successCallback(response) {
          return response.data;
        });
    };
});
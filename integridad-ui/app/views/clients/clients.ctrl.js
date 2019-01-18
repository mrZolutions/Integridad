'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ClientsCtrl
 * @description
 * # ClientsCtrl
 * Controller of the integridadUiApp
 */
angular.module('integridadUiApp')
  .controller('ClientsCtrl', function ($routeParams, $location, projectService, utilStringService, countryListService, clientService, $localStorage, validatorService) {
    
    var vm = this;
    vm.error = undefined;
    vm.success = undefined;
    vm.loading = false;
    vm.client=undefined;
    vm.countryList = countryListService.getCountryList();
    vm.citiesList = countryListService.getCitiesEcuador();
    vm.clientList = undefined;

    function _activate() {
      if ($routeParams.create) {
        vm.clientCreate();
      } else {
        vm.loading = true;
        clientService.getLazyByProjectId($localStorage.user.subsidiary.userClient.id).then(function(response) {
          vm.clientList = response;
          vm.loading = false;
        }).catch(function(error) {
          vm.loading = false;
          vm.error = error.data;
        });
      };
    };

    function create() {
      clientService.create(vm.client).then(function(response) {
        vm.client=undefined;
        _activate();
        vm.error = undefined;
        vm.success = 'Registro realizado con exito';
        if ($routeParams.create) {
          $location.path('/bills/bill');
        };
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    function update(isRemove) {
      clientService.update(vm.client).then(function(response) {
        vm.client=undefined;
        _activate();
        vm.error = undefined;
        if (isRemove) {
          vm.success = 'Registro eliminado con exito';
        } else {
          vm.success = 'Registro actualizado con exito';
        };
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.clientCotizar= function(client) {
      localStorage.setItem("client", JSON.stringify(client));
      window.location = "#!/quotation/quotation";
    };

    vm.clientCreate = function() {
      projectService.getNumberOfProjects($localStorage.user.subsidiary.userClient.id).then(function(response) {
        vm.success = undefined;
        vm.error = undefined
        var number = parseInt(response);
        vm.client = {
          country: 'Ecuador',
          city: 'Quito',
          codApp: number + 1,
          userClient: $localStorage.user.subsidiary.userClient
        };
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };

    vm.clientEdit = function(client) {
      vm.success = undefined;
      vm.error = undefined
      vm.client = angular.copy(client);
    };

    vm.validateEcuador = function() {
      if (vm.client.country !== 'Ecuador') {
        vm.client.city = undefined;
      };
    };

    vm.save = function() {
      var validationError = utilStringService.isAnyInArrayStringEmpty([
        vm.client.name, vm.client.identification, vm.client.codApp
      ]);

      var idValid = true;
      if (vm.client.typeId === 'CED') {
        idValid = validatorService.isCedulaValid(vm.client.identification);
      } else if (vm.client.typeId === 'RUC') {
        idValid = validatorService.isRucValid(vm.client.identification);
      } else if (vm.client.typeId === 'PAS') {
        idValid = true;
      };

      if (validationError) {
        vm.error = 'Debe ingresar Nombres completos, una identificacion y el Codigo de Contabilidad';
      } else if (!idValid) {
        vm.error = 'Identificacion invalida';
      } else {
        vm.loading = true;
        if (vm.client.id === undefined) {
          create();
        } else {
          update(false);
        };
      };
    };

    vm.remove = function() {
      vm.client.active = false;
      update(true);
    };

    vm.cancel = function() {
      vm.client = undefined;
      vm.success = undefined;
      vm.error = undefined;
      if ($routeParams.create) {
        $location.path('/bills/bill');
      };
    };

    vm.exit = function() {
      $location.path('/home');
    };

    (function initController() {
      _activate();
    })();
});

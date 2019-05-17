'use strict';

/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
  .controller('ProductsCtrl', function(_, $localStorage, $location, productService, utilStringService, projectService,
                                        subsidiaryService, productTypeService, messurementListService, brandService, lineService, groupService,
                                        subgroupService, $routeParams) {
    
  var vm = this;
  vm.error = undefined;
  vm.success = undefined;
                                      
  vm.loading = false;
  vm.product = undefined;
  vm.productList = [];
  vm.subsidiaryId = undefined;
  vm.productTypes = undefined;
  vm.messurements = undefined;
  vm.subsidiaries = undefined;
  vm.brands = undefined;
  vm.lineas = undefined;
  vm.groups = undefined;
  vm.subGroups = undefined;
  vm.averageC = undefined;
  vm.cashP = undefined;
  vm.productBySubsidiaries = [];
  vm.wizard = 0;
  
  function _activate() {
    vm.searchText = undefined;
    vm.loading = true;
    vm.messurements = messurementListService.getMessurementList();
    productTypeService.getproductTypesLazy().then(function(response) {
      vm.productTypes = response;
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
    vm.page = 0;
    _filter();
  };
                                      
  function _filter() {
    vm.loading = true;
    vm.totalPages = 0;
    vm.productList = [];
    var variable = vm.searchText? vm.searchText : null;
    if ($routeParams.subsidiaryId) {
      productService.getLazyBySusidiaryId($routeParams.subsidiaryId, vm.page, variable).then(function(response) {
        vm.totalPages = response.totalPages;
        vm.totalElements = response.totalElements;
        _getProductQuantities(response.content);
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    } else {
      productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, variable).then(function(response) {
        vm.totalElements = response.totalElements;
        vm.totalPages = response.totalPages;
        _getProductQuantities(response.content);
        vm.loading = false;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };
  };
                                      
  function _getProductQuantities(listResponse) {
    for (var i = 0; i < listResponse.length; i++) {
      var sub = _.find(listResponse[i].productBySubsidiaries, function(s) {
        return (s.subsidiary.id === $localStorage.user.subsidiary.id && s.active === true);
      });
      if (sub) {
        listResponse[i].quantity = sub.quantity
        vm.productList.push(listResponse[i]);
      };
    };
  };
                                      
  function _getSubsidiaries(edit) {
    subsidiaryService.getByProjectId($localStorage.user.subsidiary.userClient.id).then(function(response) {
      vm.subsidiaries = response;
      if (edit) {
        _.each(vm.subsidiaries, function(sub) {
          _.each(vm.product.productBySubsidiaries, function(ps) {
            if (sub.id === ps.subsidiary.id && ps.active) {
              sub.cantidad = ps.quantity;
              sub.selected = true;
            };
          });
        });
      };
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  function _getBrands() {
    brandService.getBrandsLazy($localStorage.user.subsidiary.userClient.id).then(function(response) {
      vm.brands = response;
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  function _getLines() {
    lineService.getLinesLazy($localStorage.user.subsidiary.userClient.id).then(function(response) {
      vm.lineas = response;
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  function create() {
    vm.product.productBySubsidiaries = vm.productBySubsidiaries;
    productService.create(vm.product).then(function(response) {
      vm.product = undefined;
      vm.selectedGroup = undefined;
      vm.selectedLine = undefined;
      vm.wizard = 0;
      _activate();
      vm.error = undefined;
      vm.success = 'Registro realizado con exito';
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  function update(isRemove) {
    _.each(vm.product.productBySubsidiaries, function(ps){ps.active=false;});
    _.each(vm.productBySubsidiaries, function(psNew){vm.product.productBySubsidiaries.push(psNew)});
    productService.update(vm.product).then(function(response) {
      vm.product = undefined;
      vm.selectedGroup = undefined;
      vm.selectedLine = undefined;
      vm.wizard = 0;
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

  vm.filter = function() {
    vm.page = 0;
    _filter();
  };
  
  vm.paginate = function(page) {
    vm.page = page;
    _filter();
  };
  
  vm.getActiveClass = function(index) {
    var classActive = vm.page === index? 'active' : '';
    return classActive;
  };
                                      
  vm.range = function() {
    return new Array(vm.totalPages);
  };
                                     
  vm.costPreview = function() {
    var avrCost = 0.0;
    var gEfectivo = 0.0;
    var avrCost = vm.product.averageCost;
    var gEfectivo = 1 + ((vm.product.cashPercentage) / 100.00);
    var preview = avrCost * gEfectivo;
    return (preview).toFixed(4);
  };
                                      
  vm.costIvaPreview = function() {
    var avrCost = 0.0;
    var gEfectivo = 0.0;
    var iva = 1.12;
    var avrCost = vm.product.averageCost;
    var gEfectivo = 1 + ((vm.product.cashPercentage) / 100.00);
    var preview = avrCost * gEfectivo * iva;
    return (preview).toFixed(4);
  };
                                      
  vm.getIva = function(cashPercen, averageCost) {
    const IVA = 1.12;
    var aC = 1 + ((cashPercen) / 100);
    var cost = aC * averageCost * IVA;
    return (cost).toFixed(4);
  };
                                      
  vm.getCost = function(cashPercen, averageCost) {
    var aC = 1 + ((cashPercen) / 100);
    var cost = aC * averageCost;
    return (cost).toFixed(4);
  };
                                      
  vm.editProduct = function(productEdit) {
    vm.success = undefined;
    vm.error = undefined;
    vm.selectedGroup = productEdit.subgroup.groupLine;
    vm.selectedLine = productEdit.subgroup.groupLine.line;
    vm.averageC = productEdit.averageCost;
    vm.cashP = productEdit.cashPercentage;
    vm.messurements = messurementListService.getMessurementList();
    _.each(vm.messurements, function(mes) {
      if (productEdit.unitOfMeasurementAbbr === mes.shortName) {
        vm.messurementSelected = mes;
      };
    });
    vm.getGroups();
    vm.getSubGroups();
    _getSubsidiaries(true);
    vm.wizard = 1;
    vm.product = productEdit;
  };
                                      
  vm.productCreate = function() {
    _getSubsidiaries(false);
    vm.success = undefined;
    vm.error = undefined
    vm.productBySubsidiaries = [];
    vm.wizard = 1;
    vm.product = {
      userClient: $localStorage.user.subsidiary.userClient,
      productBySubsidiaries: []
    };
  };
                                      
  vm.changeSub = function(subsidiary) {
    if (vm.product.productType.code !== 'SER') {
      if (subsidiary.selected) {
        subsidiary.cantidad = 0;
      } else {
        subsidiary.cantidad = undefined;
      };
    };
  };
                                      
  vm.getGroups = function() {
    if (vm.selectedLine !== null && vm.selectedLine !== undefined) {
      groupService.getGroupsByLineLazy(vm.selectedLine.id).then(function(response) {
        vm.groups = response;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };
  };
                                      
  vm.getSubGroups = function() {
    if (vm.selectedGroup !== null && vm.selectedGroup !== undefined) {
      subgroupService.getSubGroupsByGroupLazy(vm.selectedGroup.id).then(function(response) {
        vm.subGroups = response;
      }).catch(function(error) {
        vm.loading = false;
        vm.error = error.data;
      });
    };
  };
                                      
  vm.createBrand = function() {
    vm.newBrand = {
      userClient: $localStorage.user.subsidiary.userClient,
      code: vm.brands.length + 1,
      active: true
    };
  };
                                      
  vm.createLine = function() {
    vm.newLine = {
      userClient: $localStorage.user.subsidiary.userClient,
      code: vm.lineas.length + 1,
      active: true,
      groupLines:[]
    };
  };
                                      
  vm.createGroup = function() {
    vm.newGroup = {
      line: vm.selectedLine,
      code: vm.groups.length + 1,
      active: true,
      products: []
    };
  };
                                      
  vm.createSubGroup = function() {
    vm.newSubGroup = {
      groupLine: vm.selectedGroup,
      code: vm.subGroups.length + 1,
      active: true,
      subGroups: []
    };
  };
                                      
  vm.saveNewBrand = function() {
    brandService.create(vm.newBrand).then(function(response) {
      vm.brands.push(response);
      vm.product.brand = response;
      vm.newBrand = undefined;
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.saveNewLine = function() {
    lineService.create(vm.newLine).then(function(response) {
      vm.lineas.push(response);
      vm.selectedLine = response;
      vm.newLine = undefined;
      vm.groups = [];
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.saveNewGroup = function() {
    groupService.create(vm.newGroup).then(function(response) {
      vm.groups.push(response);
      vm.selectedGroup = response;
      vm.newGroup = undefined;
      vm.subGroups = [];
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.saveNewSubGroup = function() {
    subgroupService.create(vm.newSubGroup).then(function(response) {
      vm.subGroups.push(response);
      vm.product.subgroup = response;
      vm.newSubGroup = undefined;
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.wiz2 = function() {
    vm.productBySubsidiaries = [];
    if (vm.product.productType.code !== 'SER') {
      vm.product.unitOfMeasurementAbbr = vm.messurementSelected.shortName;
      vm.product.unitOfMeasurementFull = vm.messurementSelected.name;
    };
    _.each(vm.subsidiaries, function(sub) {
      if (sub.selected) {
        var productBySubsidiary = {
          dateCreated: new Date().getTime(),
          quantity: sub.cantidad,
          subsidiary: sub,
          active: true
        };
        vm.productBySubsidiaries.push(productBySubsidiary);
      };
    });
    _getBrands();
    _getLines();
    vm.wizard = 2;
  };
                                      
  vm.wiz3 = function() {
    vm.wizard = 3;
  };
                                      
  vm.deleteBrand = function(brand) {
    vm.loading = true;
    brand.active = false;
    brandService.update(brand).then(function(response) {
      vm.loading = false;
      _getBrands();
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.deleteLine = function(line) {
    vm.loading = true;
    line.active = false;
    lineService.update(line).then(function(response) {
      vm.loading = false;
      _getLines();
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.deleteGroup = function(group) {
    vm.loading = true;
    group.active = false;
    groupService.update(group).then(function(response) {
      vm.loading = false;
      vm.getGroups();
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.deleteSubGroup = function(subGroup) {
    vm.loading = true;
    subGroup.active = false;
    subgroupService.update(subGroup).then(function(response) {
      vm.getSubGroups();
      vm.loading = false;
    }).catch(function(error) {
      vm.loading = false;
      vm.error = error.data;
    });
  };
                                      
  vm.save = function() {
    var validationError = utilStringService.isAnyInArrayStringEmpty([vm.product.name]);
    if (validationError) {
      vm.error = 'Debe ingresar Nombre del producto';
    } else {
      vm.loading = true;
      if (vm.product.id === undefined) {
        create();
      } else {
        update(false);
      };
    };
  };
                                      
  vm.remove = function() {
    vm.product.active = false;
    update(true);
  };
                                      
  vm.cancel = function() {
    vm.wizard = 0;
    vm.product = undefined;
    vm.selectedGroup = undefined;
    vm.selectedLine = undefined;
    vm.success = undefined;
    vm.error = undefined;
  };

  vm.exit = function() {
    $location.path('/home');
  };
                                      
  (function initController() {
    _activate();
  })();
});
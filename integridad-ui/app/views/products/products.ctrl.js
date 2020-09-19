'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:ProjectsCtrl
 * @description
 * # ProjectsCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('ProductsCtrl', function(_, holderService, $location, productService, utilStringService, projectService,
                                          subsidiaryService, productTypeService, messurementListService, brandService, lineService, groupService,
                                          subgroupService, warehouseService, cuentaContableService, $routeParams) {

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
        vm.maxCode = undefined;
        vm.warehouseList = undefined;
        vm.cuentasContablesList = undefined;
        vm.brandLineError = undefined;

        function _activate() {
            vm.user = holderService.get();
            vm.searchText = undefined;
            vm.productBarCode = undefined;
            vm.errorCalc = false;
            vm.loading = true;
            vm.userId = vm.user.id;
            vm.userClientId = vm.user.subsidiary.userClient.id;
            vm.subKarActive = vm.user.subsidiary.kar;
            vm.subsidiaryMain = vm.user.subsidiary;
            vm.brandLineError = undefined;
            vm.cuentasContablesForProductSale = [];
            vm.cuentasContablesForProductConsume = [];
            vm.cuentasContablesForProductFinished = [];
            vm.cuentasContablesForProductCost = [];
            vm.cuentaContableSale = undefined;
            vm.cuentaContableConsume = undefined;
            vm.cuentaContableFinished = undefined;
            vm.cuentaContableCost = undefined;

            vm.selectedGroup = undefined;
            vm.selectedLine = undefined;
            vm.wizard = 0;
            vm.remveReason = undefined;

            vm.messurements = messurementListService.getMessurementList();
            productTypeService.getproductTypesLazy().then(function(response) {
                vm.productTypes = response;
                setTimeout(function() {
                    document.getElementById("input40").focus();
                }, 200);
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
            vm.page = 0;
            vm.maxCode = undefined;

            warehouseService.getAllWarehouseByUserClientId(vm.userClientId).then(function(response) {
                vm.warehouseList = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });

            cuentaContableService.getCuentaContableByUserClient(vm.userClientId).then(function(response) {
                vm.cuentasContablesList = response;
                if(vm.optionsConfig){
                    _filterAccounts();
                }
                vm.loading = false;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });

            _getLines();
            _filter();
        };

        function _filter() {
            vm.loading = true;
            vm.totalPages = 0;
            vm.productList = [];
            var variable = vm.searchText? vm.searchText : null;
            if (variable == null) {
                var busqueda = variable;
            } else {
                var busqueda = variable.toUpperCase();
            };
            var lineaIdFilter = vm.lineaFilter !== undefined && vm.lineaFilter !== null ?  vm.lineaFilter.id : undefined;
            if ($routeParams.subsidiaryId) {
                productService.getLazyBySusidiaryId($routeParams.subsidiaryId, vm.page, busqueda, lineaIdFilter).then(function(response) {
                    vm.totalPages = response.totalPages;
                    vm.totalElements = response.totalElements;
                    _getProductQuantities(response.content);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                productService.getLazyBySusidiaryId(vm.user.subsidiary.id, vm.page, busqueda, lineaIdFilter).then(function(response) {
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
                    return (s.subsidiary.id === vm.user.subsidiary.id && s.active === true);
                });
                if (sub) {
                    listResponse[i].quantity = sub.quantity
                    if(vm.product !== undefined && vm.product.id === listResponse[i].id){
                        vm.productList.push(vm.product);
                        vm.product = undefined;
                    } else {
                        vm.productList.push(listResponse[i]);
                    }
                };
            };
        };

        function _getSubsidiaries(edit) {
            subsidiaryService.getByProjectId(vm.user.subsidiary.userClient.id).then(function(response) {
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

        vm.fillBarCode = function(event){
            if (event.keyCode === 32 || event.charCode === 32) {
                if (vm.productBarCode.length < 13) {
                    vm.productBarCodeFixed = vm.productBarCode;
                    for (var i = vm.productBarCode.length; i < 13; i++) {
                        vm.productBarCodeFixed = vm.productBarCodeFixed.concat('0');
                    };
                    vm.productBarCode = vm.productBarCodeFixed.trim();
                };
            };
        };

        function _getBrands() {
            brandService.getBrandsLazy(vm.user.subsidiary.userClient.id).then(function(response) {
                vm.brands = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function _getLines() {
            lineService.getLinesLazy(vm.user.subsidiary.userClient.id).then(function(response) {
                vm.lineas = response;
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function create() {
            vm.product.productBySubsidiaries = vm.productBySubsidiaries;
            vm.product.cuentaContableByProducts = [];
            _.each(vm.cuentasContablesForProductSale, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'VENTA'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductConsume, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'CONSUMO'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductFinished, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'PRODUCTO_TERMINADO'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductCost, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'COSTO_DE_VENTA'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            vm.product.barCode = vm.productBarCode;
            vm.product.costEach = 0;
            vm.product.costCellar = 0;
            vm.product.quantityCellar = 0;
            vm.product.averageCostSuggested = 0;
            productService.create(vm.product).then(function (response) {
                vm.product = undefined;
                vm.selectedGroup = undefined;
                vm.selectedLine = undefined;
                vm.wizard = 0;
                _activate();
                vm.error = undefined;
                vm.success = 'Registro realizado con exito';
            }).catch(function (error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };

        function updateEdited(isRemove) {
            var warehouse = _.first(vm.warehouseList)
            _.each(vm.product.productBySubsidiaries, function(ps) {
                ps.active=false;
            });
            _.each(vm.subsidiaries, function(sub) {
                if (sub.selected) {
                  var productBySubsidiary = {
                    dateCreated: new Date().getTime(),
                    quantity: sub.cantidad,
                    subsidiary: sub,
                    active: true
                  };
                  vm.product.productBySubsidiaries.push(productBySubsidiary);

                  vm.kardex = {
                    codeWarehouse: warehouse.codeWarehouse,
                    product: vm.product,
                    subsidiaryId: vm.subsidiaryMain.id,
                    userClientId: vm.userClientId,
                    userId: vm.userId,
                    prodCostEach: vm.product.costEach,
                    prodQuantity: sub.cantidad,
                    prodName: vm.product.name,
                    dateRegister: new Date().getTime(),
                    prodTotal: parseFloat((vm.product.costEach * sub.cantidad).toFixed(2)),
                    observation: 'AJUSTE GENERADO AUTOMATICO',
                    details: 'AJUSTE DESDE MODULO PRODUCTO',
                  };

                  productService.createKardex(vm.kardex).then(function(respKar) {
                    vm.kardexCreated = respKar;
                  }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                  });
                };
            });
            vm.product.barCode = vm.productBarCode;
            productService.updateEdited(angular.copy(vm.product)).then(function(response) {
                vm.error = undefined;
                if (isRemove) {
                    vm.success = 'Registro eliminado con exito';
                } else {
                    vm.success = 'Producto actualizado con exito';
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.success = undefined;
                vm.error = error.data;
            });
            _activate();
        };

        function update(isRemove) {
            vm.loading = true;
            _.each(vm.product.productBySubsidiaries, function(ps) {
                ps.active=false;
            });
            _.each(vm.productBySubsidiaries, function(psNew) {
                vm.product.productBySubsidiaries.push(psNew);
            });
            vm.product.cuentaContableByProducts = [];
            _.each(vm.cuentasContablesForProductSale, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'VENTA'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductConsume, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'CONSUMO'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductFinished, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'PRODUCTO_TERMINADO'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            _.each(vm.cuentasContablesForProductCost, function(cc) {
                var cuentaContableByProduct = {
                    cuentaContable: cc,
                    type: 'COSTO_DE_VENTA'
                };
                vm.product.cuentaContableByProducts.push(cuentaContableByProduct);
            });
            vm.product.barCode = vm.productBarCode;

            if (isRemove){
                var wrapper = {
                    product : vm.product,
                    removeReason : vm.remveReason,
                    date: new Date().getTime(),
                    userId: vm.user.id
                }
                productService.remove(wrapper).then(function(response) {
                    _activate();
                    vm.error = undefined;
                    vm.loading = false;
                    vm.success = 'Registro eliminado con exito';
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                productService.update(vm.product).then(function(response) {
                    _activate();
                    vm.error = undefined;
                    vm.loading = false;
                    vm.success = 'Registro actualizado con exito';
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            }

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
            vm.errorCalc = false;
            var avrCost = 0;
            var gEfectivo = 0;
            if (vm.product === null || vm.product === undefined) {
                var avrCost = 0;
                var gEfectivo = 0;
            } else {
                var avrCost = vm.product.averageCost;
                var gEfectivo = 1 + ((vm.product.cashPercentage) / 100.00);
            };
            var preview = avrCost * gEfectivo;
            if (isNaN(preview)) {
                vm.errorCalc = true;
            };
            return (preview).toFixed(4);
        };

        vm.costIvaPreview = function() {
            var avrCost = 0;
            var gEfectivo = 0;
            var iva = 1.12;
            if (vm.product === null || vm.product === undefined) {
                var avrCost = 0;
                var gEfectivo = 0;
            } else {
                var avrCost = vm.product.averageCost;
                var gEfectivo = 1 + ((vm.product.cashPercentage) / 100.00);
            };
            var preview = avrCost * gEfectivo * iva;
            return (preview).toFixed(4);
        };

        vm.getPvp = function(iva, cashPercen, averageCost) {
            if (iva == true) {
                const IVA = 1.12;
                var aC = 1 + ((cashPercen) / 100);
                var cost = aC * averageCost * IVA;
                return (cost).toFixed(4);
            } else {
                var aC = 1 + ((cashPercen) / 100);
                var cost = aC * averageCost;
                return (cost).toFixed(4);
            };
        };

        vm.getCost = function(cashPercen, averageCost) {
            var aC = 1 + ((cashPercen) / 100);
            var cost = aC * averageCost;
            return (cost).toFixed(4);
        };

        vm.editProduct = function(productEdit) {
            vm.success = undefined;
            vm.error = undefined;
            vm.productBarCode = productEdit.barCode;
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

            _.each(productEdit.cuentaContableByProducts, function(ccp) {
                if(ccp.type === 'VENTA'){
                    vm.cuentasContablesForProductSale = [ccp.cuentaContable];
                    vm.cuentaContableSale = ccp.cuentaContable;
                }
                if(ccp.type === 'PRODUCTO_TERMINADO'){
                    vm.cuentasContablesForProductFinished = [ccp.cuentaContable];
                    vm.cuentaContableFinished = ccp.cuentaContable;
                }
                if(ccp.type === 'CONSUMO'){
                    vm.cuentasContablesForProductConsume = [ccp.cuentaContable];
                    vm.cuentaContableConsume = ccp.cuentaContable;
                }
                if(ccp.type === 'COSTO_DE_VENTA'){
                    vm.cuentasContablesForProductCost = [ccp.cuentaContable];
                    vm.cuentaContableCost = ccp.cuentaContable;
                }
            });

            vm.wizard = 3;
            vm.product = productEdit;
        };

        vm.productCreate = function() {
            _getSubsidiaries(false);
            vm.success = undefined;
            vm.error = undefined;
            vm.brandLineError = undefined;
            vm.productBySubsidiaries = [];
            vm.cuentasContablesForProductSale = [];
            vm.cuentasContablesForProductConsume = [];
            vm.cuentasContablesForProductFinished = [];
            vm.cuentasContablesForProductCost = [];
            vm.wizard = 1;

            productService.getLastCodeByUserClientIdActive(vm.userClientId).then(
                function(response){
                    vm.maxCode = response || '';
                }
            );

            vm.product = {
                userClient: vm.user.subsidiary.userClient,
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
                userClient: vm.user.subsidiary.userClient,
                code: vm.brands.length + 1,
                active: true
            };
        };

        vm.createLine = function() {
            vm.newLine = {
                userClient: vm.user.subsidiary.userClient,
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
            var repeatedCode = _.filter(vm.brands, function(brand){ return brand.code === vm.newBrand.code; });

            if(_.isEmpty(repeatedCode)){
                vm.brandLineError = undefined;
                brandService.create(vm.newBrand).then(function(response) {
                    vm.brands.push(response);
                    vm.product.brand = response;
                    vm.newBrand = undefined;
                    $('#modalCreateBrand').modal('hide');
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                    vm.brandLineError = error.data;
                });
            } else {
                vm.brandLineError = 'Código Duplicado';
            }
            
        };

        vm.saveNewLine = function() {
            var repeatedCode = _.filter(vm.lineas, function(line){ return line.code === vm.newLine.code; });

            if(_.isEmpty(repeatedCode)){
                vm.brandLineError = undefined;
                lineService.create(vm.newLine).then(function(response) {
                    vm.lineas.push(response);
                    vm.selectedLine = response;
                    vm.newLine = undefined;
                    vm.groups = [];
                    $('#modalCreateLine').modal('hide');
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                    m.brandLineError = error.data;
                });
            } else {
                vm.brandLineError = 'Código Duplicado';
            }
            
        };

        vm.saveNewGroup = function() {
            var repeatedCode = _.filter(vm.groups, function(gr){ return gr.code === vm.newGroup.code; });

            if(_.isEmpty(repeatedCode)){
                vm.brandLineError = undefined;
                groupService.create(vm.newGroup).then(function(response) {
                    vm.groups.push(response);
                    vm.selectedGroup = response;
                    vm.newGroup = undefined;
                    vm.subGroups = [];
                    $('#modalCreateGroup').modal('hide');
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                    vm.brandLineError = error.data;
                });
            } else {
                vm.brandLineError = 'Código Duplicado';
            }
            
        };

        vm.saveNewSubGroup = function() {
            var repeatedCode = _.filter(vm.subGroups, function(sub){ return sub.code === vm.newSubGroup.code; });

            if(_.isEmpty(repeatedCode)){
                subgroupService.create(vm.newSubGroup).then(function(response) {
                    vm.subGroups.push(response);
                    vm.product.subgroup = response;
                    vm.newSubGroup = undefined;
                    $('#modalCreateSubGroup').modal('hide');
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                    vm.brandLineError = error.data;
                });
            } else {
                vm.brandLineError = 'Código Duplicado';
            }
            
        };

        vm.validateAndContinueToWiz2 = function() {
          vm.productBySubsidiaries = [];
          vm.product.codeIntegridad = vm.product.codeIntegridad.trim();
          var isSubsidiarySelected = false;
          vm.error = undefined;

          if (vm.product.productType.code !== 'SER') {
            vm.product.unitOfMeasurementAbbr = vm.messurementSelected.shortName;
            vm.product.unitOfMeasurementFull = vm.messurementSelected.name;
          };
          _.each(vm.subsidiaries, function(sub) {
            if (sub.selected) {
              isSubsidiarySelected = true;
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
          //********************************************************************************************************************************** */
        //   _getLines();
          isSubsidiarySelected ? vm.wizard = 2 : vm.error = 'Debe Seleccionar por lo menos una matriz.';
        };

        vm.wiz2 = function() {
            vm.error = undefined;

            if(vm.product.id === undefined){
              productService.getProdByUserClientIdAndCodeIntegActive(vm.userClientId, vm.product.codeIntegridad).then(function(response) {
                  if (response.length === 0) {
                      vm.validateAndContinueToWiz2()
                  } else {
                      vm.error = "Código de Producto ya existente, favor Ingrese otro...";
                      vm.loading = false;
                  };
              }).catch(function(error) {
                  vm.loading = false;
                  vm.error = error.data;
              });
            } else {
              vm.validateAndContinueToWiz2();
            }
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
                    vm.subKarActive ? update(false) : updateEdited(false);
                };
            };
        };

        vm.enableSave = function(){
            if(vm.product !== undefined && vm.product.productType !== undefined){
                if(vm.product.productType.code === 'SER'){
                    return vm.product.averageCost === undefined || vm.product.averageCost === '' || vm.errorCalc;
                } else {
                    return vm.product.maxMinimun === undefined || vm.product.maxMinimun === '' || vm.product.averageCost === undefined || vm.product.averageCost === '' || vm.errorCalc;
                }
            } else {
                return false;
            } 
        };

        vm.remove = function() {
            vm.product.active = false;
            update(true);
        };

        vm.addCuentaContableSale = function(){
            if (vm.cuentaContableSale !== undefined && vm.cuentaContableSale.id !== undefined) {
                vm.cuentasContablesForProductSale.push(vm.cuentaContableSale);
            };
        };

        vm.removeCCSale = function(cc){
            vm.cuentasContablesForProductSale = _.filter(vm.cuentasContablesForProductSale, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.addCuentaContableConsume = function(){
            if (vm.cuentaContableConsume !== undefined && vm.cuentaContableConsume.id !== undefined) {
                vm.cuentasContablesForProductConsume.push(vm.cuentaContableConsume);
            };
        };

        vm.removeCCConsume = function(cc){
            vm.cuentasContablesForProductConsume = _.filter(vm.cuentasContablesForProductConsume, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.addCuentaContableFinished = function(){
            if (vm.cuentaContableFinished !== undefined && vm.cuentaContableFinished.id !== undefined) {
                vm.cuentasContablesForProductFinished.push(vm.cuentaContableFinished);
            };
        };

        vm.removeCCFinished = function(cc){
            vm.cuentasContablesForProductFinished = _.filter(vm.cuentasContablesForProductFinished, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.addCuentaContableCost = function(){
            if (vm.cuentaContableCost !== undefined && vm.cuentaContableCost.id !== undefined) {
                vm.cuentasContablesForProductCost.push(vm.cuentaContableCost);
            };
        };

        vm.removeCCCost = function(cc){
            vm.cuentasContablesForProductCost = _.filter(vm.cuentasContablesForProductCost, function(cuenta){return cuenta.name !== cc.name})
        };

        vm.cancel = function() {
            vm.wizard = 0;
            vm.product = undefined;
            vm.selectedGroup = undefined;
            vm.selectedLine = undefined;
            vm.success = undefined;
            vm.error = undefined;
            vm.cuentasContablesForProductSale = [];
            vm.cuentasContablesForProductConsume = [];
            vm.cuentasContablesForProductFinished = [];
            vm.cuentasContablesForProductCost = [];
            vm.cuentaContableSale = undefined;
            vm.cuentaContableConsume = undefined;
            vm.cuentaContableFinished = undefined;
            vm.cuentaContableCost = undefined;
        };

        vm.import = function() {
            var f = document.getElementById('file').files[0];
            if(f){
                var r = new FileReader();
                r.onload = function(event) {
                    var data = event.target.result;
                    var workbook = XLSX.read(data, {
                    type: "binary"
                    });
                    workbook.SheetNames.forEach(function(sheet) {
                        var productos = XLSX.utils.sheet_to_row_object_array(
                        workbook.Sheets[sheet]
                        );
                        
                        _.each(productos, function(newProduct){
                            var productBySubsidiary = {
                                dateCreated: new Date().getTime(),
                                quantity: newProduct.quantity,
                                subsidiary: vm.user.subsidiary,
                                active: true
                            };

                            newProduct.productType = _.find(vm.productTypes, function(t){ return t.code == newProduct.type; });
                            newProduct.userClient = vm.user.subsidiary.userClient;
                            newProduct.dateCreated = new Date().getTime();
                            newProduct.lastDateUpdated = new Date().getTime();
                            newProduct.active = true;
                            newProduct.quantityCellar = 0;
                            newProduct.costCellar = newProduct.averageCost;
                            newProduct.costEach = newProduct.averageCost;
                            newProduct.averageCostSuggested = newProduct.averageCost;
                            newProduct.productBySubsidiaries=[productBySubsidiary]
                        })

                        productService.createList(productos).then(function(response) {
                            _activate();
                            vm.error = undefined;
                            $('#modalUpload').modal('hide');
                            vm.success = 'Registros creados con exito';
                        }).catch(function(error) {
                            vm.loading = false;
                            vm.error = error.data;
                        });

                    });
                };
                r.readAsBinaryString(f);
            } 
        };

        vm.exit = function() {
            $location.path('/home');
        };

        (function initController() {
            _activate();
        })();
});

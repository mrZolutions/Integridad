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
        vm.maxCode = undefined;
        
        function _activate() {
            vm.searchText = undefined;
            vm.productBarCode = undefined;
            vm.errorCalc = false;
            vm.loading = true;
            vm.userClientId = $localStorage.user.subsidiary.userClient.id;
            vm.subKarActive = $localStorage.user.subsidiary.kar;
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
            if ($routeParams.subsidiaryId) {
                productService.getLazyBySusidiaryId($routeParams.subsidiaryId, vm.page, busqueda).then(function(response) {
                    vm.totalPages = response.totalPages;
                    vm.totalElements = response.totalElements;
                    _getProductQuantities(response.content);
                    vm.loading = false;
                }).catch(function(error) {
                    vm.loading = false;
                    vm.error = error.data;
                });
            } else {
                productService.getLazyBySusidiaryId($localStorage.user.subsidiary.id, vm.page, busqueda).then(function(response) {
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
            productService.getProdByUserClientIdAndCodeIntegActive(vm.userClientId, vm.product.codeIntegridad).then(function(response) {
                if (response.length === 0) {
                    vm.product.barCode = vm.productBarCode;
                    vm.product.costEach = 0;
                    vm.product.costCellar = 0;
                    vm.product.quantityCellar = 0;
                    vm.product.averageCostSuggested = 0;
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
                } else {
                    vm.error = "Código de Producto ya existente, favor Ingrese otro...";
                    vm.loading = false;
                };
            }).catch(function(error) {
                vm.loading = false;
                vm.error = error.data;
            });
        };
                                            
        function updateEdited(isRemove) {
            _.each(vm.product.productBySubsidiaries, function(ps) {
                ps.active=false;
            });
            _.each(vm.productBySubsidiaries, function(psNew) {
                vm.product.productBySubsidiaries.push(psNew);
            });
            vm.product.barCode = vm.productBarCode;
            productService.updateEdited(vm.product).then(function(response) {
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

        function update(isRemove) {
            _.each(vm.product.productBySubsidiaries, function(ps) {
                ps.active=false;
            });
            _.each(vm.productBySubsidiaries, function(psNew) {
                vm.product.productBySubsidiaries.push(psNew);
            });
            vm.product.barCode = vm.productBarCode;
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

        vm.filterEvent = function(event) {
            vm.page = 0;
            if (event.keyCode === 13 || event.charCode === 13) {
                _filter();
            };
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
            vm.wizard = 1;
            vm.product = productEdit;
        };
                                            
        vm.productCreate = function() {
            _getSubsidiaries(false);
            vm.success = undefined;
            vm.error = undefined
            vm.productBySubsidiaries = [];
            vm.wizard = 1;

            productService.getLastCodeByUserClientIdActive(vm.userClientId).then(
                function(response){
                    vm.maxCode = response || '';
                }
            );

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
            _getLines();
            isSubsidiarySelected ? vm.wizard = 2 : vm.error = 'Debe Seleccionar por lo menos una matriz.'
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
'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:MenuCtrl
 * @description
 * # MenuCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('MenuCtrl', function($rootScope, $scope, holderService, modulesService, $localStorage, $location) {
        $scope.permissions = [];
        $scope.permissionsSub = [];
        $scope.modules = [];

        
        
        function getModules(){
            modulesService.getAll().then(function (response){
                $scope.modules = response;
            });
        }

        $rootScope.updateMenu = function() {
            if ($scope.modules.length === 0) {
                getModules();
            }
            $scope.user = holderService.get();
            $scope.permissions = $localStorage.permissions;
            $scope.nameType = $scope.user.firstName + ' - ' + $scope.user.userType.name;
        };

        $scope.showMainManu = function(){ 
            return $scope.user !== undefined
        }

        $scope.getClass = function(item){
            return item.sons !== undefined ? 'dropdown-submenu' : '';
        }

        $scope.logout = function() {
            $scope.user = undefined;
            $scope.permissions = [];
            $scope.permissionsSub = [];
            $scope.nameType = undefined;
            $localStorage.permissions = undefined;
            $localStorage.timeloged = undefined;
            holderService.set(undefined);
            $location.path('/');
        };

        $scope.$on('onBeforeUnload', function(e, confirmation) {
            $scope.logout();
        });

        $scope.setLayout = function(){
            return $scope.nameType ? 'col-md-10' : 'col-md-12';
        }
        
        if ($localStorage.timeloged) {
            var dateNow = new Date();
            var timeNow = dateNow.getTime()
            var timeLapsed = timeNow - $localStorage.timeloged;
            if (timeLapsed > 86400000) {
                $scope.logout();
            };
        };

        if ($localStorage.permissions) {
            $rootScope.updateMenu();
        };

        getModules();
});
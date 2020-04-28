'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:MenuCtrl
 * @description
 * # MenuCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('MenuCtrl', function($rootScope, $scope, holderService, $localStorage, $location) {
        $scope.permissions = [];
        $scope.permissionsSub = [];
        $rootScope.updateMenu = function() {
            $scope.user = holderService.get();
            $scope.permissions = _.filter($localStorage.permissions, function(perm){ return perm.type === 'null'; })[0].menu;
            $scope.permissionsSub = _.filter($localStorage.permissions, function(perm){ return perm.type !== 'null'; });
            console.log($scope.permissionsSub)
            $scope.nameType = $scope.user.firstName + ' - ' + $scope.user.userType.name;
        };

        $scope.logout = function() {
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
});
'use strict';
/**
 * @ngdoc function
 * @name integridadUiApp.controller:MenuCtrl
 * @description
 * # MenuCtrl
 * Controller of the menu
 */
angular.module('integridadUiApp')
    .controller('MenuCtrl', function($rootScope, $scope, $localStorage, $location) {
        $scope.permissions = [];
        $rootScope.updateMenu = function() {
            $scope.permissions = $localStorage.permissions;
            $scope.nameType = $localStorage.user.firstName + ' - ' + $localStorage.user.userType.name;
        };

        $scope.logout = function() {
            $scope.permissions = [];
            $scope.nameType = undefined;
            $localStorage.permissions = undefined;
            $localStorage.user = undefined;
            $localStorage.timeloged = undefined;
            $location.path('/');
        };

        $scope.$on('onBeforeUnload', function(e, confirmation) {
            $scope.logout();
        });

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
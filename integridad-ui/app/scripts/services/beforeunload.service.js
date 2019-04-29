angular
    .module('app.services')
    .factory('beforeunload', function($rootScope, $window) {
        // Events are broadcast outside the Scope Lifecycle
        $window.onbeforeunload = function(e) {
            var confirmation = {};
            var event = $rootScope.$broadcast('onBeforeUnload', confirmation);
        };
        $window.onunload = function() {
            $rootScope.$broadcast('onUnload');
        };
        return {};
}).run(function(beforeunload) {
    // Must invoke the service at least once
});

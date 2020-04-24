angular
    .module('app.services')
    .service('holderService', function () {
        var user = undefined;

        this.set = function (user) {
            this.user = user
        };

        this.get = function () {
            return this.user;
        };
});
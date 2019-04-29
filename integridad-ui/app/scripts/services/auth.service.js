angular
    .module('app.services')
    .service('authService', function(securityService) {
        this.authUser = function(user) {
            return securityService.post('/user/auth',user).then(function successCallback(response) {
                return response.data;
            });
        };

        this.registerUser = function(user) {
            return securityService.post('/user',user).then(function successCallback(response) {
                return response.data;
            });
        };

        this.activeUser = function(userId, validate) {
            return securityService.put('/user/'+userId+'/'+validate).then(function successCallback(response) {
                return response.data;
            });
        };

        this.updateUser = function(user) {
            return securityService.put('/user', user).then(function successCallback(response) {
                return response.data;
            });
        };

        this.recoverUser = function(user) {
            return securityService.post('/user/recover', user).then(function successCallback(response) {
                return response.data;
            });
        };

        this.getLazy = function() {
            return securityService.get('/user/lazy').then(function successCallback(response) {
                return response.data;
            });
        };

        this.getBosses = function(code, subsidiaryId) {
            return securityService.get('/user/lazy/bosses/'+subsidiaryId+'/'+code).then(function successCallback(response) {
                return response.data;
            });
        };

        //
        // this.findById = function (auditFirmId) {
        //     return securityService.get('/audit-firms/' + auditFirmId).then(function successCallback(response) {
        //         return response.data;
        //     });
        // };
        //
        // this.save = function (auditFirm) {
        //     return securityService.post('/audit-firms', auditFirm).then(function successCallback(response) {
        //         return response.data;
        //     });
        // };
        //
        // this.update = function (auditFirmId, auditFirm) {
        //     return securityService.put('/audit-firms/' + auditFirmId, auditFirm).then(function successCallback(response) {
        //         return response.data;
        //     });
        // };
});
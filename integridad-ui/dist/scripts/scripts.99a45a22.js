function routes(a){a.when("/",{templateUrl:"views/main.html",controller:"MainCtrl",controllerAs:"vm"}).when("/activate/:idUSer/:validate",{templateUrl:"views/activate/activate.tpl.html",controller:"ActivateCtrl",controllerAs:"vm"}).when("/home",{templateUrl:"views/home/home.tpl.html",controller:"HomeCtrl",controllerAs:"vm"}).otherwise({redirectTo:"/"})}var app=angular.module("integridadUiApp",["app.routes","app.core","app.directives"]);angular.module("app.routes",["ngRoute"]).config(routes),routes.$inject=["$routeProvider"],angular.module("app.services",["base64"]),angular.module("app.core",["app.services","ui.bootstrap","ngResource","toaster","ngAnimate","ngStorage","angularUtils.directives.dirPagination"]),angular.module("app.directives",["ngPatternRestrict"]),angular.module("integridadUiApp").controller("MainCtrl",["$location","authService","utilValidationService",function(a,b,c){var d=this;d.loading=!1,d.init=!0,d.userIntegridad={},d.error=void 0,d.success=void 0,d.login=function(){d.loading=!0;var c={email:d.email,password:d.password};b.authUser(c).then(function(b){d.loading=!1,a.path("/home")})["catch"](function(a){d.loading=!1,d.error=a.data})},d.register=function(){d.userIntegridad.email=d.userIntegridad.email.trim(),d.userIntegridad.password=d.userIntegridad.password.trim();var a=c.isAnyInArrayStringEmpty([d.userIntegridad.email,d.userIntegridad.password,d.userIntegridad.firstName,d.userIntegridad.lastName]);a?d.error="Debe ingresar Nombres completos, email y password":(a=c.isStringEmpty(d.userIntegridad.cedula)&&c.isStringEmpty(d.userIntegridad.ruc),a&&(d.error="Debe ingresar un numero de cedula o ruc")),a||(d.loading=!0,b.registerUser(d.userIntegridad).then(function(a){d.loading=!1,d.error=void 0,d.init=!0,d.success="Resgistro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta"})["catch"](function(a){d.loading=!1,d.error=a.data}))}}]),angular.module("integridadUiApp").controller("ActivateCtrl",["$routeParams","$location","authService",function(a,b,c){var d=this;d.errorActive=!1,c.activeUser(a.idUSer,a.validate).then(function(a){b.path("/home")})["catch"](function(a){d.errorActive=!0,d.error=a.data})}]),angular.module("integridadUiApp").controller("HomeCtrl",function(){}),angular.module("app.services").service("securityService",["$http","$base64",function(a,b){var c="https://integridad.herokuapp.com/integridad/v1",d={},e=b.encode("dan:12345");d.headers={"Content-Type":"application/json",Authorization:"Basic "+e},this.get=function(b){return a.get(c+b)},this["delete"]=function(b){return a["delete"](c+b)},this.post=function(b,e){var f=c+b;return a.post(f,e,d)},this.put=function(b,e){var f=c+b;return a.put(f,e,d)}}]),angular.module("app.services").service("utilValidationService",function(){this.isStringEmpty=function(a){return a?!1:!0},this.isAnyInArrayStringEmpty=function(a){for(var b,c=0;c<a.length;c++){if(!a[c]){b=!0;break}b=!1}return b}}),angular.module("app.services").service("authService",["securityService",function(a){this.authUser=function(b){return a.post("/user/auth",b).then(function(a){return a.data})},this.registerUser=function(b){return a.post("/user",b).then(function(a){return a.data})},this.activeUser=function(b,c){return a.put("/user/"+b+"/"+c).then(function(a){return a.data})}}]),angular.module("integridadUiApp").run(["$templateCache",function(a){"use strict";a.put("views/activate/activate.tpl.html",'<div class="panel panel-default"> <div class="panel-body"> <div class="row" ng-show="vm.errorActive"> <div class="form-group col-md-12"> <div class="alert alert-danger">{{vm.error}}</div> </div> </div> </div> </div>'),a.put("views/home/home.tpl.html",'<div class="panel panel-default"> <div class="panel-body"> <div class="row"> <div class="form-group col-md-12"> <h1>HOLA HOLA <br> Gracias por ingresar al Sistema Integridad</h1> </div> </div> </div> </div>'),a.put("views/main.html",'<div style="width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99; background: rgba(255, 255, 255, 0.3)" ng-show="vm.loading"> <div style="position: absolute;top: 15%;z-index: 100; width: 100%; text-align: center"> <div class="sk-circle"> <div class="sk-circle1 sk-child"></div> <div class="sk-circle2 sk-child"></div> <div class="sk-circle3 sk-child"></div> <div class="sk-circle4 sk-child"></div> <div class="sk-circle5 sk-child"></div> <div class="sk-circle6 sk-child"></div> <div class="sk-circle7 sk-child"></div> <div class="sk-circle8 sk-child"></div> <div class="sk-circle9 sk-child"></div> <div class="sk-circle10 sk-child"></div> <div class="sk-circle11 sk-child"></div> <div class="sk-circle12 sk-child"></div> </div> </div> </div> <div class="row" ng-show="vm.init"> <div class="col-md-8 col-md-offset-2"> <img src="images/zolutions1.36c9be71.jpg"><br> </div> </div> <div class="panel panel-default" ng-show="vm.init"> <div class="panel-body"> <div class="row" ng-show="vm.error !== undefined"> <div class="form-group col-md-12"> <div class="alert alert-danger">{{vm.error}}</div> </div> </div> <div class="row" ng-show="vm.success !== undefined"> <div class="form-group col-md-12"> <div class="alert alert-success">{{vm.success}}</div> </div> </div> <div class="row"> <div class="form-group col-md-6"> <label>E-mail: </label> <input type="text" ng-model="vm.email" class="form-control"> </div> <div class="form-group col-md-6"> <label>Password: </label> <input type="password" ng-model="vm.password" class="form-control"> </div> </div> </div> <div class="panel-footer"> <button type="button" class="btn btn-primary" ng-click="vm.login()" ng-disabled="vm.email === undefined || vm.password === undefined"> Ingresar </button> <button type="button" class="btn btn-success" ng-click="vm.init = false; vm.userIntegridad = {}; vm.success=undefined; vm.error=undefined"> Registrarse </button> </div> </div> <div class="panel panel-default" ng-show="!vm.init"> <div class="panel-body"> <div class="row" ng-show="vm.error !== undefined"> <div class="form-group col-md-12"> <div class="alert alert-danger">{{vm.error}}</div> </div> </div> <div class="row" ng-show="vm.success !== undefined"> <div class="form-group col-md-12"> <div class="alert alert-success">{{vm.success}}</div> </div> </div> <div class="row"> <div class="form-group col-md-6"> <label>Nombre: </label> <input type="text" ng-model="vm.userIntegridad.firstName" class="form-control"> </div> <div class="form-group col-md-6"> <label>Apellido: </label> <input type="text" ng-model="vm.userIntegridad.lastName" class="form-control"> </div> </div> <div class="row"> <div class="form-group col-md-6"> <label>Tel&eacute;fono: (incluir c&oacute;digo de provincia ej: 02) </label> <input type="text" onkeypress="return event.charCode >= 48 && event.charCode <= 57" ng-model="vm.userIntegridad.phone" class="form-control" maxlength="9"> </div> <div class="form-group col-md-6"> <label>Tel&eacute;fono celular: </label> <input type="text" onkeypress="return event.charCode >= 48 && event.charCode <= 57" ng-model="vm.userIntegridad.celPhone" class="form-control" maxlength="10"> </div> </div> <div class="row"> <div class="form-group col-md-12"> <label>Direcci&oacute;n: </label> <input type="text" ng-model="vm.userIntegridad.address1" class="form-control"> </div> </div> <div class="row"> <div class="form-group col-md-12"> <label>Intersecci&oacute;n: </label> <input type="text" ng-model="vm.userIntegridad.address2" class="form-control"> </div> </div> <div class="row"> <div class="form-group col-md-6"> <label>C&eacute;dula: </label> <input type="text" onkeypress="return event.charCode >= 48 && event.charCode <= 57" ng-model="vm.userIntegridad.cedula" class="form-control" maxlength="10"> </div> <div class="form-group col-md-6"> <label>Ruc: </label> <input type="text" onkeypress="return event.charCode >= 48 && event.charCode <= 57" ng-model="vm.userIntegridad.ruc" class="form-control" maxlength="13"> </div> </div> <div class="row"> <div class="form-group col-md-6"> <label>E-mail: </label> <input type="text" ng-model="vm.userIntegridad.email" class="form-control"> </div> <div class="form-group col-md-6"> <label>Password: </label> <input type="password" ng-model="vm.userIntegridad.password" class="form-control"> </div> </div> </div> <div class="panel-footer"> <button type="button" class="btn btn-primary" ng-click="vm.register()" ng-disabled="vm.userIntegridad.email === undefined || vm.userIntegridad.password === undefined"> Registrar </button> <button type="button" class="btn btn-success" ng-click="vm.init = true; vm.userIntegridad = {}; vm.success=undefined; vm.error=undefined"> Cancelar </button> </div> </div>')}]);
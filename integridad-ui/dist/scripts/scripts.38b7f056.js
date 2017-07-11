function routes(a){a.when("/",{templateUrl:"views/main.html",controller:"MainCtrl",controllerAs:"vm"}).when("/activate/:idUSer/:validate",{templateUrl:"views/activate/activate.tpl.html",controller:"ActivateCtrl",controllerAs:"vm"}).when("/home",{templateUrl:"views/home/home.tpl.html",controller:"HomeCtrl",controllerAs:"vm"}).otherwise({redirectTo:"/"})}var app=angular.module("integridadUiApp",["app.routes","app.core","app.directives"]);angular.module("app.routes",["ngRoute"]).config(routes),routes.$inject=["$routeProvider"],angular.module("app.services",["base64"]),angular.module("app.core",["app.services","ui.bootstrap","ngResource","toaster","ngAnimate","ngStorage","angularUtils.directives.dirPagination"]),angular.module("app.directives",["ngPatternRestrict"]),angular.module("integridadUiApp").controller("MainCtrl",["$location","authService",function(a,b){var c=this;c.loading=!1,c.error=void 0,c.success=void 0,c.login=function(){c.loading=!0;var d={email:c.email,password:c.password};b.authUser(d).then(function(b){c.loading=!1,a.path("/home")})["catch"](function(a){c.loading=!1,c.error=a.data})},c.register=function(){if(c.email=c.email.trim(),c.password=c.password.trim(),""===c.email||""===c.password)c.error="Debe ingresar un Email y Password";else{c.loading=!0;var a={email:c.email,password:c.password};b.registerUser(a).then(function(a){c.loading=!1,c.error=void 0,c.success="Resgistro realizado con exito. Se envio un email a la cuenta registrada para activar su cuenta"})["catch"](function(a){c.loading=!1,c.error=a.data})}}}]),angular.module("integridadUiApp").controller("ActivateCtrl",["$routeParams","$location","authService",function(a,b,c){var d=this;d.errorActive=!1,c.activeUser(a.idUSer,a.validate).then(function(a){b.path("/home")})["catch"](function(a){d.errorActive=!0,d.error=a.data})}]),angular.module("integridadUiApp").controller("HomeCtrl",function(){}),angular.module("app.services").service("securityService",["$http","$base64",function(a,b){var c="https://integridad.herokuapp.com/integridad/v1",d={},e=b.encode("dan:12345");d.headers={"Content-Type":"application/json",Authorization:"Basic "+e},this.get=function(b){return a.get(c+b)},this["delete"]=function(b){return a["delete"](c+b)},this.post=function(b,e){var f=c+b;return a.post(f,e,d)},this.put=function(b,e){var f=c+b;return a.put(f,e,d)}}]),angular.module("app.services").service("authService",["securityService",function(a){this.authUser=function(b){return a.post("/user/auth",b).then(function(a){return a.data})},this.registerUser=function(b){return a.post("/user",b).then(function(a){return a.data})},this.activeUser=function(b,c){return a.put("/user/"+b+"/"+c).then(function(a){return a.data})}}]),angular.module("integridadUiApp").run(["$templateCache",function(a){"use strict";a.put("views/activate/activate.tpl.html",'<div class="panel panel-default"> <div class="panel-body"> <div class="row" ng-show="vm.errorActive"> <div class="form-group col-md-12"> <div class="alert alert-danger">{{vm.error}}</div> </div> </div> </div> </div>'),a.put("views/home/home.tpl.html",'<div class="panel panel-default"> <div class="panel-body"> <div class="row"> <div class="form-group col-md-12"> <h1>HOLA HOLA <br> Gracias por ingresar al Sistema Integridad</h1> </div> </div> </div> </div>'),a.put("views/main.html",'<div style="width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99; background: rgba(255, 255, 255, 0.3)" ng-show="vm.loading"> <div style="position: absolute;top: 15%;z-index: 100; width: 100%; text-align: center"> <div class="sk-circle"> <div class="sk-circle1 sk-child"></div> <div class="sk-circle2 sk-child"></div> <div class="sk-circle3 sk-child"></div> <div class="sk-circle4 sk-child"></div> <div class="sk-circle5 sk-child"></div> <div class="sk-circle6 sk-child"></div> <div class="sk-circle7 sk-child"></div> <div class="sk-circle8 sk-child"></div> <div class="sk-circle9 sk-child"></div> <div class="sk-circle10 sk-child"></div> <div class="sk-circle11 sk-child"></div> <div class="sk-circle12 sk-child"></div> </div> </div> </div> <div class="row"> <div class="col-md-8 col-md-offset-2"> <img src="images/zolutions1.36c9be71.jpg"><br> </div> </div> <div class="panel panel-default"> <div class="panel-body"> <div class="row" ng-show="vm.error !== undefined"> <div class="form-group col-md-12"> <div class="alert alert-danger">{{vm.error}}</div> </div> </div> <div class="row" ng-show="vm.success !== undefined"> <div class="form-group col-md-12"> <div class="alert alert-success">{{vm.success}}</div> </div> </div> <div class="row"> <div class="form-group col-md-6"> <label>E-mail: </label> <input type="text" ng-model="vm.email" class="form-control"> </div> <div class="form-group col-md-6"> <label>Password: </label> <input type="password" ng-model="vm.password" class="form-control"> </div> </div> </div> <div class="panel-footer"> <button type="button" class="btn btn-primary" ng-click="vm.login()" ng-disabled="vm.email === undefined || vm.password === undefined"> Ingresar </button> <button type="button" class="btn btn-success" ng-click="vm.register()" ng-disabled="vm.email === undefined || vm.password === undefined"> Registrarse </button> </div> </div>')}]);
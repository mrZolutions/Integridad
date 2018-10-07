angular
  .module('app.services')
  .service('utilStringService', function () {

    this.isStringEmpty = function(value){
      if( value ) {
        return false;
      } else {
        return true;
      }
    }

    this.isAnyInArrayStringEmpty = function(arrayString){
      var isEmpty;
      for(var i = 0; i<arrayString.length; i++){
        if( arrayString[i] ) {
          isEmpty = false;
        } else {
          isEmpty = true;
          break;
        }
      }

      return isEmpty;
    }

    this.randomString = function(){
      var text = "";
      var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

      for (var i = 0; i < 5; i++)
        text += possible.charAt(Math.floor(Math.random() * possible.length));

      return text;
    }

  });

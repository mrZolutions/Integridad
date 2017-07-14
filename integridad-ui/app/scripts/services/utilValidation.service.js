angular
  .module('app.services')
  .service('utilValidationService', function () {

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

  });

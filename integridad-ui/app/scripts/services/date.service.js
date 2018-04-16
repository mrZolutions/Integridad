angular
  .module('app.services')
  .service('dateService', function () {
    this.getIsoDate = function(dateBillPre){
      var dateReturn = new Date(dateBillPre.getTime() - (5*60*60*1000));
      return dateReturn.toISOString().toString();
    }
  });

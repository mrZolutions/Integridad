angular
  .module('app.services')
  .service('dateService', function () {
    this.getIsoDate = function(dateBillPre){
      console.log('---- 1: ', dateBillPre)
      // var dateReturn = new Date(dateBillPre.getTime() - (5*60*60*1000));
      var dateReturn = new Date(dateBillPre.getTime());
      console.log('---- 2: ', dateReturn)
      return dateReturn.toISOString().toString();
    }
  });

angular
  .module('app.services')
  .service('messurementListService', function () {

    this.getMessurementList = function(){

      var messurementList = [
        {shortName: 'UND', name:'UNIDAD'},
        {shortName: 'LT', name:'LITRO'},
        {shortName: 'KL', name:'KILO'},
        {shortName: 'LB', name:'LIBRA'},
        {shortName: 'MT', name:'METRO'},
        {shortName: 'GR', name:'GRAMO'},
        {shortName: 'GL', name:'GALON'}
      ];
      
      return messurementList;
    }

  });

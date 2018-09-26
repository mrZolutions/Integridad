angular
  .module('app.services')
  .service('eretentionService', function (_, dateService, securityService) {

    function getTipyCode(code){
      tipyIdCode = {
        RUC : '04',
        CED : '05'
      };
      return tipyIdCode[code];
    }

    this.createERetention = function(retention, user){
      var eRet = {
        "ambiente": 2,
        "tipo_emision": 1,
        "secuencial": parseInt(user.cashier.retentionNumberSeq) + 1,
        "fecha_emision": dateService.getIsoDate($('#pickerBillDateRetention').data("DateTimePicker").date().toDate()),
        "periodo_fiscal": retention.ejercicio,
        "emisor":{
          "ruc":user.cashier.subsidiary.userClient.ruc,
          "obligado_contabilidad":true,
          "contribuyente_especial":"",
          "nombre_comercial":user.cashier.subsidiary.userClient.name,
          "razon_social":user.cashier.subsidiary.userClient.name,
          "direccion":user.cashier.subsidiary.userClient.address1,
          "establecimiento":{
            "punto_emision":user.cashier.threeCode,
            "codigo":user.cashier.subsidiary.threeCode,
            "direccion":user.cashier.subsidiary.address1
          }
        },
        "sujeto":{
          "email":retention.provider.email,
          "identificacion":retention.provider.ruc,
          "tipo_identificacion":'04',
          "razon_social":retention.provider.razonSocial,
          "direccion":retention.provider.address1,
          "telefono":retention.provider.phone
        },
        "items":retention.items,
      };

      if(user.cashier.subsidiary.userClient.espTemp){
        eRet.sujeto.email = eRet.sujeto.email + ', facturacionelecppe2018@gmail.com, facturacionppecoca@gmail.com';
      };

      if(user.cashier.subsidiary.userClient.id == '1f9c21d7-a485-482b-b5eb-e363728340b2'){
        eRet.sujeto.email = eRet.sujeto.email + ',ferelozada@yahoo.com';
      };

      _.each(eRet.items, function(item){
        item.codigo = String(item.codigo);
      });

      if (user.cashier.subsidiary.userClient.testMode){
        eRet.ambiente = 1;
      };
      return eRet;
    };

    this.getClaveDeAcceso = function (req, id) {
      return securityService.post('/retention/clave_acceso/'+id, req).then(function successCallback(response) {
        // console.log(response)
        return response;
      });
    };

    this.create = function (retention) {
      return securityService.post('/retention', retention).then(function successCallback(response) {
        return response.data;
      });
    };

    this.getAllByUserClientAndDates = function (userClientId, dateOne, dateTwo) {
      return securityService.get('/retention/rep/retentions/'+ userClientId + '/' + dateOne + '/'+ dateTwo).then(function successCallback(response) {
        return response.data;
      });
    };

  });

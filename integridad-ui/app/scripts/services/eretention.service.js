angular
  .module('app.services')
  .service('eretentionService', function (dateService) {

    function getTipyCode(code){
      tipyIdCode = {
        RUC : '04',
        CED : '05'
      };
      return tipyIdCode[code];
    }

    this.createERetention = function(retention, user){
      //TODO falta agregar el numero seq retention.retentionSeq
      var eRet = {
        "ambiente": 2,
        "tipo_emision": 1,
        "secuencial": retention.retentionSeq,
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
          "razon_social":retention.provider.name,
          "direccion":retention.provider.address,
          "telefono":retention.provider.phone
        },
        "items":retention.items,
      };

      if (user.cashier.subsidiary.userClient.testMode){
        eRet.ambiente = 1;
      }
      return eRet;
    }

  });

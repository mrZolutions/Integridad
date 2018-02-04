angular
  .module('app.services')
  .service('requirementService', function () {

    function getIsoDate(dateBillPre){
      var dateReturn = new Date(dateBillPre.getTime() - (5*60*60*1000));
      return dateReturn.toISOString().toString();
    }

    function getTipyCode(code){
      tipyIdCode = {
        RUC : '04',
        CED : '05'
      };
      return tipyIdCode[code];
    }

    this.createRequirement = function(clientSelected, bill, user, impuestosTotales, items, pagos){
      var pagosDatil = [];
      var credito = undefined;
      _.each(pagos, function(pago){
        if(pago.medio !== 'credito'){
          pagosDatil.push(pago);
        } else {
          if(credito === undefined){
            credito = {};
          }
          var maxDateCred = _.max(pago.credits, function(cred){ return cred.fecha; });

          credito.monto = pago.total;
          var dateToFormat = new Date(maxDateCred.fecha);
          var yyyy = dateToFormat.getFullYear().toString();
          var mm = (dateToFormat.getMonth()+1).toString();
          var dd  = dateToFormat.getDate().toString();

          var mmChars = mm.split('');
          var ddChars = dd.split('');
          credito.fecha_vencimiento = yyyy + '-' + (mmChars[1]?mm:"0"+mmChars[0]) + '-' + (ddChars[1]?dd:"0"+ddChars[0]);
        }
      });
      var req = {
        "ambiente": 1,
        "tipo_emision": 1,
        "secuencial": bill.billSeq,
        "fecha_emision": getIsoDate($('#pickerBillDate').data("DateTimePicker").date().toDate()),
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
        "moneda":"USD",
        "totales":{
          "total_sin_impuestos":bill.subTotal,
          "impuestos":impuestosTotales,
          "importe_total":bill.total,
          "propina":0.0,
          "descuento":bill.discount
        },
        "comprador":{
          "email":clientSelected.email,
          "identificacion":clientSelected.identification,
          "tipo_identificacion":getTipyCode(clientSelected.typeId),
          "razon_social":clientSelected.name,
          "direccion":clientSelected.address,
          "telefono":clientSelected.phone
        },
        "items":items,
        "valor_retenido_iva": 0,
        "valor_retenido_renta": 0,
      };

      if(credito !== undefined){
        req.credito = credito;
      }

      if(!_.isEmpty(pagosDatil)){
        req.pagos = pagosDatil;
      }

      return req;
    }

  });

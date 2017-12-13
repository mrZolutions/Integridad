angular
  .module('app.services')
  .service('billService', function (securityService, ebillService) {


    this.getClaveDeAcceso = function () {
      var test = {
        "pagos": [
          {
            "medio": "cheque",
            "total": 4882.68,
            "propiedades": {
              "numero": "1234567890",
              "banco": "Banco Pacífico"
            }
          }
        ],
        "secuencial": 148,
        "fecha_emision": "2015-02-28T11:28:56.782Z",
        "emisor": {
          "ruc": "0910000000001",
          "razon_social": "XYZ Corporación S.A.",
          "nombre_comercial": "XYZ Corp",
          "direccion": "Av. Primera 234 y calle 5ta",
          "contribuyente_especial": "12345",
          "obligado_contabilidad": true,
          "establecimiento": {
            "codigo": "001",
            "direccion": "Av. Primera 234 y calle 5ta",
            "punto_emision": "002"
          },
          "email": "danielgokuarcos@yahoo.com"
        },
        "numero": "001-002-000000148",
        "valor_retenido_iva": 70.4,
        "valor_retenido_renta": 29.6,
        "compensaciones": [
          {
            "codigo": 1,
            "tarifa": 2,
            "valor": 2
          }
        ],
        "exportacion": {
          "incoterm": {
            "termino": "CIF",
            "lugar": "Guayaquil",
            "total_sin_impuestos": "CIF"
          },
          "origen": {
            "codigo_pais": "EC",
            "puerto": "Guayaquil"
          },
          "destino": {
            "codigo_pais": "CN",
            "puerto": "China"
          },
          "totales": {
            "flete_internacional": 1000,
            "seguro_internacional": 200,
            "gastos_aduaneros": 800,
            "otros_gastos_transporte": 350
          },
          "codigo_pais_adquisicion": "EC"
        },
        "moneda": "USD",
        "id": "4c18cbb3c9d945aeb0aa02195e7db5d3",
        "informacion_adicional": {
          "Tiempo de entrega": "5 días"
        },
        "ambiente": 1,
        "totales": {
          "total_sin_impuestos": 4359.54,
          "descuento": 0,
          "propina": 0,
          "impuestos": [
            {
              "codigo": "2",
              "codigo_porcentaje": "0",
              "base_imponible": 0,
              "valor": 0
            },
            {
              "codigo": "2",
              "codigo_porcentaje": "2",
              "base_imponible": 4359.54,
              "valor": 523.14
            }
          ],
          "importe_total": 4882.68
        },
        "comprador": {
          "razon_social": "Juan Pérez",
          "identificacion": "0987654321",
          "tipo_identificacion": "05",
          "email": "juan.perez@xyz.com",
          "direccion": "Calle única Numero 987",
          "telefono": "046029400"
        },
        "tipo_emision": 1,
        "items": [
          {
            "precio_unitario": 7.01,
            "descripcion": "Zanahoria granel  50 Kg.",
            "precio_total_sin_impuestos": 4360.22,
            "unidad_medida": "Kilos",
            "impuestos": [
              {
                "codigo": "2",
                "codigo_porcentaje": "2",
                "base_imponible": 4359.54,
                "tarifa": 12,
                "valor": 523.14
              }
            ],
            "detalles_adicionales": {
              "Peso": "5000.0000"
            },
            "cantidad": 622,
            "codigo_auxiliar": "050",
            "descuento": 0,
            "codigo_principal": "ZNC"
          }
        ],
        "credito": {
          "monto": 34.21,
          "fecha_vencimiento": "2015-03-28"
        },
        "clave_acceso": "2802201501171389814400110010020000001482715926114"
      };
      return ebillService.post('', test).then(function successCallback(response) {
        console.log(response)
        return response;
      });
    };
    //
    // this.create = function (brand) {
    //   return securityService.post('/brand', brand).then(function successCallback(response) {
    //     return response.data;
    //   });
    // };

  });

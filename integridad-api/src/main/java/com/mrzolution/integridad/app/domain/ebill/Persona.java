package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

@Data
public class Persona {
    private  String razon_social;
    private  String identificacion;
    private  String tipo_identificacion;
    private  String email;
    private  String telefono;
    private  String direccion;
}

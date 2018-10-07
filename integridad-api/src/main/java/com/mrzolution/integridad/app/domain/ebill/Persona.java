package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Persona {
    private  String razon_social;
    private  String identificacion;
    private  String tipo_identificacion;
    private  String email;
    private  String telefono;
    private  String direccion;
}

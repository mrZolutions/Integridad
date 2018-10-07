package com.mrzolution.integridad.app.domain.eretention;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sujeto {
    private String email;
    private String identificacion;
    private String tipo_identificacion;
    private String razon_social;
    private String direccion;
    private String telefono;
}

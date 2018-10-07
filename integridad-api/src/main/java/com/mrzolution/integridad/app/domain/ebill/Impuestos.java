package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Impuestos {
    private String codigo;
    private String codigo_porcentaje;
    private float base_imponible;
    private float valor;
}

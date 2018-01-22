package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pago {
    private String medio;
    private float total;
    private PagoPropiedades propiedades;
}

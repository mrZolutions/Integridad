package com.mrzolution.integridad.app.domain.ecreditNote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.ebill.Impuestos;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Totales {
    private float total_sin_impuestos;
    private float importe_total;
    private List<Impuestos> impuestos;

}

package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TotalesExportacion {
    private float flete_internacional;
    private float seguro_internacional;
    private float gastos_aduaneros;
    private float otros_gastos_transporte;
}

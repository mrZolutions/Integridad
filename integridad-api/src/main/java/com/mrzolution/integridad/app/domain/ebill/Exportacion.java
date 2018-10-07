package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Exportacion {
    private Intercom incoterm;
    private OrigenExportacion origen;
    private DestinoExportacion destino;
    private String codigo_pais_adquisicion;
    private TotalesExportacion totales;
}

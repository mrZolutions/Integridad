package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

@Data
public class Exportacion {
    private Intercom incoterm;
    private OrigenExportacion origen;
    private DestinoExportacion destino;
    private String codigo_pais_adquisicion;
    private TotalesExportacion totales;
}

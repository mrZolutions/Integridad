package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Emisor {
    private String ruc;
    private boolean obligado_contabilidad;
    private String contribuyente_especial;
    private String nombre_comercial;
    private String razon_social;
    private String direccion;
    private boolean agent_retention;
    private String retention_data_long;
    private String retention_data;
    private boolean rimpe;
    private boolean microempresa;
    private Establecimiento establecimiento;
}

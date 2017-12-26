package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

@Data
public class Emisor {
    private String ruc;
    private boolean obligado_contabilidad;
    private String contribuyente_especial;
    private String nombre_comercial;
    private String razon_social;
    private String direccion;
    private Establecimiento establecimiento;
}

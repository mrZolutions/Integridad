package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Requirement {
    private int ambiente;
    private int tipo_emision;
    private String secuencial;
    private String fecha_emision;
    private Emisor emisor;
    private String moneda;
    private InformacionAdicional informacion_adicional;
    private Totales totales;
    private Persona comprador;
    private List<Item> items;
    private float valor_retenido_iva;
    private float valor_retenido_renta;
    private Credito credito;
    private List<Pago> pagos;
    private List<CompensacionSolidaria> compensaciones;
    private Exportacion exportacion;
    private String logo;
}

package com.mrzolution.integridad.app.domain.ecreditNote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.ebill.*;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ecreditNote {
    private int ambiente;
    private int tipo_emision;
    private String secuencial;
    private String fecha_emision;
    private Emisor emisor;
    private String moneda;
    private InformacionAdicional informacion_adicional;
    private Totales totales;
    private String fecha_emision_documento_modificado;
    private String numero_documento_modificado;
    private String tipo_documento_modificado;
    private String motivo;
    private List<Item> items;
    private Persona comprador;
}

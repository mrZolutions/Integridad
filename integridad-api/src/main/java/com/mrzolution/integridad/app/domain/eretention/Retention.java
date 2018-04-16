package com.mrzolution.integridad.app.domain.eretention;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.ebill.Emisor;
import com.mrzolution.integridad.app.domain.ebill.InformacionAdicional;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Retention {
    private int ambiente;
    private int tipo_emision;
    private String secuencial;
    private String fecha_emision;
    private Emisor emisor;
    private InformacionAdicional informacion_adicional;
    private List<Item> items;
    private Sujeto sujeto;
}

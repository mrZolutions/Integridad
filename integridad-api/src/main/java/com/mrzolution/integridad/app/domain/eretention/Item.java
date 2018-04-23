package com.mrzolution.integridad.app.domain.eretention;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.ebill.DetalleAdicional;
import com.mrzolution.integridad.app.domain.ebill.ImpuestosItem;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
    private float base_imponible;
    private String codigo;
    private String codigo_porcentaje;
    private String fecha_emision_documento_sustento;
    private String numero_documento_sustento;
    private float porcentaje;
    private String tipo_documento_sustento;
    private float valor_retenido;

}

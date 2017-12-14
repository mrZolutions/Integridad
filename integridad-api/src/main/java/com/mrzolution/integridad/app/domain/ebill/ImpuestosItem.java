package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

@Data
public class ImpuestosItem {
    private String codigo;
    private String codigo_porcentaje;
    private float base_imponible;
    private float valor;
    private float tarifa;
}

package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

import java.util.List;

@Data
public class Totales {
    private float total_sin_impuestos;
    private float descuento_adicional;
    private float descuento;
    private float propina;
    private float importe_total;
    private List<Impuestos> impuestos;

}

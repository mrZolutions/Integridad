package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

import java.util.List;
@Data
public class Item {
    private String descripcion;
    private String codigo_principal;
    private String codigo_auxiliar;
    private float cantidad;
    private float precio_unitario;
    private float descuento;
    private float precio_total_sin_impuestos;
    private String unidad_medida;
    private List<ImpuestosItem> impuestos;
    private DetalleAdicional detalles_adicionales;
}

package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Data
public class ExistencyReportV2 {
    private String code;
    private String name;
    private String tipo;
    private Double quantity;
    private String groupo;
    private String marca;
    private String linea;
    private String medida;
    private String iva;
    private Double pvp;
    private Double cashPercentage;
    private Double cashDiscount;
    private Double sinIva;
    private Double precio;

    public ExistencyReportV2(String code, String name, Double quantity, String groupo, String marca, String linea, String medida, String iva, Double pvp, Double cashPercentage, Double cashDiscount, Double sinIva, Double precio, String tipo) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.groupo = groupo;
        this.marca = marca;
        this.linea = linea;
        this.medida = medida;
        this.iva = iva;
        this.pvp = pvp;
        this.cashPercentage = cashPercentage;
        this.cashDiscount = cashDiscount;
        this.sinIva = sinIva;
        this.precio = precio;
        this.tipo = tipo;
    }
}
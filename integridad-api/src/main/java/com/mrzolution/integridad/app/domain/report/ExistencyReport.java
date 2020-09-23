package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Data
public class ExistencyReport {
    private String code;
    private String name;
    private String typeProduct;
    private Double costReal;
    private Double costCash;
    private Double costCard;
    private Double costCredit;
    private Double costMajor;
    private long maxMin;
    private Double quantity;
    private String groupo;
    private String subGroup;
    private String marca;
    private String linea;
    private String observacion;
    
    public ExistencyReport(String code, String name, String typeProduct, Double costReal, Double costCash, Double costCard, Double costCredit, Double costMajor, long maxMin, Double quantity, String groupo, String subGroup, String marca, String linea, String observacion) {
        this.code = code;
        this.name = name;
        this.typeProduct = typeProduct;
        this.costReal = costReal;
        this.costCash = costCash;
        this.costCard = costCard;
        this.costCredit = costCredit;
        this.costMajor = costMajor;
        this.maxMin = maxMin;
        this.quantity = quantity;
        this.groupo = groupo;
        this.subGroup = subGroup;
        this.marca = marca;
        this.linea = linea;
        this.observacion = observacion;
    }
}
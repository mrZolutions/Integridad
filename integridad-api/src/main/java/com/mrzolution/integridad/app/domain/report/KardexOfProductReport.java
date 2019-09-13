package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class KardexOfProductReport {
    private String fecha;
    private String prodName;
    private String detalle;
    private Double entrada;
    private Double salida;
    private Double saldo;
    private Double compra;
    private Double promedio;
    
    public KardexOfProductReport(String fecha, String prodName, String detalle, Double entrada, Double salida, Double saldo, Double compra, Double promedio) {
        this.fecha = fecha;
        this.prodName = prodName;
        this.detalle = detalle;
        this.entrada = entrada;
        this.salida = salida;
        this.saldo = saldo;
        this.compra = compra;
        this.promedio = promedio;
    }
}
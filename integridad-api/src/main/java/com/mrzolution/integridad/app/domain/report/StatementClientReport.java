package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class StatementClientReport {
    private String identification;
    private String clientName;
    private String fechVenta;
    private String fechVence;
    private String billNumber;
    private String retenNumber;
    private String notacNumber;
    private String detalle;
    private String observacion;
    private String modePayment;
    private String numCheque;
    private Double total;
    private Double valorAbono;
    private Double valorReten;
    private Double valorNotac;
    private Double saldo;
    
    public StatementClientReport(String identification, String clientName, String fechVenta, String fechVence, String billNumber, String retenNumber, String notacNumber, String detalle, String observacion, String modePayment, String numCheque, Double total, Double valorAbono, Double valorReten, Double valorNotac, Double saldo) {
        this.identification = identification;
        this.clientName = clientName;
        this.fechVenta =  fechVenta;
        this.fechVence =  fechVence;
        this.billNumber =  billNumber;
        this.retenNumber = retenNumber;
        this.notacNumber = notacNumber;
        this.detalle = detalle;
        this.observacion = observacion;
        this.modePayment = modePayment;
        this.numCheque = numCheque;
        this.total =  total;
        this.valorAbono =  valorAbono;
        this.valorReten =  valorReten;
        this.saldo =  saldo;
    }
}
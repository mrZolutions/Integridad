package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class StatementProviderReport {
    private String ruc;
    private String providerName;
    private String fechCompra;
    private String fechVence;
    private String fechPago;
    private String billNumber;
    private String debtsSeq;
    private String retenNumber;
    private String detalle;
    private String observacion;
    private String modePayment;
    private String numCheque;
    private Double total;
    private Double valorAbono;
    private Double valorReten;
    private Double saldo;
    
    public StatementProviderReport(String ruc, String providerName, String fechCompra, String fechVence, String fechPago, String billNumber, String debtsSeq, String retenNumber, String detalle, String observacion, String modePayment, String numCheque, Double total, Double valorAbono, Double valorReten, Double saldo) {
        this.ruc = ruc;
        this.providerName = providerName;
        this.fechCompra =  fechCompra;
        this.fechVence =  fechVence;
        this.fechPago = fechPago;
        this.billNumber =  billNumber;
        this.debtsSeq = debtsSeq;
        this.retenNumber = retenNumber;
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
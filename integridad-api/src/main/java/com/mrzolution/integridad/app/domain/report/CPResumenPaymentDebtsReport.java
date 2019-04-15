package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CPResumenPaymentDebtsReport {
    private String ruc;
    private String nameProv;
    private String debtsNumber;
    private String billNumber;
    private Double debtsTotal;
    private String tipTransac;
    private String formPago;
    private String numCheque;
    private String fechPago;
    private Double valorAbono;
    private Double valorReten;
    
    public CPResumenPaymentDebtsReport(String ruc, String nameProv, String debtsNumber, String billNumber, Double debtsTotal, String tipTransac, String formPago, String numCheque, String fechPago, Double valorAbono, Double valorReten) {
        this.ruc = ruc;
        this.nameProv = nameProv;
        this.debtsNumber = debtsNumber;
        this.billNumber = billNumber;
        this.debtsTotal = debtsTotal;
        this.tipTransac = tipTransac;
        this.formPago = formPago;
        this.numCheque = numCheque;
        this.fechPago = fechPago;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
    };
}

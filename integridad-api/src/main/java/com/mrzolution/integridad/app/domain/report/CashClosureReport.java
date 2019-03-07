package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CashClosureReport {
    private String billDateCreated;
    private String billStringSeq;
    private Double billBaseTaxes;
    private Double billBaseNoTaxes;
    private Double billSubTotal;
    private Double billIva;
    private Double billTotal;
    private String pagoMedio;
    private String pagoCardBrand;
    private String pagoNumeroLote;
    private String pagoChequeAccount;
    private String pagoChequeBank;
    private String pagoChequeNumber;
    
    public CashClosureReport(String billDateCreated, String billStringSeq, Double billBaseTaxes, Double billBaseNoTaxes, Double billSubTotal, Double billIva, Double billTotal, String pagoMedio, String pagoCardBrand, String pagoNumeroLote, String pagoChequeAccount, String pagoChequeBank, String pagoChequeNumber) {
        this.billDateCreated = billDateCreated;
        this.billStringSeq = billStringSeq;
        this.billBaseTaxes = billBaseTaxes;
        this.billBaseNoTaxes = billBaseNoTaxes;
        this.billSubTotal = billSubTotal;
        this.billIva = billIva;
        this.billTotal = billTotal;
        this.pagoMedio = pagoMedio;
        this.pagoCardBrand = pagoCardBrand;
        this.pagoNumeroLote = pagoNumeroLote;
        this.pagoChequeAccount = pagoChequeAccount;
        this.pagoChequeBank = pagoChequeBank;
        this.pagoChequeNumber = pagoChequeNumber;
    }
}

package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */
@Data
public class PaymentReport {
    private long datePayment;
    private String typePayment;
    private String modePayment;
    private Double valor;
    private String noAccount;
    private String noDocument;
    private String clientName;
    private String billstringSeq;
    
    public PaymentReport(long datePayment, String typePayment, String modePayment, Double valor, String noAccount, String noDocument, String clientName, String billstringSeq) {
        this.datePayment = datePayment;
        this.typePayment = typePayment;
        this.modePayment = modePayment;
        this.valor = valor;
        this.noAccount = noAccount;
        this.noDocument = noDocument;
        this.clientName = clientName;
        this.billstringSeq = billstringSeq;
    }
}

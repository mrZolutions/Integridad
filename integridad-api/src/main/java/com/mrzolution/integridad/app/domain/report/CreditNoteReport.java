package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CreditNoteReport {
    private String stringSeq;
    private String dateCreated;
    private String documentStringSeq;
    private String status;
    private Double baseDoce;
    private Double baseCero;
    private Double iva;
    private Double total;
    private String motivo;
    
    
    public CreditNoteReport(String stringSeq, String dateCreated, String documentStringSeq, String status, Double baseDoce, Double baseCero, Double iva, Double total, String motivo) {
        this.stringSeq = stringSeq;
        this.dateCreated = dateCreated;
        this.documentStringSeq = documentStringSeq;
        this.status = status;
        this.baseDoce = baseDoce;
        this.baseCero = baseCero;
        this.iva = iva;
        this.total = total;
        this.motivo = motivo;
    }
}
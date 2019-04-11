package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class RetentionClientReport {
    private String date;
    private String documentDate;
    private String clientCode;
    private String clientName;
    private String identification;
    private String retentionNumber;
    private String documentNumber;
    private String ejercicioFiscal;
    private String codigoRetentionFuente;
    private Double baseFuente;
    private Double porcenFuente;
    private Double subTotalFuente;
    private String codigoRetentionIva;
    private Double baseIva;
    private Double porcenIva;
    private Double subTotalIva;
    private Double total;
    
    public RetentionClientReport(String date, String documentDate, String clientCode, String clientName, String identification, String retentionNumber, String documentNumber, String ejercicioFiscal, String codigoRetentionFuente, Double baseFuente, Double porcenFuente, Double subTotalFuente, String codigoRetentionIva, Double baseIva, Double porcenIva, Double subTotalIva, Double total) {
        this.date = date;
        this.documentDate = documentDate;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.identification = identification;
        this.retentionNumber = retentionNumber;
        this.documentNumber = documentNumber;
        this.ejercicioFiscal = ejercicioFiscal;
        this.codigoRetentionFuente = codigoRetentionFuente;
        this.baseFuente = baseFuente;
        this.porcenFuente = porcenFuente;
        this.subTotalFuente = subTotalFuente;
        this.codigoRetentionIva = codigoRetentionIva;
        this.baseIva = baseIva;
        this.porcenIva = porcenIva;
        this.subTotalIva = subTotalIva;
        this.total = total;
    }
}

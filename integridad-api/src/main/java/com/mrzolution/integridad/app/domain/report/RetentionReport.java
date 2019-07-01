package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

@Data
public class RetentionReport {
    private String date;
    private String documentDate;
    private String providerCode;
    private String providerName;
    private String ruc;
    private String retentionNumber;
    private String authorizationNumber;
    private String documentNumber;
    private String ejercicioFiscal;
    private String debtsSeq;
    private String status;
    private String codigoRetentionFuente;
    private Double baseFuente;
    private Double porcenFuente;
    private Double subTotalFuente;
    private String codigoRetentionIva;
    private Double baseIva;
    private Double porcenIva;
    private Double subTotalIva;
    private Double total;
    private String subsidiary;
    private String userName;

    public RetentionReport(String date, String documentDate, String providerCode, String providerName, String ruc, String retentionNumber, String authorizationNumber, String documentNumber, String ejercicioFiscal, String debtsSeq, String status, String codigoRetentionFuente, Double baseFuente, Double porcenFuente, Double subTotalFuente, String codigoRetentionIva, Double baseIva, Double porcenIva, Double subTotalIva, Double total, String subsidiary, String userName) {
        this.date = date;
        this.documentDate = documentDate;
        this.providerCode = providerCode;
        this.providerName = providerName;
        this.ruc = ruc;
        this.retentionNumber = retentionNumber;
        this.authorizationNumber = authorizationNumber;
        this.documentNumber = documentNumber;
        this.ejercicioFiscal = ejercicioFiscal;
        this.debtsSeq = debtsSeq;
        this.status = status;
        this.codigoRetentionFuente = codigoRetentionFuente;
        this.baseFuente = baseFuente;
        this.porcenFuente = porcenFuente;
        this.subTotalFuente = subTotalFuente;
        this.codigoRetentionIva = codigoRetentionIva;
        this.baseIva = baseIva;
        this.porcenIva = porcenIva;
        this.subTotalIva = subTotalIva;
        this.total = total;
        this.subsidiary = subsidiary;
        this.userName = userName;
    }
}
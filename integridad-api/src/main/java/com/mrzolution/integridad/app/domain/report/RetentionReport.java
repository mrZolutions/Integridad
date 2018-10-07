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
    private Double total;
    private String subsidiary;
    private String userName;
    private String status;

    public RetentionReport(String date, String documentDate, String providerCode, String providerName, String ruc, String retentionNumber, String authorizationNumber, String documentNumber, String ejercicioFiscal, Double total, String subsidiary, String userName, String status) {
        this.date = date;
        this.documentDate = documentDate;
        this.providerCode = providerCode;
        this.providerName = providerName;
        this.ruc = ruc;
        this.retentionNumber = retentionNumber;
        this.authorizationNumber = authorizationNumber;
        this.documentNumber = documentNumber;
        this.ejercicioFiscal = ejercicioFiscal;
        this.total = total;
        this.subsidiary = subsidiary;
        this.userName = userName;
        this.status = status;
    }
}

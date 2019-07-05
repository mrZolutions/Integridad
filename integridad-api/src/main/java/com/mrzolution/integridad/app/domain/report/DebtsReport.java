package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class DebtsReport {
    private String date;
    private String providerCode;
    private String providerName;
    private String ruc;
    private String debtNumber;
    private String billNumber;
    private String authorizationNumber;
    private String purchaseType;
    private String status;
    private String observacion;
    private Double subTotalDoce;
    private Double iva;
    private Double subTotalCero;
    private Double total;
    private String endDate;
    private String cashier;
    private String warehouse;
    private String subsidiary;
    private String userName;
    
    public DebtsReport(String date, String providerCode, String providerName, String ruc, String debtNumber, String billNumber, String authorizationNumber, String purchaseType, String status, String observacion, Double subTotalDoce, Double iva, Double subTotalCero, Double total, String endDate, String cashier, String warehouse, String subsidiary, String userName) {
        this.date = date;
        this.providerCode = providerCode;
        this.providerName = providerName;
        this.ruc = ruc;
        this.debtNumber = debtNumber;
        this.billNumber = billNumber;
        this.authorizationNumber = authorizationNumber;
        this.purchaseType = purchaseType;
        this.status = status;
        this.observacion = observacion;
        this.subTotalDoce = subTotalDoce;
        this.iva = iva;
        this.subTotalCero = subTotalCero;
        this.total = total;
        this.endDate = endDate;
        this.cashier = cashier;
        this.warehouse = warehouse;
        this.subsidiary = subsidiary;
        this.userName = userName;
    }
}
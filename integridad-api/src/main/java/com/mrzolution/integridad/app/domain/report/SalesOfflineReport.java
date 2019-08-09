package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class SalesOfflineReport {
    private String date;
    private String clientCode;
    private String clientName;
    private String ruc;
    private String billNumber;
    private String status;
    private Double baseTaxes;
    private Double discount;
    private Double baseNoTaxes;
    private Double iva;
    private Double total;
    private String cashier;
    private String subsidiary;
    private String userName;

    public SalesOfflineReport(String date, String clientCode, String clientName, String ruc, String billNumber, String status, Double baseTaxes, Double discount, Double baseNoTaxes, Double iva, Double total, String cashier, String subsidiary, String userName) {
        this.date = date;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.ruc = ruc;
        this.billNumber = billNumber;
        this.status = status;
        this.baseTaxes = baseTaxes;
        this.discount = discount;
        this.baseNoTaxes = baseNoTaxes;
        this.iva = iva;
        this.total = total;
        this.cashier = cashier;
        this.subsidiary = subsidiary;
        this.userName = userName;
    }
}
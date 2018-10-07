package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

@Data
public class SalesReport {
    private String date;
    private String clientCode;
    private String clientName;
    private String ruc;
    private String billNumber;
    private String status;
    private String oti;
    private Double baseTaxes;
    private Double discount;
    private Double baseNoTaxes;
    private Double iva;
    private Double total;
    private String endDate;
    private String cashier;
    private String warehouse;
    private String subsidiary;
    private String userName;

    public SalesReport(String date, String clientCode, String clientName, String ruc, String billNumber, String status, String oti, Double baseTaxes, Double discount, Double baseNoTaxes, Double iva, Double total, String endDate, String cashier, String warehouse, String subsidiary, String userName) {
        this.date = date;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.ruc = ruc;
        this.billNumber = billNumber;
        this.status = status;
        this.oti = oti;
        this.baseTaxes = baseTaxes;
        this.discount = discount;
        this.baseNoTaxes = baseNoTaxes;
        this.iva = iva;
        this.total = total;
        this.endDate = endDate;
        this.cashier = cashier;
        this.warehouse = warehouse;
        this.subsidiary = subsidiary;
        this.userName = userName;
    }
}

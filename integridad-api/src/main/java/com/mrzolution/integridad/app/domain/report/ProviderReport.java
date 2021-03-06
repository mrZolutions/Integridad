package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Data
public class ProviderReport {
    private String ruc_type;
    private String ruc;
    private String name;
    private String razonSocial;
    private String address1;
    private String phone;
    private String celPhone;
    private String email;
    private String providerType;
    private String contact;
    
    public ProviderReport(String ruc_type, String ruc, String name, String razonSocial, String address1, String phone, String celPhone, String email, String providerType, String contact) {
        this.ruc_type = ruc_type;
        this.ruc = ruc;
        this.name = name;
        this.razonSocial = razonSocial;
        this.address1 = address1;
        this.phone = phone;
        this.celPhone = celPhone;
        this.email = email;
        this.providerType = providerType;
        this.contact = contact;
    }
}
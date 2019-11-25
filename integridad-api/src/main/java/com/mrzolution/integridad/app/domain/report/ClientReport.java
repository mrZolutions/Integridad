package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class ClientReport {
    private String typeId;
    private String identification;
    private String name;
    private String address;
    private String phone;
    private String celPhone;
    private String email;
    private String contact;
    
    public ClientReport(String typeId, String identification, String name, String address, String phone, String celPhone, String email, String contact) {
        this.typeId = typeId;
        this.identification = identification;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.celPhone = celPhone;
        this.email = email;
        this.contact = contact;
    }
}
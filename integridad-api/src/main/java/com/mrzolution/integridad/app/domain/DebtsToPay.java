package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class DebtsToPay {
    @Id
    @GeneratedValue
    private UUID id;

    private String buyTypeVoucher;
    private String purchaseType;
    private long date;
    private int period;
    private String establishmentNumber;
    private String cashierNumber;
    private String sequentialNumber;
    private String billNumber;
    private String authorizationNumber;
    private double total;
    private double subTotal;
    private String tipoDeCompra;

    private String description;
    private String account_type;
    private String name;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "cuenta_contable_id")
    private CuentaContable cuentaContable;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    public void setListsNull(){

    }

    public void setFatherListToNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
        provider.setListsNull();
        provider.setFatherListToNull();
        cuentaContable.setListsNull();
        cuentaContable.setFatherListToNull();
    }
}

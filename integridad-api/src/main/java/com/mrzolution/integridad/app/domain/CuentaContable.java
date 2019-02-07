package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class CuentaContable {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String type;
    private boolean active;
    private String accountType;
    private String description;
    private String name;
    private boolean ivaVenta;
    private boolean ivaCompra;
    private boolean ctaProvider;
    private boolean ctaClient;


    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    public void setListsNull(){
    }

    public void setFatherListToNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
    }

    public static CuentaContable newCuentaContableTest(){
        CuentaContable cuentaContable = new CuentaContable();
        cuentaContable.setUserClient(UserClient.newUserClientTest());
        return cuentaContable;
    }
}

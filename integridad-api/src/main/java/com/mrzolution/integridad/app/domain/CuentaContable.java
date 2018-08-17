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
    private String description;
    private String account_type;
    private String name;

    private boolean active;

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

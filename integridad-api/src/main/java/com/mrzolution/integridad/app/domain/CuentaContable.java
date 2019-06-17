package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
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

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    @OneToMany(mappedBy = "cuentaContable", cascade = CascadeType.ALL)
    private List<CuentaContableByProduct> cuentaContableByProducts;

    public void setListsNull(){
        if(cuentaContableByProducts != null){
            cuentaContableByProducts = null;
        }
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
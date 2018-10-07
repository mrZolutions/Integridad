package com.mrzolution.integridad.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Data;

/**
 * Created by daniel.
 */
@Entity
@Data
public class UserClient {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String threeCode;
    private String codeIntegridad;
    private String celPhone;
    private String phone;
    private String city;
    private String address1;
    private String address2;
    private String cedula;
    private String ruc;
    private long initialActivityDate;
    private String ePassword;
    private String logo;
    private String apiKey;
    private boolean espTemp;

    private boolean testMode;

    //TODO
    //razonsocial
    //contribuyente especial
    
    private long dateCreated;
    
    private boolean active;
    
    @OneToMany(mappedBy = "userClient", cascade = CascadeType.ALL)
    private List<Subsidiary> subsidiaries;

    @OneToMany(mappedBy = "userClient", cascade = CascadeType.ALL)
    private List<Provider> providers;

    @OneToMany(mappedBy = "userClient", cascade = CascadeType.ALL)
    private List<CuentaContable> cuentaContables;
    
    public void setListsNull(){
    	subsidiaries = null;
        providers = null;
        if(cuentaContables != null) cuentaContables = null;
    }
    
    public void setFatherListToNull(){
    }

    @Transient
    public static UserClient newUserClientTest(){
        UserClient userClient = new UserClient();
        userClient.setSubsidiaries(new ArrayList<>());
        userClient.setProviders(new ArrayList<>());
        userClient.setCuentaContables(new ArrayList<>());
        
        return userClient;
    }

}

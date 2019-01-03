package com.mrzolution.integridad.app.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Provider {

    @Id
    @GeneratedValue
    private UUID id;

    private String codeIntegridad;
    private String name;
    private String razonSocial;
    private String celPhone;
    private String phone;
    private String country;
    private String city;
    private String address1;
    private String ruc_type;
    private String ruc;
    private String contact;
    private String providerType;
    private long dateCreated;
    private boolean active;
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;
    
//    @OneToMany(mappedBy = "userClient", cascade = CascadeType.ALL)
//    private List<Subsidiary> subsidiaries;
    
    public void setListsNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
    }
    
    public void setFatherListToNull(){
    }

    @Transient
    public static Provider newProviderTest(){
        Provider provider = new Provider();
        provider.setUserClient(UserClient.newUserClientTest());
        
        return provider;
    }

}

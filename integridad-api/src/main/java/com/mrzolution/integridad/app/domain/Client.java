package com.mrzolution.integridad.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import lombok.Data;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Client {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String celPhone;
    private String phone;
    private String city;
    private String country;
    private String address;
    private String typeId;
    private String identification;
    private String contact;
    private String codConta;
    private String codApp;
    private long entryDate;
    
    private long dateCreated;
    private boolean active;

    //TODO
    //razon social
    
    @Email
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Bill> bills;
    
    public void setListsNull(){
    	bills = null;
    }
    
    public void setFatherListToNull(){
    	if(userClient != null){    		
    		userClient.setListsNull();
    		userClient.setFatherListToNull();
    	}
    }

    @Transient
    public static Client newClientTest(){
        Client client = new Client();
        client.setUserClient(UserClient.newUserClientTest());
        client.setBills(new ArrayList<>());

        return client;
    }

}
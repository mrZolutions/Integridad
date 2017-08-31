package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.mrzolution.integridad.app.interfaces.Child;

import lombok.Data;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Subsidiary implements Child{

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String threeCode;
    private String celPhone;
    private String phone;
    private String city;
    private String address1;
    private String address2;
    
    private long dateCreated;
    
    private boolean active;
    
    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;
    
    
    public void setListsNull(){
    	
    }
    
    public void setFatherListToNull(){
    	userClient.setListsNull();
    }

    @Transient
    public static Subsidiary newSubsidiaryTest(){
        Subsidiary subsidiary = new Subsidiary();
        subsidiary.setUserClient(UserClient.newUserClientTest());
        
        return subsidiary;
    }

}

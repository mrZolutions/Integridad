package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private long entryDate;
    
    private long dateCreated;
    private boolean active;
    
    @Email
    private String email;
    
    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    }

    @Transient
    public static Client newClientTest(){
        Client client = new Client();

        return client;
    }

}

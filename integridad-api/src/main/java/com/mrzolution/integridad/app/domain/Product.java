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
public class Product implements Child{

    @Id
    @GeneratedValue
    private UUID id;
    
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

    @Transient
    public static Product newProducTest(){
        Product product = new Product();
        product.setUserClient(UserClient.newUserClientTest());

        return product;
    }

}

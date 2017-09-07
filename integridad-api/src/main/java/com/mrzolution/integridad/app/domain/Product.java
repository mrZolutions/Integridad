package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    
    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    }

    @Transient
    public static Product newProducTest(){
        Product product = new Product();

        return product;
    }

}

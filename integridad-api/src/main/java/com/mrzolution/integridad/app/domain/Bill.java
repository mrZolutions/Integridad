package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Bill {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    
    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    	client.setListsNull();
    }

    @Transient
    public static Bill newBillTest(){
        Bill bill = new Bill();
        bill.setClient(Client.newClientTest());

        return bill;
    }

}

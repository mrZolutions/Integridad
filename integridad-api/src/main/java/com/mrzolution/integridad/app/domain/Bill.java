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
    
    private long dateCreated;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Detail> details;
    
    
    public void setListsNull(){
    	details = null;
    }
    
    public void setFatherListToNull(){
    	client.setListsNull();
    	client.setFatherListToNull();
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    }

    @Transient
    public static Bill newBillTest(){
        Bill bill = new Bill();
        bill.setDetails(new ArrayList<>());
        bill.setClient(Client.newClientTest());
        bill.setUserIntegridad(UserIntegridad.newUserIntegridadTest());

        return bill;
    }

}

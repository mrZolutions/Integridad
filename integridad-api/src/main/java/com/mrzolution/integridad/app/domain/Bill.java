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
    private String billSeq;

    private double iva;
    private double ice;
    private double total;
    private double subTotal;

    private String ordenDecompra;
    private String srr;
    private String lugar;
    private String otir;

    //TODO
    //propina
    //descuento
    //valor retenido iva
    //valor retenido renta
    //pagos

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Detail> details;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Pago> pagos;
    
    
    public void setListsNull(){
    	details = null;
    	pagos = null;
    }
    
    public void setFatherListToNull(){
    	client.setListsNull();
    	client.setFatherListToNull();
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }

    @Transient
    public static Bill newBillTest(){
        Bill bill = new Bill();
        bill.setDetails(new ArrayList<>());
        bill.setPagos(new ArrayList<>());
        bill.setClient(Client.newClientTest());
        bill.setUserIntegridad(UserIntegridad.newUserIntegridadTest());
        bill.setSubsidiary(Subsidiary.newSubsidiaryTest());

        return bill;
    }

}

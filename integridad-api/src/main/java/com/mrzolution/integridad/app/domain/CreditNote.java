package com.mrzolution.integridad.app.domain;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by daniel.
 */

@Entity
@Data
public class CreditNote {
    @Id
    @GeneratedValue
    private UUID id;
    
    private long dateCreated;
    private boolean active;
    private String creditSeq;
    private String billSeq;
    private String stringSeq;
    private String documentStringSeq;
    private String priceType;

    private double iva;
    private double ice;
    private double total;
    private double subTotal;
    private double baseNoTaxes;
    private double baseTaxes;

    private String ordenDecompra;
    private String srr;
    private String lugar;
    private String otir;
    private double discount;
    private int discountPercentage;
    private String motivo;

    private String claveDeAcceso;
    private String idSri;


    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL)
    private List<Detail> details;
    
    public void setListsNull(){
    	details = null;
    };
    
    public void setFatherListToNull(){
    	client.setListsNull();
    	client.setFatherListToNull();
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    };

    @Transient
    public static CreditNote newCreditNoteTest(){
        CreditNote bill = new CreditNote();
        bill.setDetails(new ArrayList<>());
        bill.setClient(Client.newClientTest());
        bill.setUserIntegridad(UserIntegridad.newUserIntegridadTest());
        bill.setSubsidiary(Subsidiary.newSubsidiaryTest());

        return bill;
    };
    
    @Transient
    public Payment payment;
    
    @Transient
    public Credits credits;
}
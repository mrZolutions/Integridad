package com.mrzolution.integridad.app.domain;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Entity
@Data
public class BillOffline {
    @Id
    @GeneratedValue
    private UUID id;
    
    private long dateCreated;
    private boolean active;
    private String billSeq;
    private String stringSeq;
    private String priceType;

    private double iva;
    private double ice;
    private double total;
    private double subTotal;
    private double baseNoTaxes;
    private double baseTaxes;
    private double discount;
    private int discountPercentage;
    private String saldo;
    private int typeDocument;
    private String observation;
    private String claveDeAcceso;
    private String idSri;
    
    private String creditNoteId;
    private String creditNoteNumber;
    private boolean creditNoteApplied;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "billOffline", cascade = CascadeType.ALL)
    private List<DetailOffline> detailsOffline;

    @OneToMany(mappedBy = "billOffline", cascade = CascadeType.ALL)
    private List<PagoOffline> pagosOffline;
    
    public void setListsNull(){
    	detailsOffline = null;
    	pagosOffline = null;
    }
    
    public void setFatherListToNull(){
    	client.setListsNull();
    	client.setFatherListToNull();
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }
}
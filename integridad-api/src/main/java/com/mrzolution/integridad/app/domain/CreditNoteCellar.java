package com.mrzolution.integridad.app.domain;

import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Entity
@Data
public class CreditNoteCellar {
    @Id
    @GeneratedValue
    private UUID id;
    
    private long dateCreated;
    private boolean active;
    private String creditSeq;
    private String cellarSeq;
    private String stringSeq;
    private String documentStringSeq;

    private double iva;
    private double total;
    private double subTotal;
    private double baseNoTaxes;
    private double baseTaxes;
    private String motivo;
    private String notaCredito;
    private String autorizacion;


    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "creditNoteCellar", cascade = CascadeType.ALL)
    private List<DetailCellar> detailsCellar;
    
    public void setListsNull(){
    	detailsCellar = null;
    };
    
    public void setFatherListToNull(){
    	provider.setListsNull();
    	provider.setFatherListToNull();
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    };
    
    @Transient
    public PaymentDebts paymentDebts;
    
    @Transient
    public CreditsDebts creditsDebts;
}
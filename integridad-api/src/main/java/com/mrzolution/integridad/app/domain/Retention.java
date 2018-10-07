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
public class Retention {

    @Id
    @GeneratedValue
    private UUID id;
    
    private long dateCreated;
    private boolean active;
    private String retSeq;
    private String stringSeq;

    private String ejercicioFiscal;
    private String documentNumber;
    private long documentDate;

    private String claveDeAcceso;
    private String idSri;


    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "retention", cascade = CascadeType.ALL)
    private List<DetailRetention> detailRetentions;

    public void setListsNull(){
    	detailRetentions = null;
    }
    
    public void setFatherListToNull(){
        provider.setListsNull();
        provider.setFatherListToNull();
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }

    @Transient
    public static Retention newRetentionTest(){
        Retention retention = new Retention();
        retention.setDetailRetentions(new ArrayList<>());
        retention.setProvider(Provider.newProviderTest());
        retention.setUserIntegridad(UserIntegridad.newUserIntegridadTest());
        retention.setSubsidiary(Subsidiary.newSubsidiaryTest());

        return retention;
    }

}

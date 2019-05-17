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
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class Cellar {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String billNumber;
    private long dateBill;
    private long dateEnterCellar;
    private String authNumber;
    private String statusIngreso;
    private String cellarSeq;
    private String whNumberSeq;
    private boolean active;
    private double iva;
    private double ice;
    private double total;
    private double subTotal;
    private double baseNoTaxes;
    private double baseTaxes;
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @OneToMany(mappedBy = "cellar", cascade = CascadeType.ALL)
    private List<DetailCellar> detailsCellar;
    
    @OneToMany(mappedBy = "cellar", cascade = CascadeType.ALL)
    private List<Kardex> detailsKardex;
    
    public void setListsNull() {
        detailsCellar = null;
        detailsKardex = null;
    }
    
    public void setFatherListToNull() {
        warehouse.setListsNull();
    	warehouse.setFatherListToNull();
    	provider.setListsNull();
    	provider.setFatherListToNull();
        subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
        userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    }
}
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
    private String whNumberSeq;
    private boolean active;
    
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
    private List<DetailCellar> detailCellar;
    
    public void setListsNull() {
        detailCellar = null;
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

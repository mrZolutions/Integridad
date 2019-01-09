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
public class Consumption {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String codeWarehouse;
    private long dateConsumption;
    private String csmSeq;
    private String csmNumberSeq;
    private String details;
    private String observation;
    private String nameClient;
    private String nameSupervisor;
    private boolean active;
    private double iva;
    private double ice;
    private double total;
    private double subTotal;
    private double baseNoTaxes;
    private double baseTaxes;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @OneToMany(mappedBy = "consumption", cascade = CascadeType.ALL)
    private List<DetailConsumption> detailsConsumption;
    
    @OneToMany(mappedBy = "consumption", cascade = CascadeType.ALL)
    private List<Kardex> detailsKardex;
    
    public void setListsNull() {
        detailsConsumption = null;
        detailsKardex = null;
    }
    
    public void setFatherListToNull() {
        client.setListsNull();
    	client.setFatherListToNull();
        warehouse.setListsNull();
    	warehouse.setFatherListToNull();
        subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
        userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    }
}

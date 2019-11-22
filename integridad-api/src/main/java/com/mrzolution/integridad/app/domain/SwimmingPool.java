package com.mrzolution.integridad.app.domain;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Entity
@Data
public class SwimmingPool {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long fecha;
    private String barCode;
    private String stringSeq;
    private String tipoClnt;
    private String codTipoClnt;
    private String status;
    private double subTotal;
    private double iva;
    private double total;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    public void setFatherListToNull(){
    	userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }
}
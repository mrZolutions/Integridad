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
    
    private long fecha;
    private String barCode;
    private String stringSeq;
    private String clntName;
    private String clntIdent;
    private boolean active;
    private String status;
    private double subTotal;
    private double iva;
    private double total;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
}
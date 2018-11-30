package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */
@Entity
@Data
public class CreditsDebts implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private int diasPlazo;
    private long fecha;
    private int payNumber;
    private double valor;
    private String documentNumber;
    private String billId;
    
    @ManyToOne
    @JoinColumn(name = "pagodebts_id")
    private PagoDebts pagodebts;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        pagodebts.setListsNull();
        pagodebts.setFatherListToNull();
    }
}

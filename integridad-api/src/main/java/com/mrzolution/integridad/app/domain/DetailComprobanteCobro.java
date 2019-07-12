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
 * @author daniel-one
 */

@Entity
@Data
public class DetailComprobanteCobro implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String numCheque;
    private String cuenta;
    private String banco;
    private String tipoAbono;
    private double totalAbono;
    private String billNumber;
    private long dateBill;
    
    @ManyToOne
    @JoinColumn(name = "comprobanteCobro_id")
    private ComprobanteCobro comprobanteCobro;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        if (comprobanteCobro != null) {
            comprobanteCobro.setListsNull();
            comprobanteCobro.setFatherListToNull();
        }
    }
}
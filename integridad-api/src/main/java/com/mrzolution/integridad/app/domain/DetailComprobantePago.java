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
public class DetailComprobantePago implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String codeConta;
    private String descrip;
    private String name;
    private String tipo;
    private double baseImponible;
    private String deber;
    private String haber;
    
    @ManyToOne
    @JoinColumn(name = "comprobantePago_id")
    private ComprobantePago comprobantePago;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        if (comprobantePago != null) {
            comprobantePago.setListsNull();
            comprobantePago.setFatherListToNull();
        }
    }
}
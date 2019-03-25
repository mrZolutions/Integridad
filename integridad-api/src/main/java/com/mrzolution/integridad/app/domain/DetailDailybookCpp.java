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
public class DetailDailybookCpp implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String codeConta;
    private String descrip;
    private String name;
    private String tipo;
    private double baseImponible;
    
    @ManyToOne
    @JoinColumn(name = "dailybookCpp_id")
    private DailybookCpp dailybookCpp;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        dailybookCpp.setListsNull();
        dailybookCpp.setFatherListToNull();
    }
}

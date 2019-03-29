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
public class DetailDailybookCg implements Child {
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
    @JoinColumn(name = "dailybookCg_id")
    private DailybookCg dailybookCg;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        dailybookCg.setListsNull();
        dailybookCg.setFatherListToNull();
    }
}

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
public class DetailDailybookCe implements Child {
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
    @JoinColumn(name = "dailybookCe_id")
    private DailybookCe dailybookCe;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        dailybookCe.setListsNull();
        dailybookCe.setFatherListToNull();
    }
}
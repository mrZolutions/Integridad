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
public class DetailDailybookContab implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String typeContab;
    private String numCheque;
    private String codeConta;
    private String descrip;
    private String name;
    private String tipo;
    private double baseImponible;
    private String deber;
    private String haber;
    private long dateDetailDailybook;
    private String dailybookNumber;
    private String userClientId;
    
    @ManyToOne
    @JoinColumn(name = "dailybookCg_id")
    private DailybookCg dailybookCg;
    
    @ManyToOne
    @JoinColumn(name = "dailybookCe_id")
    private DailybookCe dailybookCe;
    
    @ManyToOne
    @JoinColumn(name = "dailybookCi_id")
    private DailybookCi dailybookCi;
    
    @ManyToOne
    @JoinColumn(name = "dailybookCxP_id")
    private DailybookCxP dailybookCxP;
    
    @ManyToOne
    @JoinColumn(name = "dailybookFv_id")
    private DailybookFv dailybookFv;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        if (dailybookCg != null) {
            dailybookCg.setListsNull();
            dailybookCg.setFatherListToNull();
        }
        if (dailybookCe != null) {
            dailybookCe.setListsNull();
            dailybookCe.setFatherListToNull();
        }
        if (dailybookCi != null) {
            dailybookCi.setListsNull();
            dailybookCi.setFatherListToNull();
        }
        if (dailybookCxP != null) {
            dailybookCxP.setListsNull();
            dailybookCxP.setFatherListToNull();
        }
        if (dailybookFv != null) {
            dailybookFv.setListsNull();
            dailybookFv.setFatherListToNull();
        }
    }
}
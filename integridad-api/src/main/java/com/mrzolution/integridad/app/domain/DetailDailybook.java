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
public class DetailDailybook implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String codeConta;
    private String name;
    private String tipo;
    private double valorConta;
    
    @ManyToOne
    @JoinColumn(name = "dailybook_id")
    private Dailybook dailybook;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        dailybook.setListsNull();
        dailybook.setFatherListToNull();
    }
}

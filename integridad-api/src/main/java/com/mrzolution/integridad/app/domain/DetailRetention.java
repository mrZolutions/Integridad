package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by daniel.
 */

@Entity
@Data
public class DetailRetention implements Child {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "retention_id")
    private Retention retention;

    private String taxType;
    private String code;
    private double baseImponible;
    private double percentage;
    private double total;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
    	retention.setListsNull();
        retention.setFatherListToNull();
    }

    @Transient
    public static DetailRetention newDetailRetentionTest() {
        DetailRetention detail = new DetailRetention();
        detail.setRetention(Retention.newRetentionTest());
        return detail;
    }

}
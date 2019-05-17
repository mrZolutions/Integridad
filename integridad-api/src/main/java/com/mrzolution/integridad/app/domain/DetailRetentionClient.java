package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class DetailRetentionClient implements Child {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "retentionClient_id")
    private RetentionClient retentionClient;

    private String taxType;
    private String code;
    private double baseImponible;
    private double percentage;
    private double total;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
    	retentionClient.setListsNull();
        retentionClient.setFatherListToNull();
    }

    @Transient
    public static DetailRetentionClient newDetailRetentionClientTest() {
        DetailRetentionClient detail = new DetailRetentionClient();
        detail.setRetentionClient(RetentionClient.newRetentionClientTest());
        return detail;
    }
}
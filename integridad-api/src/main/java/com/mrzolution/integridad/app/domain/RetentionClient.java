package com.mrzolution.integridad.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class RetentionClient {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String retentionNumber;
    private String ejercicioFiscal;
    private String documentNumber;
    private long dateToday;
    private long documentDate;
    
    @OneToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;
    
    @OneToMany(mappedBy = "retentionClient", cascade = CascadeType.ALL)
    private List<DetailRetentionClient> detailRetentionClient;
    
    public void setListsNull(){
        detailRetentionClient = null;
    }
    
    public void setFatherListToNull(){
        bill.setListsNull();
        bill.setFatherListToNull();
    }
    
    @Transient
    public static RetentionClient newRetentionClientTest(){
        RetentionClient retentionClient = new RetentionClient();
        retentionClient.setDetailRetentionClient(new ArrayList<>());
        retentionClient.setBill(Bill.newBillTest());
        return retentionClient;
    }
}

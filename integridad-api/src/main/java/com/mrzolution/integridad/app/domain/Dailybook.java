package com.mrzolution.integridad.app.domain;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class Dailybook {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long dateRecordBook;
    private String generalDetail;
    private double valor;
    
    @ManyToOne
    @JoinColumn(name = "debtsToPay_id")
    private DebtsToPay debtsToPay;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "dailybook", cascade = CascadeType.ALL)
    private List<DetailDailybook> detailDailybook;
    
    public void setListsNull(){
        detailDailybook = null;
    }
    
    public void setFatherListToNull(){
        debtsToPay.setListsNull();
        debtsToPay.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }
}

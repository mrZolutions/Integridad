package com.mrzolution.integridad.app.domain;

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
public class dailybook {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long fecha;
    private String dailyNumberSeq;
    private String dailySeq;
    private String dailyNumber;
    private String dailyGeneralDetail;
    private double total;
    
    @ManyToOne
    @JoinColumn(name = "debtsToPay_id")
    private DebtsToPay debtsToPay;
    
    
}

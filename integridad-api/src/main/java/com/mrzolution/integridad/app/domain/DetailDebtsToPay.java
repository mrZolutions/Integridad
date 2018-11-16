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
public class DetailDebtsToPay implements Child{
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "debtsToPay_id")
    private DebtsToPay debtsToPay;
    
    private String taxType;
    private String codeConta;
    private String name;
    private double baseImponible;
    
    public void setListsNull(){
    };
    
    public void setFatherListToNull(){
        debtsToPay.setListsNull();
        debtsToPay.setFatherListToNull();
    };
}



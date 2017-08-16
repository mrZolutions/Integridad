package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.mrzolution.integridad.app.interfaces.Child;

import lombok.Data;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Detail implements Child{

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;
    
    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    	bill.setListsNull();
    	bill.setFatherListToNull();
    }

    @Transient
    public static Detail newDetailTest(){
        Detail detail = new Detail();
        detail.setBill(Bill.newBillTest());

        return detail;
    }

}

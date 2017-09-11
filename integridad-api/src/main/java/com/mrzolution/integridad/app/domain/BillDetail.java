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
public class BillDetail implements Child{

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;
    
    @ManyToOne
    @JoinColumn(name = "detail_id")
    private Detail detail;
    
    private long quantity;
    
    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    	bill.setListsNull();
    	bill.setFatherListToNull();
    	
    	detail.setListsNull();
    	detail.setFatherListToNull();
    }

    @Transient
    public static BillDetail newBillDetailTest(){
        BillDetail billDetail = new BillDetail();
        billDetail.setBill(Bill.newBillTest());
        billDetail.setDetail(Detail.newDetailTest());

        return billDetail;
    }

}

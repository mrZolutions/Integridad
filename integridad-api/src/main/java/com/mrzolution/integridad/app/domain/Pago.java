package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Pago {

    @Id
    @GeneratedValue
    private UUID id;

    private String medio;
    private double total;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    Bill bill;

    public void setListsNull(){

    }

    public void setFatherListToNull(){
        bill.setListsNull();
        bill.setFatherListToNull();
    }

    public static Pago newPagoTest(){
        Pago pago = new Pago();
        pago.setBill(Bill.newBillTest());

        return pago;
    }

}

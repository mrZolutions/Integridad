package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Pago {

    @Id
    @GeneratedValue
    private UUID id;

    private String medio;
    private double total;
    private String payForm;
    private String cardBrand;
    private String chequeAccount;
    private String chequeBank;
    private String chequeNumber;
    private String chequeDiasPlazo;
    private long fechaCobro;
    private int creditoIntervalos;
    private int creditoNumeroPagos;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL)
    private List<Credits> credits;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    public void setListsNull(){
        credits = null;
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
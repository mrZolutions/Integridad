package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Credits {

    @Id
    @GeneratedValue
    private UUID id;

    private int diasPlazo;
    private long fecha;
    private int payNumber;
    private double valor;

    @ManyToOne
    @JoinColumn(name = "pago_id")
    private Pago pago;

    public void setListsNull(){

    }

    public void setFatherListToNull(){
        pago.setListsNull();
        pago.setFatherListToNull();
    }

    public static Credits newCreditsTest(){
        Credits credit = new Credits();
        credit.setPago(Pago.newPagoTest());

        return credit;
    }

}

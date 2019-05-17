package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class Credits implements Child {
    @Id
    @GeneratedValue
    private UUID id;

    private int diasPlazo;
    private long fecha;
    private int payNumber;
    private double valor;
    private String documentNumber;
    private String statusCredits;
    private String billId;

    @ManyToOne
    @JoinColumn(name = "pago_id")
    private Pago pago;

    @OneToMany(mappedBy = "credits", cascade = CascadeType.ALL)
    private List<Payment> payments;

    public void setListsNull() {
        payments = null;
    }

    public void setFatherListToNull() {
        pago.setListsNull();
        pago.setFatherListToNull();
    }

    public static Credits newCreditsTest() {
        Credits credit = new Credits();
        credit.setPago(Pago.newPagoTest());
        return credit;
    }
}
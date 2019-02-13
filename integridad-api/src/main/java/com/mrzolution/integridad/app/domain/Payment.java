package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;
import javax.persistence.*;
import java.util.UUID;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class Payment implements Child {
    @Id
    @GeneratedValue
    private UUID id;

    private String typePayment;
    private String modePayment;
    private String detail;
    private long datePayment;
    private String noAccount;
    private String noDocument;
    private String documentNumber;
    private String banco;
    private String cardBrand;
    private String numeroLote;
    private double valorAbono;
    private double valorReten;
    private double valorNotac;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credits credits;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        credits.setListsNull();
        credits.setFatherListToNull();
    }
}

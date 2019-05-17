package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class PaymentDebts implements Child {
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
    private String ctaCtableBanco;
    private String cardBrand;
    private String numeroLote;
    private double valorAbono;
    private double valorReten;
    
    @ManyToOne
    @JoinColumn(name = "credit_id")
    private CreditsDebts creditsDebts;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        creditsDebts.setListsNull();
        creditsDebts.setFatherListToNull();
    }
}
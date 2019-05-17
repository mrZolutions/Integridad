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
public class CreditsDebts implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private int diasPlazo;
    private long fecha;
    private int payNumber;
    private double valor;
    private String documentNumber;
    private String estadoCredits;
    private String debtsToPayId;
    
    @ManyToOne
    @JoinColumn(name = "pagoDebts_id")
    private PagoDebts pagoDebts;
    
    @OneToMany(mappedBy = "creditsDebts", cascade = CascadeType.ALL)
    private List<PaymentDebts> paymentDebts;
    
    public void setListsNull() {
        paymentDebts = null;
    }
    
    public void setFatherListToNull() {
        pagoDebts.setListsNull();
        pagoDebts.setFatherListToNull();
    }
}
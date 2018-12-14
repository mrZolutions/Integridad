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
public class PagoDebts implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String medio;
    private double total;
    private String payForm;
    private String country;
    private String cardBrand;
    private String chequeAccount;
    private String chequeBank;
    private String chequeNumber;
    private String chequeDiasPlazo;
    private long fechaCobro;
    private int creditoIntervalos;
    private int creditoNumeroPagos;
    
    @OneToMany(mappedBy = "pagoDebts", cascade = CascadeType.ALL)
    private List<CreditsDebts> creditsDebts;
    
    @ManyToOne
    @JoinColumn(name = "debtsToPay_id")
    private DebtsToPay debtsToPay;
    
    public void setListsNull() {
        creditsDebts = null;
    }
    
    public void setFatherListToNull() {
        debtsToPay.setListsNull();
        debtsToPay.setFatherListToNull();
    }
}

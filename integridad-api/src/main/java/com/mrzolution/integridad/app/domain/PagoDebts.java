package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;

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
    private String payForm;
    private String cardBrand;
    private String chequeAccount;
    private String chequeBank;
    private String chequeNumber;
    private String chequeDiasPlazo;
    private long fechaCobro;
    private int creditoIntervalos;
    private int creditoNumeroPagos;
    private double total;
    
    @OneToMany(mappedBy = "pagodebts", cascade = CascadeType.ALL)
    private List<CreditsDebts> creditsdebts;
    
    @ManyToOne
    @JoinColumn(name = "debtstopay_id")
    private DebtsToPay debtstopay;
    
    public void setListsNull() {
        creditsdebts = null;
    }
    
    public void setFatherListToNull() {
        debtstopay.setListsNull();
        debtstopay.setFatherListToNull();
    }
}

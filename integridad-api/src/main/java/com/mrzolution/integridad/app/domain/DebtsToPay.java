package com.mrzolution.integridad.app.domain;

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
public class DebtsToPay {
    @Id
    @GeneratedValue
    private UUID id;

    private boolean active;
    private String buyTypeVoucher;
    private String purchaseType;
    private long fecha;
    private String ejercicio;
    private String threeNumberOne;
    private String threeNumberTwo;
    private String seccondPartNumber;
    private String billNumber;
    private String authorizationNumber;
    private double subTotalDoce;
    private double subTotalCero;
    private double iva;
    private double total;
    private double saldo;
    private String tipoDeCompra;
    private String debtsSeq;
    private String retentionId;
    private String retentionNumber;
    private long retentionDateCreated;
    private double retentionTotal;
    private String observacion;
    private String estado;
    
    private String credNoteId;
    private String credNoteNumber;
    private boolean credNoteApplied;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "debtsToPay", cascade = CascadeType.ALL)
    private List<DetailDebtsToPay> detailDebtsToPay;
    
    @OneToMany(mappedBy = "debtsToPay", cascade = CascadeType.ALL)
    private List<PagoDebts> pagos;

    public void setListsNull(){
        detailDebtsToPay = null;
        pagos = null;
    }

    public void setFatherListToNull(){
        provider.setListsNull();
        provider.setFatherListToNull();
        userIntegridad.setListsNull();
    	userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }
}
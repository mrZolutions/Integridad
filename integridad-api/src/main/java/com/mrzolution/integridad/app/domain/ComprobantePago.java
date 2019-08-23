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
 * @author daniel-one
 */

@Entity
@Data
public class ComprobantePago {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long dateComprobante;
    private String providerName;
    private String providerRuc;
    private String comprobanteSeq;
    private String comprobanteStringSeq;
    private String comprobanteConcep;
    private String comprobanteEstado;
    private String debtsNumber;
    private String billNumber;
    private String paymentDebtId;
    private double subTotalDoce;
    private double iva;
    private double total;
    
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "comprobantePago", cascade = CascadeType.ALL)
    private List<DetailComprobantePago> detailComprobantePago;
    
    public void setListsNull() {
        detailComprobantePago = null;
    }
    
    public void setFatherListToNull() {
        if (provider != null) {
            provider.setListsNull();
            provider.setFatherListToNull();
        }
        if (userIntegridad != null) {
            userIntegridad.setListsNull();
            userIntegridad.setFatherListToNull();
        }
        if (subsidiary != null) {
            subsidiary.setListsNull();
            subsidiary.setFatherListToNull();
        }
    }
}
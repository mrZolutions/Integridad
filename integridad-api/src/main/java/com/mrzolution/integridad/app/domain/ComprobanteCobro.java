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
public class ComprobanteCobro {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long dateComprobante;
    private String clientName;
    private String clientRuc;
    private String comprobanteSeq;
    private String comprobanteStringSeq;
    private String comprobanteConcep;
    private String comprobanteEstado;
    private String paymentId;
    private double subTotalDoce;
    private double iva;
    private double total;
        
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "comprobanteCobro", cascade = CascadeType.ALL)
    private List<DetailComprobanteCobro> detailComprobanteCobro;
    
    public void setListsNull() {
        detailComprobanteCobro = null;
    }
    
    public void setFatherListToNull() {
        if (client != null) {
            client.setListsNull();
            client.setFatherListToNull();
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
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
public class DailybookCpp {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long dateRecordBook;
    private String billNumber;
    private String clientProvName;
    private String codeTypeContab;
    private String typeContab;
    private String generalDetail;
    private String dailyCppSeq;
    private String dailyCppStringSeq; //Generado automáticamente por el Sistema
    private String dailyCppStringUserSeq; //Introducido por el Usuario
    private double subTotalDoce;
    private double iva;
    private double subTotalCero;
    private double total;
    private String retentionId;
    private String retentionNumber;
    private long retentionDateCreated;
    private double retentionTotal;
    
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "dailybookCpp", cascade = CascadeType.ALL)
    private List<DetailDailybookCpp> detailDailybookCpp;
    
    public void setListsNull() {
        detailDailybookCpp = null;
    }
    
    public void setFatherListToNull() {
        provider.setListsNull();
        provider.setFatherListToNull();
        userIntegridad.setListsNull();
        userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }
}

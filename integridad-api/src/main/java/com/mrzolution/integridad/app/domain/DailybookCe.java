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
public class DailybookCe {
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
    private String dailyCeSeq;
    private String dailyCeStringSeq; //Generado automáticamente por el Sistema
    private String dailyCeStringUserSeq; //Introducido por el Usuario
    private double subTotalDoce;
    private double iva;
    private double subTotalCero;
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
    
    @OneToMany(mappedBy = "dailybookCe", cascade = CascadeType.ALL)
    private List<DetailDailybookCe> detailDailybookCe;
    
    public void setListsNull() {
        detailDailybookCe = null;
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

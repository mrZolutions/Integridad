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
public class DailybookCg {
    @Id
    @GeneratedValue
    private UUID id;
    
    private boolean active;
    private long dateRecordBook;
    private String clientProvName;
    private String billNumber;
    private String codeTypeContab;
    private String typeContab;
    private String generalDetail;
    private String dailyCgSeq;
    private String dailyCgStringSeq; //Generado automáticamente por el Sistema
    private String dailyCgStringUserSeq; //Introducido por el Usuario
    private double subTotalDoce;
    private double iva;
    private double subTotalCero;
    private double total;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad userIntegridad;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    @OneToMany(mappedBy = "dailybookCg", cascade = CascadeType.ALL)
    private List<DetailDailybookCg> detailDailybookCg;
    
    public void setListsNull() {
        detailDailybookCg = null;
    }
    
    public void setFatherListToNull() {
        userIntegridad.setListsNull();
        userIntegridad.setFatherListToNull();
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
    }
}

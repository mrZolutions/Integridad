package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class DetailCellar implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private long quantity;
    private Double costEach;
    private Double total;
    private String adicional;
    
    @ManyToOne
    @JoinColumn(name = "cellar_id")
    private Cellar cellar;
    
    @ManyToOne
    @JoinColumn(name = "credit_note_cellar_id")
    private CreditNoteCellar creditNoteCellar;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        cellar.setListsNull();
        cellar.setFatherListToNull();
        if (creditNoteCellar != null) {
            creditNoteCellar.setListsNull();
            creditNoteCellar.setFatherListToNull();
        }
    }
}
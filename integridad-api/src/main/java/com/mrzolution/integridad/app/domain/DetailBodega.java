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
public class DetailBodega implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private long quantity;
    private Double costEach;
    private Double total;
    
    @ManyToOne
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        bodega.setListsNull();
        bodega.setFatherListToNull();
    }
}

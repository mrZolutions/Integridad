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
 * @author daniel-one
 */

@Entity
@Data
public class DetailOffline implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private long quantity;
    private Double costEach;
    private Double total;
    private String adicional;

    @ManyToOne
    @JoinColumn(name = "billOffline_id")
    private BillOffline billOffline;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
    	billOffline.setListsNull();
    	billOffline.setFatherListToNull();
    }
}

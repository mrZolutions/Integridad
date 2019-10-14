package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Entity
@Data
public class Kardex implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String codeWarehouse;
    private String details;
    private String observation;
    private String detalle;
    private long dateRegister;
    private String prodName;
    private double prodCostEach;
    private long prodQuantity;
    private double prodTotal;
    private String userClientId;
    private String userId;
    private String subsidiaryId;
    private boolean active;
    private String credNoteIdVenta;
    private String credNoteIdCompra;
    
    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;
    
    @ManyToOne
    @JoinColumn(name = "cellar_id")
    private Cellar cellar;
    
    @ManyToOne
    @JoinColumn(name = "consumption_id")
    private Consumption consumption;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "billoff_id")
    private BillOffline billOffline;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        if (bill != null) {
            bill.setListsNull();
            bill.setFatherListToNull();
        }
        if (cellar != null) {
            cellar.setListsNull();
            cellar.setFatherListToNull();
        }
        if (consumption != null) {
            consumption.setListsNull();
            consumption.setFatherListToNull();
        }
        if (product != null) {
            product.setListsNull();
            product.setFatherListToNull();
        }
        if (billOffline != null) {
            billOffline.setListsNull();
            billOffline.setFatherListToNull();
        }
    }
    
    @Transient
    public static Kardex newKardexTest() {
        Kardex detailk = new Kardex();
        detailk.setBill(Bill.newBillTest());
        detailk.setProduct(Product.newProducTest());
        return detailk;
    }
}
package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by daniel.
 */
@Entity
@Data
public class ProductBySubsidiary implements Child{

    @Id
    @GeneratedValue
    private UUID id;

    private Long dateCreated;
    private Long quantity;

    private boolean active;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;
    
    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    	subsidiary.setListsNull();
    	subsidiary.setFatherListToNull();
        product.setFatherListToNull();
        product.setListsNull();
    }

    @Transient
    public static ProductBySubsidiary newProductBySubsidiaryTest(){
        ProductBySubsidiary product = new ProductBySubsidiary();
        product.setSubsidiary(Subsidiary.newSubsidiaryTest());
        product.setProduct(Product.newProducTest());
        
        return product;
    }

}

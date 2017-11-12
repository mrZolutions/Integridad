package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class ProductType {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;
    
    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL)
    private List<Product> products;
    
    public void setListsNull(){
    	if(products != null) products = null;
    }

    @Transient
    public static ProductType ProductTypeTest(){
    	ProductType productType = new ProductType();
    	productType.setProducts(new ArrayList<>());

        return productType;
    }
}

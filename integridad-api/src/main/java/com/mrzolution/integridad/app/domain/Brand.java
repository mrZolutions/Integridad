package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Brand {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;

    private boolean active;
    
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products;
    
    public void setListsNull(){
    	if(products != null) products = null;
    }

    @Transient
    public static Brand newBrandTest(){
    	Brand brand = new Brand();
    	brand.setProducts(new ArrayList<>());

        return brand;
    }
}

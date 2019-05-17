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

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;
    
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products;

    public void setFatherListToNull(){
        userClient.setFatherListToNull();
        userClient.setListsNull();

    }

    public void setListsNull(){
    	if(products != null) products = null;
    }

    @Transient
    public static Brand newBrandTest(){
    	Brand brand = new Brand();
    	brand.setProducts(new ArrayList<>());
    	brand.setUserClient(UserClient.newUserClientTest());

        return brand;
    }
}
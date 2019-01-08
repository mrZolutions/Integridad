package com.mrzolution.integridad.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;

/**
 * Created by daniel.
 */

@Entity
@Data
public class Product implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    
    private String name;
    private boolean active;
    private Long dateCreated;
    private Long lastDateUpdated;
    private String codeIntegridad;
    private String unitOfMeasurementAbbr;
    private String unitOfMeasurementFull;
    private String characteristics;
    private String barCode;

    private Double averageCost;
    private Double cashPercentage;
    private Double creditPercentage;
    private Double cardPercentage;
    private Double majorPercentage;
    private Long maxMinimun;
    private boolean ice;
    private boolean iva;


    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;
    
    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "subgroup_id")
    private SubGroup subgroup;
    

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductBySubsidiary> productBySubsidiaries;
    
    public void setListsNull() {
        if (productBySubsidiaries != null) {
            productBySubsidiaries = null;
        }
    }
    
    public void setFatherListToNull() {
    	userClient.setListsNull();
    	userClient.setFatherListToNull();
        productType.setListsNull();
        if (brand != null) {
            brand.setListsNull();
        }
        if (subgroup != null) {
            subgroup.setListsNull();
            subgroup.setFatherListToNull();
        }
    }

    @Transient
    public static Product newProducTest() {
        Product product = new Product();
        product.setUserClient(UserClient.newUserClientTest());
        product.setBrand(Brand.newBrandTest());
        product.setSubgroup(SubGroup.newSubBrandTest());
        product.setProductType(ProductType.newProductTypeTest());
        product.setProductBySubsidiaries(new ArrayList<>());
        
        return product;
    }

}

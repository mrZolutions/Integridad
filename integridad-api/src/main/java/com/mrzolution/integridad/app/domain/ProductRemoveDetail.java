package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class ProductRemoveDetail {
    @Id
    @GeneratedValue
    private UUID id;

    private Long date;
    private String reason;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_integridad_id")
    private UserIntegridad userIntegridad;
    
    public void setListsNull(){}

    @Transient
    public static ProductRemoveDetail newUserTypeTest(){
    	ProductRemoveDetail productRemoveDetail = new ProductRemoveDetail();
        return productRemoveDetail;
    }
}
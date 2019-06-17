package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class CuentaContableByProduct {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cuenta_contable_id")
    private CuentaContable cuentaContable;

    public void setListsNull(){
    }

    public void setFatherListToNull(){
        if(cuentaContable != null){
            cuentaContable.setListsNull();
            cuentaContable.setFatherListToNull();
        }

        if(product != null){
            product.setFatherListToNull();
            product.setListsNull();
        }
    }
}

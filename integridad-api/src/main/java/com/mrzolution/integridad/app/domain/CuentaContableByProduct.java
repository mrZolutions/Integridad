package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class CuentaContableByProduct implements Child {
    @Id
    @GeneratedValue
    private UUID id;
    private String type;

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

package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    private String detail;
    private long datePayment;
    private String typePayment;
    private String modePayment;
    private double valor;
    private String noAccount;
    private String noDocument;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credits credits;

    @ManyToOne
    @JoinColumn(name = "cuenta_contable_id")
    private CuentaContable cuentaContablePrincipal;

    public void setFatherListToNull(){
        credits.setFatherListToNull();
        credits.setListsNull();
        
        cuentaContablePrincipal.setFatherListToNull();
        cuentaContablePrincipal.setListsNull();
    }

}

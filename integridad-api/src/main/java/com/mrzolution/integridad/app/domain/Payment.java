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

    private String typePayment;
    private String modePayment;
    private String detail;
    private long datePayment;
    private String noAccount;
    private String noDocument;
    private String documentNumber;
    private double valor;

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

    public void setCredits(UUID idCredit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

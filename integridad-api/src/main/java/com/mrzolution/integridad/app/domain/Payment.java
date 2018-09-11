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

    private double value;
    private String detail;
    private String noCheck;
    private String noAccount;
    private String noTransfer;
    private String noDocument;

    @ManyToOne
    @JoinColumn(name = "cuenta_contable_principal_id")
    private CuentaContable cuentaContablePrincipal;

    @ManyToOne
    @JoinColumn(name = "cuenta_contable_auxiliar_id")
    private CuentaContable cuentaContableAuxiliar;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credits credit;


    public void setFatherListToNull(){
        cuentaContablePrincipal.setFatherListToNull();
        cuentaContablePrincipal.setListsNull();

        cuentaContableAuxiliar.setFatherListToNull();
        cuentaContableAuxiliar.setListsNull();

        credit.setFatherListToNull();
        credit.setListsNull();
    }

}

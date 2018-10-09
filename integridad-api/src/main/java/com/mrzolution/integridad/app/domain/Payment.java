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
    @JoinColumn(name = "cuenta_contable_principal_id")
    private CuentaContable cuentaContablePrincipal;

    @ManyToOne
    @JoinColumn(name = "code_conta")
    private Client clientCodeConta;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credits credit;


    public void setFatherListToNull(){
        cuentaContablePrincipal.setFatherListToNull();
        cuentaContablePrincipal.setListsNull();

        clientCodeConta.setFatherListToNull();
        clientCodeConta.setListsNull();

        credit.setFatherListToNull();
        credit.setListsNull();
    }

}

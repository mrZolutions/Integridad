package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;

import java.util.List;
import java.util.UUID;
import javax.persistence.*;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Entity
@Data
public class PagoOffline implements Child {
    @Id
    @GeneratedValue
    private UUID id;

    private String medio;
    private double total;
    private String payForm;
    private String cardBrand;
    private String chequeAccount;
    private String chequeBank;
    private String chequeNumber;
    private String numeroLote;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL)
    private List<Credits> credits;

   @ManyToOne
    @JoinColumn(name = "billOffline_id")
    private BillOffline billOffline;

    public void setListsNull(){
        credits = null;
    }

    public void setFatherListToNull(){
        billOffline.setListsNull();
        billOffline.setFatherListToNull();
    }
}
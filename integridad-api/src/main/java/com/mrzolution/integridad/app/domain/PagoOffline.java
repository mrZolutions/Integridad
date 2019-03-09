package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private String chequeDiasPlazo;
    private String numeroLote;
    private long fechaCobro;

   @ManyToOne
    @JoinColumn(name = "billOffline_id")
    private BillOffline billOffline;

    public void setFatherListToNull(){
        billOffline.setListsNull();
        billOffline.setFatherListToNull();
    }
}

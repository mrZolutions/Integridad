package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Cashier implements Child {
    
    @Id
    @GeneratedValue
    private UUID id;

    private String nameNumber;
    private String threeCode;

    private long billNumberSeq;
    private long billOfflineNumberSeq;
    private long dailyCgNumberSeq;
    private long dailyCeNumberSeq;
    private long dailyCiNumberSeq;
    private long dailyFvNumberSeq;
    private long dailyCppNumberSeq;
    private long debtsNumberSeq;
    private long whNumberSeq;
    private long csmNumberSeq;
    private long quotationNumberSeq;
    private long retentionNumberSeq;
    private long creditNoteNumberSeq;
    private long creditNoteCellarNumberSeq;
    private long compCobroNumberSeq;
    private long compPagoNumberSeq;
    private long dateCreated;
    private boolean active;
    private boolean specialPrint;

    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;

    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
        subsidiary.setListsNull();
        subsidiary.setFatherListToNull();
    }

    @Transient
    public static Cashier newCashierTest() {
        Cashier cashier = new Cashier();
        cashier.setSubsidiary(Subsidiary.newSubsidiaryTest());
        return cashier;
    }

}
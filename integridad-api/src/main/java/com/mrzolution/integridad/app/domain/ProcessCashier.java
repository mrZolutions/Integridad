package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class ProcessCashier {
    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private Long date;
    private String currency;
    private String type;
    private double total;

    @OneToMany(mappedBy = "processCashier", cascade = CascadeType.ALL)
    List<DetailProcessCashier> details;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad user;

    @ManyToOne
    @JoinColumn(name = "cashier_id")
    private Cashier cashier;

    public void setFatherListToNull(){
        cashier.setListsNull();
        cashier.setFatherListToNull();
        user.setListsNull();
        user.setFatherListToNull();
    }

    public void setListsToNull(){
        details = null;
    }

}

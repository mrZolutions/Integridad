package com.mrzolution.integridad.app.domain;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class DebtsToPay {
    @Id
    @GeneratedValue
    private UUID id;

    private String buyTypeVoucher;
    private String purchaseType;
    private long date;
    private String ejercicio;
    private String establishmentNumber;
    private String cashierNumber;
    private String sequentialNumber;
    private String billNumber;
    private String authorizationNumber;
    private double total;
    private double subTotal;
    private String tipoDeCompra;
    private String account_type;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @OneToMany(mappedBy = "debtsToPay", cascade = CascadeType.ALL)
    private List<DetailDebtsToPay> detailDebtsToPay;

    public void setListsNull(){
        detailDebtsToPay = null;
    };

    public void setFatherListToNull(){
        provider.setListsNull();
        provider.setFatherListToNull();
    };
}

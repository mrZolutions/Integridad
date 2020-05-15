package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class DetailProcessCashier {
    @Id
    @GeneratedValue
    private UUID id;
    private double denominacion;
    private Integer quantity;
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "process_cashier_id")
    ProcessCashier processCashier;

}

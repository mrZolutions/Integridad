package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Cupon {

    @Id
    @GeneratedValue
    private UUID id;

    private String numeroCupon;
    private Long dateCupon;
    private Boolean hasIva;
    private Double precio;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "guia_id")
    private Guia guia;

    @ManyToOne
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;

    @ManyToOne
    @JoinColumn(name = "placa_id")
    private Placa placa;
}

package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Guia {

    @Id
    @GeneratedValue
    private UUID id;

    private Long dateGuia;
    private String noOrden;
    private String formaPago;
    private Double subTotal;
    private Double subTotalZero;
    private Double subTotalDoce;
    private Double ivaDoce;
    private Double total;
    private Boolean active;
    private Boolean finalizada;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    @ManyToOne
    @JoinColumn(name = "asociado_id")
    private Asociado asociado;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


}

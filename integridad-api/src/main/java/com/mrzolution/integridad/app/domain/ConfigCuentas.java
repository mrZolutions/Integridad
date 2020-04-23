package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class ConfigCuentas {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private OptionConfigCuentas option;

    private UUID idCuenta;
    private String code;
    private String description;
    private boolean active;
}

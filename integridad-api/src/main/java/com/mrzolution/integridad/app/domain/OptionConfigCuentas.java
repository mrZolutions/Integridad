package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class OptionConfigCuentas {

    @Id
    @GeneratedValue
    private UUID id;

    private String description;
    private String name;
    private String code;
    private boolean active;
}

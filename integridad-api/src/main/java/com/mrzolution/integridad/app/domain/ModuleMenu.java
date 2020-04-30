package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
public class ModuleMenu {
    @Id
    @GeneratedValue
    private UUID id;

    private String menuName;
}

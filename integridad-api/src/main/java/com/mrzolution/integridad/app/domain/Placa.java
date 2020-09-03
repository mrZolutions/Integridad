package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Placa {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String conductor;
    private String cedula;
    private Long fechaCaducidadLicencia;
    private String tipoLicencia;
    private String observacion;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    @ManyToOne
    @JoinColumn(name = "asociado_id")
    private Asociado asociado;

    public void setFatherListToNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
    }
}

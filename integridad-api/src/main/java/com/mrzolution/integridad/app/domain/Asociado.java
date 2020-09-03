package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class Asociado {

    @Id
    @GeneratedValue
    private UUID id;
    private String apellidos;
    private String nombres;
    private String ruc;
    private String correo;
    private String telefono;
    private String contacto;
    private String telefonoContacto;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    public void setFatherListToNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
    }
}

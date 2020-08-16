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
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;

    public void setFatherListToNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
    }
}

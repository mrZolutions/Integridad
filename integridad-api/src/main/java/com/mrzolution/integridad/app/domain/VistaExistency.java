/**
 *
 * @author daniel-one
 */
package com.mrzolution.integridad.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table (name="vista_existency")
public class VistaExistency {
    
    @Id
    private UUID id;
    private String code;
    private String name;
    private Double cost;
    private int maxMin;
    private int quantity;
    
    
    @ManyToOne
    @JoinColumn(name = "user_client_id")
    private UserClient userClient;
    
    public void setFatherListToNull(){
        userClient.setListsNull();
        userClient.setFatherListToNull();
    }
    
}

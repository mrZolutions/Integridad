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

@Entity
@Table (name="vista_existency")
@Data
public class Existency {
    
    @Id
    @Column(name = "code_integridad")
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

    public void setListsNull() {
        
    }
}

package com.mrzolution.integridad.app.domain;

import com.mrzolution.integridad.app.interfaces.Child;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by daniel.
 */
@Entity
@Data
public class Warehouse implements Child{

    @Id
    @GeneratedValue
    private UUID id;

    private String nameNumber;
    
    private long dateCreated;
    
    private boolean active;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;

    
    public void setListsNull(){ }
    
    public void setFatherListToNull(){
        subsidiary.setListsNull();
        subsidiary.setFatherListToNull();
    }

    @Transient
    public static Warehouse newWarehouseTest(){
        Warehouse warehouse = new Warehouse();
        warehouse.setSubsidiary(Subsidiary.newSubsidiaryTest());

        return warehouse;
    }

}

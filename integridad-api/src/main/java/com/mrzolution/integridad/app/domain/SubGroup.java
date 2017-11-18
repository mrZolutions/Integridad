package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class SubGroup {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    
    @OneToMany(mappedBy = "subgroup", cascade = CascadeType.ALL)
    private List<Product> products;

    public void setFatherListToNull(){
        group.setListsNull();
        group.setFatherListToNull();
    }
    
    public void setListsNull(){
    	if(products != null) products = null;
    }

    @Transient
    public static SubGroup newSubBrandTest(){
    	SubGroup subGroup = new SubGroup();
    	subGroup.setGroup(Group.newGroupTest());
    	subGroup.setProducts(new ArrayList<>());

        return subGroup;
    }
}

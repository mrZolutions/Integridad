package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Group {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Line line;
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<SubGroup> subGroups;

    public void setFatherListToNull(){
        line.setListsNull();
        line.setFatherListToNull();
    }
    
    public void setListsNull(){
    	if(subGroups != null) subGroups = null;
    }

    @Transient
    public static Group newGroupTest(){
    	Group group = new Group();
    	group.setLine(Line.newLineTest());
    	group.setSubGroups(new ArrayList<>());

        return group;
    }
}

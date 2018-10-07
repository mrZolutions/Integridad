package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class GroupLine {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
    
    @OneToMany(mappedBy = "groupLine", cascade = CascadeType.ALL)
    private List<SubGroup> subGroups;

    public void setFatherListToNull(){
        line.setListsNull();
        line.setFatherListToNull();
    }
    
    public void setListsNull(){
    	if(subGroups != null) subGroups = null;
    }

    @Transient
    public static GroupLine newGroupLineTest(){
    	GroupLine groupLine = new GroupLine();
    	groupLine.setLine(Line.newLineTest());
    	groupLine.setSubGroups(new ArrayList<>());

        return groupLine;
    }
}

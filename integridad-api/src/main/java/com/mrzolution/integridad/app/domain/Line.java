package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Line {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;

    private boolean active;
    
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Group> groups;

    public void setFatherListToNull(){

    }
    
    public void setListsNull(){
    	if(groups != null) groups = null;
    }

    @Transient
    public static Line newLineTest(){
    	Line line = new Line();
    	line.setGroups(new ArrayList<>());

        return line;
    }
}

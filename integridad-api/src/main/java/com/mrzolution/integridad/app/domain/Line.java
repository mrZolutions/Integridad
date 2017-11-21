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
    private List<GroupLine> groupLines;

    public void setFatherListToNull(){

    }
    
    public void setListsNull(){
    	if(groupLines != null) groupLines = null;
    }

    @Transient
    public static Line newLineTest(){
    	Line line = new Line();
    	line.setGroupLines(new ArrayList<>());

        return line;
    }
}

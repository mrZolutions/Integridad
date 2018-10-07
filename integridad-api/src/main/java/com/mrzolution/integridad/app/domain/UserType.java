package com.mrzolution.integridad.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class UserType {

	@Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String code;
    
    @OneToMany(mappedBy = "userType", cascade = CascadeType.ALL)
    private List<UserIntegridad> users;
    
    public void setListsNull(){
    	if(users != null) users = null;
    }

    @Transient
    public static UserType newUserTypeTest(){
    	UserType userType = new UserType();
    	userType.setUsers(new ArrayList<>());

        return userType;
    }
}

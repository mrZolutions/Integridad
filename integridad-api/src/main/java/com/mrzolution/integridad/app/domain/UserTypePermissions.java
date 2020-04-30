package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class UserTypePermissions {

	@Id
    @GeneratedValue
    private UUID id;
	
	private String path;
	private String menuName;

	@ManyToOne
    @JoinColumn(name = "user_type_id")
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "module_menu_id")
    private ModuleMenu moduleMenu;

    public void setListsNull(){
    }
    
    public void setFatherListToNull(){
    	userType.setListsNull();
    }

    @Transient
    public static UserTypePermissions newUserTypePermissionsTest(){
    	UserTypePermissions userTypepermissions = new UserTypePermissions();
        return userTypepermissions;
    }
}
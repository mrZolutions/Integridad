package com.mrzolution.integridad.app.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import lombok.Data;

/**
 * Created by daniel.
 */
@Entity
@Data
public class UserIntegridad {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String userName;
    private String password;
    private String validation;
    private boolean active;
    
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_type_id")
    private UserType userType;
    
    public void setListsNull(){
    	
    }
    
    public void setFatherListToNull(){
    	userType.setListsNull();
    }

    @Transient
    public static UserIntegridad newUserIntegridadTest(){
        UserIntegridad userIntegridad = new UserIntegridad();
        userIntegridad.setUserType(new UserType());

        return userIntegridad;
    }

}

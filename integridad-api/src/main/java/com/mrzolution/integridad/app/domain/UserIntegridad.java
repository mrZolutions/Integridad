package com.mrzolution.integridad.app.domain;

import lombok.Data;

import javax.persistence.*;

import org.hibernate.validator.constraints.Email;

import java.util.UUID;

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
    
    @Email
    private String email;

    
    public void setListsNull(){
    	
    }

    @Transient
    public static UserIntegridad newUserIntegridadTest(){
        UserIntegridad userIntegridad = new UserIntegridad();

        return userIntegridad;
    }

}

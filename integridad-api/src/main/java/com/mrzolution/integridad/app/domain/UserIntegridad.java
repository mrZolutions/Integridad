package com.mrzolution.integridad.app.domain;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;

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

    private String firstName;
    private String lastName;
    private String celPhone;
    private String phone;
    private String city;
    private String address1;
    private String address2;
    private String cedula;
    private String ruc;
    private long birthDay;
    
    private long dateCreated;
    private String userName;
    private String password;
    
    private boolean tempPass;
    
    private String validation;
    private boolean active;
    
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_type_id")
    private UserType userType;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad user;
    
    public void setListsNull(){
    	
    }
    
    public void setFatherListToNull(){
    	userType.setListsNull();
    	if(user != null){
    	    user.setFatherListToNull();
        }
    	if(subsidiary != null) {
    		subsidiary.setListsNull(); 
    		subsidiary.setFatherListToNull();
    	};
    }

    @Transient
    public static UserIntegridad newUserIntegridadTest(){
        UserIntegridad userIntegridad = new UserIntegridad();
        userIntegridad.setUserType(UserType.newUserTypeTest());
        userIntegridad.setSubsidiary(Subsidiary.newSubsidiaryTest());

        UserIntegridad userFatther = new UserIntegridad();
        userFatther.setUserType(UserType.newUserTypeTest());
        userFatther.setSubsidiary(Subsidiary.newSubsidiaryTest());

        userIntegridad.setUser(userFatther);

        return userIntegridad;
    }

}

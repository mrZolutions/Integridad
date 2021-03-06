package com.mrzolution.integridad.app.domain;

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
//    private String cashier;
    private long birthDay;
    private long dateCreated;
    private String userName;
    private String password;
    private boolean tempPass;
    private String validation;
    private boolean active;

    private String token;
    private String refreshToken;
    private boolean apiConnection;
    private boolean guias;
    private String type;

    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "cashier_id")
    private Cashier cashier;

    @ManyToOne
    @JoinColumn(name = "user_type_id")
    private UserType userType;
    
    @ManyToOne
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserIntegridad user;

    @ManyToOne
    @JoinColumn(name = "aso_id")
    private Asociado asociado;
    
    public void setListsNull() {
    }
    
    public void setFatherListToNull() {
    	userType.setListsNull();
    	if (user != null) {
    	    user.setFatherListToNull();
        }
    	if (subsidiary != null) {
    		subsidiary.setListsNull(); 
    		subsidiary.setFatherListToNull();
    	};
    	if (cashier != null) {
    	    cashier.setFatherListToNull();
    	    cashier.setListsNull();
        }

    	if(asociado != null){
            asociado.setFatherListToNull();
        }
    }

    @Transient
    public static UserIntegridad newUserIntegridadTest() {
        UserIntegridad userIntegridad = new UserIntegridad();
        userIntegridad.setUserType(UserType.newUserTypeTest());
        userIntegridad.setSubsidiary(Subsidiary.newSubsidiaryTest());
        userIntegridad.setCashier(Cashier.newCashierTest());
        UserIntegridad userFatther = new UserIntegridad();
        userFatther.setUserType(UserType.newUserTypeTest());
        userFatther.setSubsidiary(Subsidiary.newSubsidiaryTest());
        userIntegridad.setUser(userFatther);
        Asociado asociadoFather = new Asociado();
        asociadoFather.setUserClient(UserClient.newUserClientTest());
        userIntegridad.setAsociado(asociadoFather);
        return userIntegridad;
    }

}
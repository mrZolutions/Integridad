package com.mrzolution.integridad.app.services;

import java.util.*;

import com.mrzolution.integridad.app.domain.Subsidiary;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.mrzolution.integridad.app.cons.Constants;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserIntegridadServices {
    @Autowired
    UserIntegridadRepository userIntegridadRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MailingService mailingService;
    @Autowired
    UserTypeServices userTypeServices;
    @Autowired
    SubsidiaryServices subsidiaryServices;
	@Autowired
	httpCallerService httpCallerService;
	
    public UserIntegridad create(UserIntegridad userIntegridad) throws BadRequestException {
        log.info("UserIntegridadServices create: {}", userIntegridad.getEmail());	
	if (userIntegridadRepository.findByEmailIgnoreCaseAndActive(userIntegridad.getEmail(), true) != null) {
            throw new BadRequestException("Email already used");
	}
	if (userIntegridadRepository.findByCedulaAndActive(userIntegridad.getCedula(), true) != null) {
            throw new BadRequestException("Cedula already used");
	}	
	String passPreEncoded = userIntegridad.getPassword();
	String encoded = passwordEncoder.encode(userIntegridad.getPassword());
	userIntegridad.setPassword(encoded);
	userIntegridad.setValidation(UUID.randomUUID().toString());
	userIntegridad.setActive(false);
	userIntegridad.setTempPass(true);
	userIntegridad.setDateCreated(new Date().getTime());
	log.info("UserIntegridadServices create: {} password Encoded", userIntegridad.getEmail());	
	if (userIntegridad.getUserType() == null) {
            UserType userType = userTypeServices.getByCode(Constants.USER_TYPE_EMP_CODE);
            userIntegridad.setUserType(userType);
	}	
	UserIntegridad saved = userIntegridadRepository.save(userIntegridad);	
	mailingService.sendEmailREgister(userIntegridad, passPreEncoded);	
	log.info("UserIntegridadServices created: {}", userIntegridad.getId());	
	return saved;
    }
	
    public UserIntegridad update (UserIntegridad userIntegridad) throws BadRequestException {
	log.info("UserIntegridadServices update: {}", userIntegridad.getEmail());
	UserIntegridad retrieved = userIntegridadRepository.findByEmailIgnoreCaseAndActive(userIntegridad.getEmail(), true);
	UserIntegridad retrievedCedula = userIntegridadRepository.findByCedulaAndActive(userIntegridad.getCedula(), true);
	if (!retrieved.getId().equals(userIntegridad.getId())) {
            throw new BadRequestException("Email already used");
	}
	if (!retrievedCedula.getId().equals(userIntegridad.getId())) {
            throw new BadRequestException("Cedula already used");
	}
	if (!"".equals(userIntegridad.getPassword())) {
            String encoded = passwordEncoder.encode(userIntegridad.getPassword());
            userIntegridad.setPassword(encoded);
            log.info("UserIntegridadServices update: {} password Encoded", userIntegridad.getEmail());
	} else {
            userIntegridad.setPassword(retrieved.getPassword());
	}
	if (userIntegridad.getUserType() == null) {
            UserType userType = userTypeServices.getByCode(Constants.USER_TYPE_EMP_CODE);
            userIntegridad.setUserType(userType);
	}
	UserIntegridad updated = userIntegridadRepository.save(userIntegridad);
	log.info("UserIntegridadServices updated: {}", userIntegridad.getId());
	updated.setFatherListToNull();
	return updated;
    }

    public UserIntegridad authenticate(UserIntegridad user) throws Exception {
	log.info("UserIntegridadServices authenticate: {}", user.getEmail());
	UserIntegridad userResponse = userIntegridadRepository.findByEmailIgnoreCaseAndActive(user.getEmail(), true);
	if (userResponse == null) {
            throw new BadRequestException("Invalid Email");
	}
	if (!passwordEncoder.matches(user.getPassword(), userResponse.getPassword())) {
            userResponse = null;
            throw new BadRequestException("Wrong Password");
	}
	userResponse.setFatherListToNull();

	String data = "{\"email\": \""+ user.getEmail() + "\", \"password\": \"" + user.getPassword()+"\"}";
	String response = httpCallerService.post(Constants.FACTURACION_LINK_AUTH, data, null);
	System.out.println(response);

	JSONParser parser = new JSONParser();
	ContainerFactory containerFactory = new ContainerFactory(){
		public List creatArrayContainer() { return new LinkedList(); }
		public Map createObjectContainer() { return new LinkedHashMap(); }
	};

	Map json = (Map)parser.parse(response, containerFactory);
	Iterator iter = json.entrySet().iterator();
	while(iter.hasNext()){
		Map.Entry entry = (Map.Entry)iter.next();
		if(entry.getKey().equals("accessToken"))  userResponse.setToken((String) entry.getValue());
		if(entry.getKey().equals("refreshToken")) userResponse.setRefreshToken((String) entry.getValue());
//      System.out.println(entry.getKey() + "=>" + entry.getValue());
	}

	if(userResponse.getToken() != null) update(userResponse);


	log.info("UserIntegridadServices authenticate success: {}, id: {}", userResponse.getEmail(), userResponse.getId());
	return userResponse;
    }

    public UserIntegridad activate(UUID userId, String validation) throws BadRequestException {
	log.info("UserIntegridadServices activate: {}", userId);
	UserIntegridad userToValidate = userIntegridadRepository.findByIdAndValidation(userId, validation);
	if (userToValidate != null && !userToValidate.isActive()) {
            log.info("UserIntegridadServices activating: {}", userToValidate.getId());
            userToValidate.setActive(true);
            UserIntegridad activeUser = userIntegridadRepository.save(userToValidate);
            activeUser.setFatherListToNull();
            log.info("UserIntegridadServices activated: {}", activeUser.getId());
            return activeUser;
	}
        throw new BadRequestException("Wrong URL to validate");
    }

    public UserIntegridad recoverPassword(String eMail) {
	log.info("UserIntegridadServices recoverPassword: {}", eMail);
	UserIntegridad userResponse = userIntegridadRepository.findByEmailIgnoreCaseAndActive(eMail, true);
        if (userResponse == null) {
            throw new BadRequestException("Invalid Email");
	}
	log.info("UserIntegridadServices recoverPassword user found: {}", userResponse.getId());
	UUID rand = UUID.randomUUID();
	String newPass = rand.toString();
	log.info("UserIntegridadServices recoverPassword user found new pass: {}", newPass);
	String encoded = passwordEncoder.encode(newPass);
	userResponse.setPassword(encoded);
	userResponse.setTempPass(true);
	userIntegridadRepository.save(userResponse);
	log.info("UserIntegridadServices recoverPassword user updated with new pass: {}", userResponse.getId());
	mailingService.sendEmailRecoveryPass(userResponse, newPass);
	userResponse.setFatherListToNull();
	return userResponse;
    }

    public Iterable<UserIntegridad> getAllActivesLazy() {
	Iterable<UserIntegridad> userIntegridadList = userIntegridadRepository.findByActive(true);
	userIntegridadList.forEach(user -> {user.setFatherListToNull();});
	return userIntegridadList;
    }

    public Iterable<UserIntegridad> getByCodeTypeAndSubsidiaryIdActivesLazy(String code, UUID subsidiaryId) {
	log.info("UserIntegridadServices getByCodeTypeAndSubsidiaryIdActivesLazy code: {},  subsidiaryId: {}", code, subsidiaryId);
	UserType userType = userTypeServices.getByCode(code);
	log.info("UserIntegridadServices getByCodeTypeAndSubsidiaryIdActivesLazy userType retreived");
	Iterable<UserIntegridad> userIntegridadList = new ArrayList<>();
	if (Constants.USER_TYPE_SAD_CODE.equals(code)) {
            userIntegridadList = userIntegridadRepository.
            findByUserTypeAndActive(userType,true);
            log.info("UserIntegridadServices getByCodeTypeAndSubsidiaryIdActivesLazy userIntegridad list retreived");
	} else {
            Subsidiary subsidiary = subsidiaryServices.getSubsidiaryById(subsidiaryId);
            log.info("UserIntegridadServices getByCodeTypeAndSubsidiaryIdActivesLazy subsidiary retreived");
            userIntegridadList = userIntegridadRepository.
            findByUserTypeAndActiveAndSubsidiary(userType,true, subsidiary);
            log.info("UserIntegridadServices getByCodeTypeAndSubsidiaryIdActivesLazy userIntegridad list retreived");
	}
	userIntegridadList.forEach(user -> user.setFatherListToNull());
        return userIntegridadList;
    }

}
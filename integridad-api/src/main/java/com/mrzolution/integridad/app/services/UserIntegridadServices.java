package com.mrzolution.integridad.app.services;

import java.util.Date;
import java.util.UUID;

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
	
	public UserIntegridad create(UserIntegridad userIntegridad) throws BadRequestException{
		log.info("UserIntegridadServices create: {}", userIntegridad.getEmail());
		
		if(userIntegridadRepository.findByEmailIgnoreCaseAndActive(userIntegridad.getEmail(), true) != null){
			throw new BadRequestException("Email already used");
		}
		
		String passPreEncoded = userIntegridad.getPassword();
		
		String encoded = passwordEncoder.encode(userIntegridad.getPassword());
		userIntegridad.setPassword(encoded);
		userIntegridad.setValidation(UUID.randomUUID().toString());
		userIntegridad.setActive(false);
		userIntegridad.setDateCreated(new Date().getTime());
		log.info("UserIntegridadServices create: {} password Encoded", userIntegridad.getEmail());
		
		if(userIntegridad.getUserType() == null){
			UserType userType = userTypeServices.getByCode(Constants.USER_TYPE_EMP_CODE);
			userIntegridad.setUserType(userType);
		}
		
		UserIntegridad saved = userIntegridadRepository.save(userIntegridad);
		
		mailingService.sendEmailREgister(userIntegridad, passPreEncoded);
		
		log.info("UserIntegridadServices created: {}", userIntegridad.getId());
		
		return saved;
	}

	public UserIntegridad authenticate(UserIntegridad user) throws BadRequestException{
		log.info("UserIntegridadServices authenticate: {}", user.getEmail());
		UserIntegridad userResponse = userIntegridadRepository.findByEmailIgnoreCaseAndActive(user.getEmail(), true);
		if(userResponse == null){
			throw new BadRequestException("Invalid Email");
		}
		
		if(!passwordEncoder.matches(user.getPassword(), userResponse.getPassword())){
			userResponse = null;
			throw new BadRequestException("Wrong Password");
		}
		
		userResponse.setFatherListToNull();
		
		log.info("UserIntegridadServices authenticate success: {}, id: {}", userResponse.getEmail(), userResponse.getId());
		return userResponse;
	}

	public UserIntegridad activate(UUID userId, String validation) throws BadRequestException{
		UserIntegridad userToValidate = userIntegridadRepository.findByIdAndValidation(userId, validation);
		if(userToValidate != null && !userToValidate.isActive()){
			userToValidate.setActive(true);
			UserIntegridad activeUser = userIntegridadRepository.save(userToValidate);
			
			return activeUser;
		}
		throw new BadRequestException("Wrong URL to validate");
	}

	public UserIntegridad recoverPassword(String eMail) {
		log.info("UserIntegridadServices recoverPassword: {}", eMail);
		UserIntegridad userResponse = userIntegridadRepository.findByEmailIgnoreCaseAndActive(eMail, true);
		
		if(userResponse == null){
			throw new BadRequestException("Invalid Email");
		}
		
		log.info("UserIntegridadServices recoverPassword user found: {}", userResponse.getId());
		UUID rand = UUID.randomUUID();
		String newPass = rand.toString();
		
		log.info("UserIntegridadServices recoverPassword user found new pass: {}", newPass);
		String encoded = passwordEncoder.encode(newPass);
		userResponse.setPassword(encoded);
		
		userIntegridadRepository.save(userResponse);
		
		log.info("UserIntegridadServices recoverPassword user updated with new pass: {}", userResponse.getId());
		
		mailingService.sendEmailRecoveryPass(userResponse, newPass);
		
		return userResponse;
	}

}

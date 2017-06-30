package com.mrzolution.integridad.app.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserIntegridad;
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
	
	public UserIntegridad create(UserIntegridad userIntegridad) throws BadRequestException{
		log.info("UserIntegridadServices create: {}", userIntegridad.getEmail());
		
		if(userIntegridadRepository.findByEmailContainingIgnoreCaseAndActive(userIntegridad.getEmail(), true) != null){
			throw new BadRequestException("Email already used");
		}
		
		String encoded = passwordEncoder.encode(userIntegridad.getPassword());
		userIntegridad.setPassword(encoded);
		userIntegridad.setValidation(UUID.randomUUID().toString());
		userIntegridad.setActive(false);
		log.info("UserIntegridadServices create: {} password Encoded", userIntegridad.getEmail());
		
		UserIntegridad saved = userIntegridadRepository.save(userIntegridad);
		
//		TODO send email to validate EMAIL with id and validation ---------------------------------------------------------------
		
		log.info("UserIntegridadServices created: {}", userIntegridad.getId());
		
		return saved;
	}

	public UserIntegridad authenticate(UserIntegridad user) throws BadRequestException{
		log.info("UserIntegridadServices authenticate: {}", user.getEmail());
		UserIntegridad userResponse = userIntegridadRepository.findByEmailContainingIgnoreCaseAndActive(user.getEmail(), true);
		if(userResponse == null){
			throw new BadRequestException("Invalid Email");
		}
		
		if(!passwordEncoder.matches(user.getPassword(), userResponse.getPassword())){
			userResponse = null;
			throw new BadRequestException("Wrong Password");
		}
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

}

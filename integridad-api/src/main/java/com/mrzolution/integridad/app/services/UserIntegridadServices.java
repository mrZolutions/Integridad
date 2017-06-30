package com.mrzolution.integridad.app.services;

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
		
		if(userIntegridadRepository.findByEmailContainingIgnoreCase(userIntegridad.getEmail()) != null){
			throw new BadRequestException("Email already used");
		}
		
		String encoded = passwordEncoder.encode(userIntegridad.getPassword());
		userIntegridad.setPassword(encoded);
		log.info("UserIntegridadServices create: {} password Encoded", userIntegridad.getEmail());
		
		UserIntegridad saved = userIntegridadRepository.save(userIntegridad);
		
		log.info("UserIntegridadServices created: {}", userIntegridad.getId());
		
		return saved;
	}

	public UserIntegridad authenticate(UserIntegridad user) throws BadRequestException{
		log.info("UserIntegridadServices authenticate: {}", user.getEmail());
		UserIntegridad userResponse = userIntegridadRepository.findByEmailContainingIgnoreCase(user.getEmail());
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

}

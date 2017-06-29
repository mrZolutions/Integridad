package com.mrzolution.integridad.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserIntegridadServices {
	
	@Autowired
	UserIntegridadRepository userIntegridadRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public UserIntegridad create(UserIntegridad userIntegridad){
		log.info("UserIntegridadServices create: {}", userIntegridad.getEmail());
		String encoded = passwordEncoder.encode(userIntegridad.getPassword());
		userIntegridad.setPassword(encoded);
		log.info("UserIntegridadServices create: {} password Encoded", userIntegridad.getEmail());
		
		System.out.println("***************** 1: " +  userIntegridad.getPassword());
		System.out.println("***************** 3: " +  (passwordEncoder.matches("12345", encoded)));
		
		UserIntegridad saved = userIntegridadRepository.save(userIntegridad);
		
		log.info("UserIntegridadServices created: {}", userIntegridad.getId());
		
		return saved;
	}

}

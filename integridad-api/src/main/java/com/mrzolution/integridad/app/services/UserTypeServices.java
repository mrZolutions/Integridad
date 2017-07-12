package com.mrzolution.integridad.app.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;
import com.mrzolution.integridad.app.repositories.UserTypeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserTypeServices {
	
	@Autowired
	UserTypeRepository userTypeRepository;
	
	public UserType create(UserType userType){
		log.info("UserTypeServices create: {}", userType.getName());
		UserType saved = userTypeRepository.save(userType);
		log.info("UserTypeServices created id: {}", saved.getId());
		return saved;
	}
	
	public UserType update(UserType userType) throws BadRequestException{
		if(userType.getId() == null){
			throw new BadRequestException("Invalid User Type");
		}
		log.info("UserTypeServices update: {}", userType.getName());
		UserType updated = userTypeRepository.save(userType);
		log.info("UserTypeServices update id: {}", updated.getId());
		return updated;
	}
	
	public UserType getById(UUID id){
		log.info("UserTypeServices getById: {}", id);
		UserType retrieved = userTypeRepository.findOne(id);
		if(retrieved != null){
			log.info("UserTypeServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("UserTypeServices retrieved id NULL: {}", id);
		}
		
		return retrieved;
	}
	
	public Iterable<UserType> getAll(){
		return userTypeRepository.findAll();
	}
	

}

package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
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
	@Autowired
	UserIntegridadRepository userIntegridadRepository;
	
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
		userType.setListsNull();
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
		
		populateChildren(retrieved);
		return retrieved;
	}
	
	public Iterable<UserType> getAll(){
		log.info("UserTypeServices getAll");
		Iterable<UserType> usersType = userTypeRepository.findAll();
		for (UserType userType : usersType) {
			populateChildren(userType);
		}
		log.info("UserTypeServices getAll size retrieved: {}", Iterables.size(usersType));
		return usersType;
	}
	
	private void populateChildren(UserType userType) {
		log.info("UserTypeServices populateChildren userTypeId: {}", userType.getId());
		List<UserIntegridad> userIntegridadList = new ArrayList<>();
		Iterable<UserIntegridad> usersIntegridad= userIntegridadRepository.findByUserType(userType);
		
		for (UserIntegridad userIntegridad : usersIntegridad) {
			userIntegridad.setUserType(null);
			
			userIntegridadList.add(userIntegridad);
		}
		
		userType.setUsers(userIntegridadList);
		log.info("UserTypeServices populateChildren FINISHED userTypeId: {}", userType.getId());
	}

}

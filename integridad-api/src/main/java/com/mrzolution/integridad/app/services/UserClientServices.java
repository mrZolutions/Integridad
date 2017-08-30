package com.mrzolution.integridad.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.UserClientRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserClientServices {
	
	@Autowired
	UserClientRepository userClientRepository;
		
	public UserClient create(UserClient userClient) throws BadRequestException{
		log.info("UserClientServices create: {}", userClient.getName());
		
		
		log.info("UserClientServices created: {}", userClient.getId());
		
		return null;
	}
	
	public UserClient update (UserClient userClient) throws BadRequestException{
		log.info("UserClientServices update: {}", userClient.getName());
		
		log.info("UserClientServices updated: {}", userClient.getId());
		
		return null;
	}

	public Iterable<UserClient> getAllActivesLazy() {
		Iterable<UserClient> userClientList = userClientRepository.findByActive(true);
		userClientList.forEach(user->{user.setFatherListToNull(); user.setListsNull();});
		return userClientList;
	}

}

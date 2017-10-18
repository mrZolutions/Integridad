package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.ClientChildRepository;
import com.mrzolution.integridad.app.repositories.SubsidiaryChildRepository;
import com.mrzolution.integridad.app.repositories.SubsidiaryRepository;
import com.mrzolution.integridad.app.repositories.UserClientRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserClientServices {
	
	@Autowired
	UserClientRepository userClientRepository;
	@Autowired
	SubsidiaryRepository subsidiaryRepository;
	@Autowired
	SubsidiaryChildRepository subsidiaryChildRepository;
	@Autowired
	ClientChildRepository clientChildRepository;
		
	public UserClient create(UserClient userClient) throws BadRequestException{
		log.info("UserClientServices create: {}", userClient.getName());
		userClient.setDateCreated(new Date().getTime());
		userClient.setActive(true);
		
		List<Subsidiary> subsidiarieList = userClient.getSubsidiaries();
		userClient.setSubsidiaries(null);
		
		if(subsidiarieList == null || subsidiarieList.isEmpty()){
			throw new BadRequestException("Debe tener una sucursal por lo menos");
		}
		
		UserClient saved = userClientRepository.save(userClient);
		
		subsidiarieList.forEach(subsidiary->{
			subsidiary.setUserClient(saved);
			subsidiaryRepository.save(subsidiary);
			subsidiary.setUserClient(null);
		});
		
		log.info("UserClientServices created: {}", userClient.getId());
		
		saved.setSubsidiaries(subsidiarieList);
		return saved;
	}
	
	public UserClient update (UserClient userClient) throws BadRequestException{
		if(userClient.getId() == null){
			throw new BadRequestException("Invalid User Client");
		}
		log.info("UserClientServices update: {}", userClient.getName());
		Father<UserClient, Subsidiary> father = new Father<>(userClient, userClient.getSubsidiaries());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, subsidiaryChildRepository, subsidiaryRepository);
        fatherUpdateChildren.updateChildren();

        log.info("UserClientServices CHILDREN updated: {}", userClient.getId());
        
        userClient.setListsNull();
        UserClient updated = userClientRepository.save(userClient);
		log.info("UserClientServices updated id: {}", updated.getId());
		return updated;
	}

	public Iterable<UserClient> getAllActivesLazy() {
		Iterable<UserClient> userClientList = userClientRepository.findByActive(true);
		userClientList.forEach(user->{user.setFatherListToNull(); user.setListsNull();});
		return userClientList;
	}

	public UserClient getById(UUID id) {
		log.info("UserClientServices getById: {}", id);
		UserClient retrieved = userClientRepository.findOne(id);
		if(retrieved != null){
			log.info("UserClientServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("UserClientServices retrieved id NULL: {}", id);
		}
		
		populateChildren(retrieved);
		return retrieved;
	}
	
	public Integer getNumberOfClients(UUID userClientId) {
		log.info("UserClientServices getNumberOfClients: {}", userClientId);
		UserClient userClient = userClientRepository.findOne(userClientId);
		Iterable<UUID> ids = clientChildRepository.findByFather(userClient);
		return Iterables.size(ids);
	}

	private void populateChildren(UserClient userClient) {
		log.info("UserClientServices populateChildren userClientId: {}", userClient.getId());
		List<Subsidiary> subsidiaryList = new ArrayList<>();
		Iterable<Subsidiary> subsidairies = subsidiaryRepository.findByUserClient(userClient);
		
		subsidairies.forEach(subsidiary -> {
			subsidiary.setListsNull();
			subsidiary.setFatherListToNull();
			subsidiary.setUserClient(null);
			
			subsidiaryList.add(subsidiary);
		});
		
		userClient.setSubsidiaries(subsidiaryList);
		userClient.setFatherListToNull();
		log.info("UserClientServices populateChildren FINISHED userClientId: {}", userClient.getId());
		
	}

}
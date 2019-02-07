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
	SubsidiaryServices subsidiaryServices;
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
		
		List<Subsidiary> subsidiariesList = userClient.getSubsidiaries();
		userClient.setSubsidiaries(null);
		
		if(subsidiariesList == null || subsidiariesList.isEmpty()){
			throw new BadRequestException("Debe tener una sucursal por lo menos");
		}
		
		UserClient saved = userClientRepository.save(userClient);
		
		subsidiariesList.forEach(subsidiary -> {
			subsidiary.setUserClient(saved);
			subsidiaryServices.createSubsidiary(subsidiary);
			subsidiary.setUserClient(null);
		});
		
		log.info("UserClientServices created: {}", saved.getId());
		
		saved.setSubsidiaries(subsidiariesList);
		return saved;
	}
	
	public UserClient update (UserClient userClient) throws BadRequestException{
		if(userClient.getId() == null){
			throw new BadRequestException("Invalid User Client");
		}
		log.info("UserClientServices update: {}", userClient.getName());
		userClient.getSubsidiaries().forEach(subsidiary -> {
			subsidiary.setUserClient(userClient);
			if(subsidiary.getId() == null){
				subsidiaryServices.createSubsidiary(subsidiary);
			} else {
				subsidiaryServices.updateSubsidiary(subsidiary);
			}
		});

        log.info("UserClientServices CHILDREN updated: {}", userClient.getId());
        
        userClient.setListsNull();
        UserClient updated = userClientRepository.save(userClient);
		log.info("UserClientServices updated id: {}", updated.getId());
		return updated;
	}

	public Iterable<UserClient> getAllActivesLazy() {
		Iterable<UserClient> userClientList = userClientRepository.findByActive(true);
		userClientList.forEach(user -> {user.setFatherListToNull(); user.setListsNull();});
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
		List<Subsidiary> subsidiaryList = new ArrayList<>();
		Iterable<Subsidiary> subsidairies = subsidiaryServices.getAllActivesByUserClientId(userClient.getId());

		subsidairies.forEach(subsidiary -> {
			subsidiary.setUsers(null);
			subsidiary.setFatherListToNull();
			subsidiary.setUserClient(null);
			
			subsidiaryList.add(subsidiary);
		});
		
		userClient.setSubsidiaries(subsidiaryList);
		//***** Se setea en null porque los proveedores y cuentas contables se los maneja aparte de los userClient/Empresas
		userClient.setCuentaContables(null);
		userClient.setProviders(null);
		userClient.setFatherListToNull();
	}

}

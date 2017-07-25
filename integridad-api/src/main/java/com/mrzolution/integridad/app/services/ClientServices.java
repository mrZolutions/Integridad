package com.mrzolution.integridad.app.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.ClientRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientServices {
	
	@Autowired
	ClientRepository clientRepository;
	
	public Client create(Client client){
		log.info("ClientServices create: {}", client.getName());
		Client saved = clientRepository.save(client);
		log.info("ClientServices created id: {}", saved.getId());
		return saved;
	}
	
	public Client update(Client client) throws BadRequestException{
		if(client.getId() == null){
			throw new BadRequestException("Invalid Client");
		}
		log.info("ClientServices update: {}", client.getName());
		client.setListsNull();
		Client updated = clientRepository.save(client);
		log.info("ClientServices update id: {}", updated.getId());
		return updated;
	}
	
	public Client getById(UUID id){
		log.info("ClientServices getById: {}", id);
		Client retrieved = clientRepository.findOne(id);
		if(retrieved != null){
			log.info("ClientServices retrieved id: {}", retrieved.getId());
		} else {
			log.info("ClientServices retrieved id NULL: {}", id);
		}
		
		populateChildren(retrieved);
		return retrieved;
	}
	
	public Iterable<Client> getAll(){
		log.info("ClientServices getAll");
		Iterable<Client> clients = clientRepository.findAll();
		for (Client client : clients) {
			populateChildren(client);
		}
		log.info("ClientServices getAll size retrieved: {}", Iterables.size(clients));
		return clients;
	}
	
	private void populateChildren(Client client) {
		log.info("ClientServices populateChildren clientId: {}", client.getId());
		log.info("ClientServices populateChildren FINISHED clientId: {}", client.getId());
	}

}

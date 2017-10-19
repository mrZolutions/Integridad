package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ClientServices;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/client")
public class ClientController {

	@Autowired
	ClientServices service;
	

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Client client){
		log.info("ClientController create: {}", client);
		Client response = null;
		try {
			response = service.create(client);
		}catch(BadRequestException e) {
			log.error("ClientController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Client>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Client client){
		log.info("ClientController update: {}", client);
		Client response = null;
		try {
			service.update(client);
		}catch(BadRequestException e) {
			log.error("ClientController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getLazy(){
		log.info("ClientController getLazy");
		Iterable<Client> response = null;
		try {
			response = service.getAllLazy();
		}catch(BadRequestException e) {
			log.error("ClientController getLazy Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/lazy/{id}")
	public ResponseEntity getLazyByUserClient(@PathVariable("id") UUID userClientId){
		log.info("ClientController getLazyByUserClient id:{}", userClientId);
		Iterable<Client> response = null;
		try {
			response = service.getAllLazyByUserClientid(userClientId);
		}catch(BadRequestException e) {
			log.error("ClientController getLazyByUserClient Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}
	
	
}

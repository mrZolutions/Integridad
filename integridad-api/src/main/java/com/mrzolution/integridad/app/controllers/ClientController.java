package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ClientServices;

import lombok.extern.slf4j.Slf4j;

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
			log.error("UserTypeController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Client>(response, HttpStatus.CREATED);
	}
	
	
}

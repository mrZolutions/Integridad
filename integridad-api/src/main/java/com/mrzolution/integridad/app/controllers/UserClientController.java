package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.UserClientServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/project")
public class UserClientController {

	@Autowired
	UserClientServices service;
	

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody UserClient userClient){
		log.info("UserClientController create: {}", userClient);
		UserClient response = null;
		try {
			response = service.create(userClient);
		}catch(BadRequestException e) {
			log.error("UserClientController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<UserClient>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody UserClient userClient){
		log.info("UserClientController update: {}", userClient);
		UserClient response = null;
		try {
			response = service.update(userClient);
		}catch(BadRequestException e) {
			log.error("UserClientController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<UserClient>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getLazy(){
		log.info("UserClientController getLazy");
		Iterable<UserClient> response = null;
		try {
			response = service.getAllActivesLazy();
		}catch(BadRequestException e) {
			log.error("UserClientController getLazy Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}
	
	
}

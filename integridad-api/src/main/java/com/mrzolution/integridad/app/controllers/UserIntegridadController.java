package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.UserIntegridadServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/user")
public class UserIntegridadController {
	
	@Autowired
	UserIntegridadServices service;

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody UserIntegridad userIntegridad){
		log.info("UserIntegridadController create: {}", userIntegridad);
		UserIntegridad response = null;
		try {
			response = service.create(userIntegridad);
		}catch(BadRequestException e) {
			log.error("UserIntegridadController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<UserIntegridad>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/auth")
    public ResponseEntity authenticate(@RequestBody UserIntegridad userIntegridad){
		log.info("UserIntegridadController authenticate: {}", userIntegridad);
		UserIntegridad response = null;
		try {
			response = service.authenticate(userIntegridad);
		}catch(BadRequestException e) {
			log.info("UserIntegridadController authenticate Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	    }
		return new ResponseEntity<UserIntegridad>(response, HttpStatus.OK);
	}
}

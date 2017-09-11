package com.mrzolution.integridad.app.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody UserIntegridad userIntegridad){
		log.info("UserIntegridadController update: {}", userIntegridad.getId());
		UserIntegridad response = null;
		try {
			response = service.update(userIntegridad);
		}catch(BadRequestException e) {
			log.error("UserIntegridadController update Exception thrown: {}", e.getMessage());	    
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
	
	@RequestMapping(method = RequestMethod.PUT, value="/{id}/{validation}")
    public ResponseEntity activate(@PathVariable("id") UUID id, @PathVariable("validation") String validation){
		log.info("UserIntegridadController activate: {}", id);
		UserIntegridad response = null;
		try {
			response = service.activate(id, validation);
		}catch(BadRequestException e) {
			log.info("UserIntegridadController activate Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<UserIntegridad>(response, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/recover")
    public ResponseEntity recoverPass(@RequestBody UserIntegridad userIntegridad){
		String email = userIntegridad.getEmail();
		log.info("UserIntegridadController recoverPass: {}", email);
		UserIntegridad response = null;
		try {
			response = service.recoverPassword(email);
		}catch(BadRequestException e) {
			log.info("UserIntegridadController recoverPass Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<UserIntegridad>(response, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getAllActivesLazy(){
		log.info("UserIntegridadController getAllActivesLazy");
		Iterable<UserIntegridad> response = null;
		try {
			response = service.getAllActivesLazy();
		}catch(BadRequestException e) {
			log.info("UserIntegridadController recoverPass Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.OK);
	}
}

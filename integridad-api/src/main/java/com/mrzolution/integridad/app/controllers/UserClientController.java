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
	UserClient response = null;
	try {
            response = service.create(userClient);
	} catch (BadRequestException e) {
            log.error("UserClientController create Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<UserClient>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody UserClient userClient){
	UserClient response = null;
	try {
            response = service.update(userClient);
	} catch (BadRequestException e) {
            log.error("UserClientController create Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<UserClient>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getLazy(){
	Iterable<UserClient> response = null;
	try {
            response = service.getAllActivesLazy();
	} catch (BadRequestException e) {
            log.error("UserClientController getLazy Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/clients_number/{userClientId}")
    public ResponseEntity getNumberOfClients(@PathVariable(value = "userClientId") UUID userClientId){
	Integer response = null;
	try {
            response = service.getNumberOfClients(userClientId);
	} catch (BadRequestException e) {
            log.error("UserClientController getLazy Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Integer>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value = "/{userClientId}")
    public ResponseEntity getById(@PathVariable(value = "userClientId") UUID userClientId) {
	UserClient response = null;
	try {
            response = service.getById(userClientId);
	} catch (BadRequestException e) {
            log.error("UserClientController getById Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<UserClient>(response, HttpStatus.OK);
    }
	
}
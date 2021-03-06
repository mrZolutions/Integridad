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

import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.UserTypeServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/user_type")
public class UserTypeController {
    @Autowired
    UserTypeServices service;
	

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody UserType userType){
	UserType response = null;
	try {
            response = service.create(userType);
	} catch (BadRequestException e) {
            log.error("UserTypeController create Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("UserTypeController create DONE");
	return new ResponseEntity<UserType>(response, HttpStatus.CREATED);
    }
		
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody UserType userType){
	UserType response = null;
	try {
            response = service.update(userType);
	} catch (BadRequestException e) {
            log.info("UserTypeController update Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserTypeController update DONE");
	return new ResponseEntity<UserType>(response, HttpStatus.OK);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getById(@PathVariable("id") UUID id){
	UserType response = null;
	try {
            response = service.getById(id);
	} catch (BadRequestException e) {
            log.info("UserTypeController getById Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserTypeController getById DONE");
	return new ResponseEntity<UserType>(response, HttpStatus.OK);
    }
	
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(){
	Iterable<UserType>  response = null;
	try {
            response = service.getAll();
	} catch (BadRequestException e) {
            log.info("UserTypeController getAll Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserTypeController getAll DONE");
	return new ResponseEntity<Iterable<UserType>>(response, HttpStatus.OK);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getAllLazy(){
	Iterable<UserType>  response = null;
	try {
            response = service.getAllLazy();
	} catch (BadRequestException e) {
            log.info("UserTypeController getAll Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserTypeController getAllLazy DONE");
	return new ResponseEntity<Iterable<UserType>>(response, HttpStatus.OK);
    }
}
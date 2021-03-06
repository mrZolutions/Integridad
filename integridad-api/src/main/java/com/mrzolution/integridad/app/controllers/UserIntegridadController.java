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
    public ResponseEntity create(@RequestBody UserIntegridad userIntegridad) {
	UserIntegridad response = null;
	try {
            response = service.create(userIntegridad);
	} catch (BadRequestException e) {
            log.error("UserIntegridadController create Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("UserIntegridadController create DONE");
	return new ResponseEntity<UserIntegridad>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody UserIntegridad userIntegridad) {
	UserIntegridad response = null;
	try {
            response = service.update(userIntegridad);
	} catch (BadRequestException e) {
            log.error("UserIntegridadController update Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserIntegridadController update DONE");
	return new ResponseEntity<UserIntegridad>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.POST, value="/auth")
    public ResponseEntity authenticate(@RequestBody UserIntegridad userIntegridad) {
	log.info("UserIntegridadController authenticate {}", userIntegridad.getEmail());
        UserIntegridad response = null;
	try {
            response = service.authenticate(userIntegridad);
	} catch (Exception e) {
            log.info("UserIntegridadController authenticate Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
	return new ResponseEntity<UserIntegridad>(response, HttpStatus.OK);
    }
	
    @RequestMapping(method = RequestMethod.PUT, value="/{id}/{validation}")
    public ResponseEntity activate(@PathVariable("id") UUID id, @PathVariable("validation") String validation) {
	UserIntegridad response = null;
	try {
            response = service.activate(id, validation);
	} catch (BadRequestException e) {
            log.info("UserIntegridadController activate Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserIntegridadController activate DONE");
	return new ResponseEntity<UserIntegridad>(response, HttpStatus.OK);
    }
	
    @RequestMapping(method = RequestMethod.POST, value="/recover")
    public ResponseEntity recoverPass(@RequestBody UserIntegridad userIntegridad) {
	String email = userIntegridad.getEmail();
	UserIntegridad response = null;
	try {
            response = service.recoverPassword(email);
	} catch (BadRequestException e) {
            log.info("UserIntegridadController recoverPass Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserIntegridadController recoverPass DONE");
	return new ResponseEntity<UserIntegridad>(response, HttpStatus.OK);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getAllActivesLazy() {
	Iterable<UserIntegridad> response = null;
	try {
            response = service.getAllActivesLazy();
	} catch (BadRequestException e) {
            log.info("UserIntegridadController getAllActivesLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("UserIntegridadController getAllActivesLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value="/lazy/bosses/{id}/{code}")
    public ResponseEntity getAllActivesBosesLazy(@PathVariable("id") UUID idSubsidiary, @PathVariable("code") String code) {
	Iterable<UserIntegridad> response = null;
	try {
            response = service.getByCodeTypeAndSubsidiaryIdActivesLazy(code, idSubsidiary);
	} catch (BadRequestException e) {
            log.info("UserIntegridadController getAllActivesBosesLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("UserIntegridadController getAllActivesBosesLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.OK);
    }
}
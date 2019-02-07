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
    public ResponseEntity createClient(@RequestBody Client client) {
	log.info("ClientController createClient: {}", client);
	Client response = null;
	try {
            response = service.createClient(client);
	} catch (BadRequestException e) {
            log.error("ClientController createClient Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Client>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateClient(@RequestBody Client client) {
	log.info("ClientController updateClient: {}", client);
	Client response = null;
	try {
            service.updateClient(client);
	} catch (BadRequestException e) {
            log.error("ClientController updateClient Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getAllClientActives() {
	log.info("ClientController getAllClientActives");
	Iterable<Client> response = null;
	try {
            response = service.getAllClientActives();
	} catch (BadRequestException e) {
            log.error("ClientController getAllClientActives Exception thrown: {}", e.getMessage());	    
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
	} catch (BadRequestException e) {
            log.error("ClientController getLazyByUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
	
}

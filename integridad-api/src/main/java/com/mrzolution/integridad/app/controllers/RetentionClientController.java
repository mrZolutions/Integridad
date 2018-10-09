package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.RetentionClientServices;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mrzolutions-daniel
 */

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/retenclient")
public class RetentionClientController {
    @Autowired
    RetentionClientServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getById(@PathVariable("id") UUID id){
        log.info("RetentionClientController getId: {}", id);
	RetentionClient response = null;
	try {
            response = service.getById(id);
	}catch(BadRequestException e) {
            log.error("RetentionClientController getId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<RetentionClient>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody RetentionClient retentionClient){
	log.info("RetentionClientController create: {}", retentionClient.getDocumentNumber());
	RetentionClient response = null;
	try {
            response = service.create(retentionClient);
	}catch(BadRequestException e) {
            log.error("RetentionClientController create Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
            return new ResponseEntity<RetentionClient>(response, HttpStatus.CREATED);
    }
}

package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.RetentionClient;
import com.mrzolution.integridad.app.domain.report.RetentionClientReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.RetentionClientServices;
import java.util.List;
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
    public ResponseEntity getById(@PathVariable("id") UUID id) {
        log.info("RetentionClientController getId: {}", id);
	RetentionClient response = null;
	try {
            response = service.getRetentionClientById(id);
	} catch (BadRequestException e) {
            log.error("RetentionClientController getId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<RetentionClient>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createRetentionClient(@RequestBody RetentionClient retentionClient) {
	log.info("RetentionClientController createRetentionClient: {}", retentionClient.getDocumentNumber());
	RetentionClient response = null;
	try {
            response = service.createRetentionClient(retentionClient);
	} catch (BadRequestException e) {
            log.error("RetentionClientController createRetentionClient Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<RetentionClient>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/retenclient/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getRetentionClientByUserClientIdAndDates(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
	log.info("RetentionClientController getRetentionClientByUserClientIdAndDates: {}, {}, {}", userClientId, dateOne, dateTwo);
	List<RetentionClientReport> response = null;
	try {
            response = service.getRetentionClientByUserClientIdAndDates(userClientId, dateOne, dateTwo);
	} catch (BadRequestException e) {
            log.error("RetentionClientController getRetentionClientByUserClientIdAndDates Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}
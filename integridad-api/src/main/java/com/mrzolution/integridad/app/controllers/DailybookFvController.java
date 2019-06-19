package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DailybookFv;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DailybookFvServices;
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
 * @author daniel-one
 */

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/dailyfv")
public class DailybookFvController {
    @Autowired
    DailybookFvServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookFvById(@PathVariable("id") UUID id) {
        DailybookFv response = null;
	try {
            response = service.getDailybookFvById(id);
	} catch (BadRequestException e) {
            log.error("DailybookFvController getDailybookFvById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DailybookFvController getDailybookFvById DONE");
        return new ResponseEntity<DailybookFv>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/client/{id}")
    public ResponseEntity getDailybookFvByClientId(@PathVariable("id") UUID id) {
        Iterable<DailybookFv> response = null;
        try {
            response = service.getDailybookFvByClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookFvController getDailybookFvByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookFvController getDailybookFvByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookFvByUserClientId(@PathVariable("id") UUID id) {
        Iterable<DailybookFv> response = null;
        try {
            response = service.getDailybookFvByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookFvController getDailybookFvByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookFvController getDailybookFvByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/noclient/{id}")
    public ResponseEntity getDailybookFvByUserClientIdWithNoClient(@PathVariable("id") UUID id) {
        Iterable<DailybookFv> response = null;
        try {
            response = service.getDailybookFvByUserClientIdWithNoClient(id);
        } catch (BadRequestException e) {
            log.error("DailybookFvController getDailybookFvByUserClientIdWithNoProvider Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookFvController getDailybookFvByUserClientIdWithNoClient DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{clientId}/{billNumber}")
    public ResponseEntity getDailybookFvByUserClientIdAndClientIdAndBillNumber(@PathVariable("userClientId") UUID userClientId, @PathVariable("clientId") UUID clientId, @PathVariable("billNumber") String billNumber) {
        Iterable<DailybookFv> response = null;
        try {
            response = service.getDailybookFvByUserClientIdAndClientIdAndBillNumber(userClientId, clientId, billNumber);
        } catch (BadRequestException e) {
            log.error("DailybookFvController getDailybookFvByUserClientIdAndClientIdAndBillNumber Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookFvController getDailybookFvByUserClientIdAndClientIdAndBillNumber DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDailybookFv(@RequestBody DailybookFv dailybookFv) {
        DailybookFv response = null;
	try {
            response = service.createDailybookFv(dailybookFv);
	} catch (BadRequestException e) {
            log.error("DailybookFvController createDailybookFv Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DailybookFvController createDailybookFv DONE");
        return new ResponseEntity<DailybookFv>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDailybookFv(@RequestBody DailybookFv dailybookFv) {
        try {
            service.deactivateDailybookFv(dailybookFv);
        } catch (BadRequestException e) {
            log.error("DailybookFvController deactivateDailybookFv Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookFvController deactivateDailybookFv DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
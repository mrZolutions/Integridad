package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DailybookCxP;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DailybookCxPServices;
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
@RequestMapping(value = "/integridad/v1/dailycxp")
public class DailybookCxPController {
    @Autowired
    DailybookCxPServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookCxPById(@PathVariable("id") UUID id) {
        DailybookCxP response = null;
	try {
            response = service.getDailybookCxPById(id);
	} catch (BadRequestException e) {
            log.error("DailybookCxPController getDailybookCxPById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DailybookCxPController getDailybookCxPById DONE");
        return new ResponseEntity<DailybookCxP>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getDailybookCxPByProviderId(@PathVariable("id") UUID id) {
        Iterable<DailybookCxP> response = null;
        try {
            response = service.getDailybookCxPByProviderId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCxPController getDailybookCxPByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCxPController getDailybookCxPByProviderId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookCxPByUserClientId(@PathVariable("id") UUID id) {
        Iterable<DailybookCxP> response = null;
        try {
            response = service.getDailybookCxPByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCxPController getDailybookCxPByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCxPController getDailybookCxPByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{provId}/{billNumber}")
    public ResponseEntity getDailybookCxPByUserClientIdAndProvIdAndBillNumber(@PathVariable("userClientId") UUID userClientId, @PathVariable("provId") UUID provId, @PathVariable("billNumber") String billNumber) {
        Iterable<DailybookCxP> response = null;
        try {
            response = service.getDailybookCxPByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        } catch (BadRequestException e) {
            log.error("DailybookCxPController getDailybookCxPByUserClientIdAndProvIdAndBillNumber Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCxPController getDailybookCxPByUserClientIdAndProvIdAndBillNumber DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDailybookCxP(@RequestBody DailybookCxP dailybookCxP) {
        DailybookCxP response = null;
	try {
            response = service.createDailybookCxP(dailybookCxP);
	} catch (BadRequestException e) {
            log.error("DailybookCxPController createDailybookCxP Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DailybookCxPController createDailybookCxP DONE");
        return new ResponseEntity<DailybookCxP>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDailybookCxP(@RequestBody DailybookCxP dailybookCxP) {
        try {
            service.deactivateDailybookCxP(dailybookCxP);
        } catch (BadRequestException e) {
            log.error("DailybookCxPController deactivateDailybookCxP Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCxPController deactivateDailybookCxP DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
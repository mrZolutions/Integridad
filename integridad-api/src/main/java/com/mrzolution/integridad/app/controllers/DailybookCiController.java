package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DailybookCi;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DailybookCiServices;
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
@RequestMapping(value = "/integridad/v1/contable/dailyci")
public class DailybookCiController {
    @Autowired
    DailybookCiServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookCiById(@PathVariable("id") UUID id) {
        log.info("DailybookCiController getDailybookCiById: {}", id);
	DailybookCi response = null;
	try {
            response = service.getDailybookCiById(id);
	} catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCi>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getDailybookCiByProviderId(@PathVariable("id") UUID id) {
        log.info("DailybookCiController getDailybookCiByProviderId: {}", id);
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByProviderId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookCiByUserClientId(@PathVariable("id") UUID id) {
        log.info("DailybookCiController getDailybookCiByUserClientId: {}", id);
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/noprovider/{id}")
    public ResponseEntity getDailybookCiByUserClientIdWithNoProvider(@PathVariable("id") UUID id) {
        log.info("DailybookCiController getDailybookCiByUserClientIdWithNoProvider: {}", id);
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByUserClientIdWithNoProvider(id);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByUserClientIdWithNoProvider Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{provId}/{billNumber}")
    public ResponseEntity getDailybookCiByUserClientIdAndProvIdAndBillNumber(@PathVariable("userClientId") UUID userClientId, @PathVariable("provId") UUID provId, @PathVariable("billNumber") String billNumber) {
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByUserClientIdAndProvIdAndBillNumber Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCiController getDailybookCiByUserClientIdAndProvIdAndBillNumber DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDailybookCi(@RequestBody DailybookCi dailybookCi) {
	DailybookCi response = null;
	try {
            response = service.createDailybookCi(dailybookCi);
	} catch (BadRequestException e) {
            log.error("DailybookCiController createDailybookCi Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCi>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDailybookCi(@RequestBody DailybookCi dailybookCi) {
        try {
            service.deactivateDailybookCi(dailybookCi);
        } catch (BadRequestException e) {
            log.error("DailybookCiController deactivateDailybookCi Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCiController deactivateDailybookCi DONE: {}", dailybookCi.getId());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
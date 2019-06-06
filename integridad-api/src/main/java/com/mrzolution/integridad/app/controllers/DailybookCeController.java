package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DailybookCeServices;
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
@RequestMapping(value = "/integridad/v1/contable/dailyce")
public class DailybookCeController {
    @Autowired
    DailybookCeServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookCeById(@PathVariable("id") UUID id) {
        DailybookCe response = null;
	try {
            response = service.getDailybookCeById(id);
	} catch (BadRequestException e) {
            log.error("DailybookCeController getDailybookCeById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCe>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getDailybookCeByProviderId(@PathVariable("id") UUID id) {
        Iterable<DailybookCe> response = null;
        try {
            response = service.getDailybookCeByProviderId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCeController getDailybookCeByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookCeByUserClientId(@PathVariable("id") UUID id) {
        Iterable<DailybookCe> response = null;
        try {
            response = service.getDailybookCeByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCeController getDailybookCeByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/noprovider/{id}")
    public ResponseEntity getDailybookCeByUserClientIdWithNoProvider(@PathVariable("id") UUID id) {
        Iterable<DailybookCe> response = null;
        try {
            response = service.getDailybookCeByUserClientIdWithNoProvider(id);
        } catch (BadRequestException e) {
            log.error("DailybookCeController getDailybookCeByUserClientIdWithNoProvider Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{provId}/{billNumber}")
    public ResponseEntity getDailybookCeByUserClientIdAndProvIdAndBillNumber(@PathVariable("userClientId") UUID userClientId, @PathVariable("provId") UUID provId, @PathVariable("billNumber") String billNumber) {
        Iterable<DailybookCe> response = null;
        try {
            response = service.getDailybookCeByUserClientIdAndProvIdAndBillNumber(userClientId, provId, billNumber);
        } catch (BadRequestException e) {
            log.error("DailybookCeController getDailybookCeByUserClientIdAndProvIdAndBillNumber Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDailybookCe(@RequestBody DailybookCe dailybookCe) {
	DailybookCe response = null;
	try {
            response = service.createDailybookCe(dailybookCe);
	} catch (BadRequestException e) {
            log.error("DailybookCeController createDailybookCe Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCe>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDailybookCe(@RequestBody DailybookCe dailybookCe) {
        try {
            service.deactivateDailybookCe(dailybookCe);
        } catch (BadRequestException e) {
            log.error("DailybookCeController deactivateDailybookCe Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
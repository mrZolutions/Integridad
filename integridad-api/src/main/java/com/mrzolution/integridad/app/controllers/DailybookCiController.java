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
@RequestMapping(value = "/integridad/v1/dailyci")
public class DailybookCiController {
    @Autowired
    DailybookCiServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookCiById(@PathVariable("id") UUID id) {
        DailybookCi response = null;
	try {
            response = service.getDailybookCiById(id);
	} catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DailybookCiController getDailybookCiById DONE");
        return new ResponseEntity<DailybookCi>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/client/{id}")
    public ResponseEntity getDailybookCiByClientId(@PathVariable("id") UUID id) {
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCiController getDailybookCiByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookCiByUserClientId(@PathVariable("id") UUID id) {
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCiController getDailybookCiByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/noclient/{id}")
    public ResponseEntity getDailybookCiByUserClientIdWithNoClient(@PathVariable("id") UUID id) {
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByUserClientIdWithNoClient(id);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByUserClientIdWithNoProvider Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCiController getDailybookCiByUserClientIdWithNoClient DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{clientId}/{billNumber}")
    public ResponseEntity getDailybookCiByUserClientIdAndClientIdAndBillNumber(@PathVariable("userClientId") UUID userClientId, @PathVariable("clientId") UUID clientId, @PathVariable("billNumber") String billNumber) {
        Iterable<DailybookCi> response = null;
        try {
            response = service.getDailybookCiByUserClientIdAndClientIdAndBillNumber(userClientId, clientId, billNumber);
        } catch (BadRequestException e) {
            log.error("DailybookCiController getDailybookCiByUserClientIdAndClientIdAndBillNumber Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCiController getDailybookCiByUserClientIdAndClientIdAndBillNumber DONE");
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
        log.info("DailybookCiController createDailybookCi DONE");
        return new ResponseEntity<DailybookCi>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.POST, value="/asin")
    public ResponseEntity createDailybookAsinCi(@RequestBody DailybookCi dailybookCi) {
        DailybookCi response = null;
	try {
            response = service.createDailybookAsinCi(dailybookCi);
	} catch (BadRequestException e) {
            log.error("DailybookCiController createDailybookAsinCi Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DailybookCiController createDailybookAsinCi DONE");
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
        log.info("DailybookCiController deactivateDailybookCi DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
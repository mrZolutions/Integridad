package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.SwimmingPool;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.SwimmingPoolServices;
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
@RequestMapping(value = "/integridad/v1/swimm")
public class SwimmingPoolController {
    @Autowired
    SwimmingPoolServices service;
        
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getSwimmPoolById(@PathVariable("id") UUID id) {
        SwimmingPool response = null;
        try {
            response = service.getSwimmPoolById(id);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController getSwimmPoolById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController getSwimmPoolById DONE");
        return new ResponseEntity<SwimmingPool>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/{subId}/{dateOne}/{dateTwo}")
    public ResponseEntity getSwimmPoolActivesBySubIdAndDates(@PathVariable("subId") UUID subId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        Iterable<SwimmingPool> response = null;
        try {
            response = service.getSwimmPoolActivesBySubIdAndDates(subId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController getSwimmPoolBySubIdAndDates Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController getSwimmPoolBySubIdAndDates DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/barcode/active/{id}/{barCode}")
    public ResponseEntity getSwimmPoolBySubIdAndBarCodeActive(@PathVariable("id") UUID id, @PathVariable("barCode") String barCode) {
        SwimmingPool response = null;
        try {
            response = service.getSwimmPoolBySubIdAndBarCodeActive(id, barCode);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController getSwimmPoolByBarCodeActive Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController getSwimmPoolByBarCodeActive DONE");
        return new ResponseEntity<SwimmingPool>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/barcode/all/{id}/{barCode}")
    public ResponseEntity getSwimmPoolBySubIdAndBarCodeAll(@PathVariable("id") UUID id, @PathVariable("barCode") String barCode) {
        SwimmingPool response = null;
        try {
            response = service.getSwimmPoolBySubIdAndBarCodeAll(id, barCode);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController getSwimmPoolByBarCodeAll Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController getSwimmPoolByBarCodeAll DONE");
        return new ResponseEntity<SwimmingPool>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createSwimmPool(@RequestBody SwimmingPool swimmPool) {
        SwimmingPool response = null;
        try {
            response = service.createSwimmPool(swimmPool);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController createSwimmPool Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController createSwimmPool DONE");
        return new ResponseEntity<SwimmingPool>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{id}/{barCode}")
    public ResponseEntity validateSwimmPool(@PathVariable("id") UUID id, @PathVariable("barCode") String barCode) {
        try {
            service.validateSwimmPool(id, barCode);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController validateSwimmPool Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController validateSwimmPool DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateSwimmPool(@RequestBody SwimmingPool swimmPool) {
        try {
            service.deactivateSwimmPool(swimmPool);
        } catch (BadRequestException e) {
            log.error("SwimmingPoolController deactivateSwimmPool Exception thrown: {}", e.getMessage());
    	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("SwimmingPoolController deactivateSwimmPool DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.report.CellarEntryReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CellarServices;
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
@RequestMapping(value = "/integridad/v1/cellar")
public class CellarController {
    @Autowired
    CellarServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getCellarById(@PathVariable("id") UUID id) {
        Cellar response = null;
        try {
            response = service.getCellarById(id);
	} catch (BadRequestException e) {
            log.error("CellarController getCellarById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController getCellarById DONE");
        return new ResponseEntity<Cellar>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createCellar(@RequestBody Cellar cellar) {
        Cellar response = null;
        try {
            response = service.createCellar(cellar);
	} catch (BadRequestException e) {
            log.error("CellarController createCellar Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController createCellar DONE");
        return new ResponseEntity<Cellar>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity validateCellar(@RequestBody Cellar cellar) {
        Cellar response = null;
        try {
            response = service.validateCellar(cellar);
        } catch (BadRequestException e) {
            log.error("CellarController validateCellar Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController validateCellar DONE");
        return new ResponseEntity<Cellar>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/warehouse/pending/{id}")
    public ResponseEntity getCellarsPendingOfWarehouse(@PathVariable("id") UUID id) {
        Iterable<Cellar> response = null;
        try {
            response = service.getCellarPendingOfWarehouse(id);
        } catch (BadRequestException e) {
            log.error("CellarController getCellarsPendingByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController getCellarsPendingOfWarehouse DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getCellarsByProviderId(@PathVariable("id") UUID id) {
        Iterable<Cellar> response = null;
        try {
            response = service.getCellarsByProviderId(id);
        } catch (BadRequestException e) {
            log.error("CellarController getCellarsByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController getCellarsByProviderId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{billNum}")
    public ResponseEntity getByUserClientIdAndBillNumberActive(@PathVariable("userClientId") UUID userClientId, @PathVariable("billNum") String billNum) {
        Iterable<Cellar> response = null;
        try {
            response = service.getByUserClientIdAndBillNumberActive(userClientId, billNum);
        } catch (BadRequestException e) {
            log.error("CellarController getByUserClientIdAndBillNumberActive Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController getByUserClientIdAndBillNumberActive DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/nocrednot/{id}")
    public ResponseEntity getCellarsByProviderIdAndNoCN(@PathVariable("id") UUID id) {
        Iterable<Cellar> response = null;
        try {
            response = service.getCellarsByProviderIdAndNoCN(id);
        } catch (BadRequestException e) {
            log.error("CellarController getCellarsByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController getCellarsByProviderId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<CellarEntryReport> response = null;
        try {
            response = service.getByUserClientIdAndDatesActives(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("CellarController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CellarController getByUserClientIdAndDatesActives DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}
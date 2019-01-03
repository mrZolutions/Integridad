package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CellarServices;
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
    public ResponseEntity getAllCellarById(@PathVariable("id") UUID id) {
        log.info("CellarController getId: {}", id);
        Cellar response = null;
        try {
            response = service.getCellarById(id);
	} catch (BadRequestException e) {
            log.error("CellarController getId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Cellar>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Cellar cellar) {
        Cellar response = null;
        try {
            response = service.create(cellar);
	} catch (BadRequestException e) {
            log.error("CellarController create Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Cellar>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity dactivateCellar(@RequestBody Cellar cellar) {
        log.info("CellarController dactivate: {}", cellar.getId());
        try {
            service.deactivateCellar(cellar);
        } catch (BadRequestException e) {
            log.error("CellarController dactivate Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
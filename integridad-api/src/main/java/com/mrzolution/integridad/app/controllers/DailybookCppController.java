package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DailybookCpp;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DailybookCppServices;
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
@RequestMapping(value = "/integridad/v1/contable/dailycpp")
public class DailybookCppController {
    @Autowired
    DailybookCppServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookCppById(@PathVariable("id") UUID id) {
        log.info("DailybookCppController getDailybookCppById: {}", id);
	DailybookCpp response = null;
	try {
            response = service.getDailybookCgById(id);
	} catch (BadRequestException e) {
            log.error("DailybookCppController getDailybookCppById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCpp>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookCppByUserClientId(@PathVariable("id") UUID id) {
        log.info("DailybookCgController getDailybookCppByUserClientId: {}", id);
        Iterable<DailybookCpp> response = null;
        try {
            response = service.getDailybookCgByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCppController getDailybookCppByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDailybookCpp(@RequestBody DailybookCpp dailybookCpp) {
	DailybookCpp response = null;
	try {
            response = service.createDailybookCg(dailybookCpp);
	} catch (BadRequestException e) {
            log.error("DailybookCppController createDailybookCpp Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCpp>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDailybookCpp(@RequestBody DailybookCpp dailybookCpp) {
        try {
            service.deactivateDailybookCpp(dailybookCpp);
        } catch (BadRequestException e) {
            log.error("DailybookCppController deactivateDailybookCpp Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCppController deactivateDailybookCpp DONE: {}", dailybookCpp.getId());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}

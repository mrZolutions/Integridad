package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DailybookCgServices;
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
@RequestMapping(value = "/integridad/v1/contable/dailycg")
public class DailybookCgController {
    @Autowired
    DailybookCgServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getDailybookCgById(@PathVariable("id") UUID id) {
        log.info("DailybookCgController getDailybookCgById: {}", id);
	DailybookCg response = null;
	try {
            response = service.getDailybookCgById(id);
	} catch (BadRequestException e) {
            log.error("DailybookCgController getDailybookCgById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCg>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getDailybookCgByUserClientId(@PathVariable("id") UUID id) {
        log.info("DailybookCgController getDailybookCgByUserClientId: {}", id);
        Iterable<DailybookCg> response = null;
        try {
            response = service.getDailybookCgByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DailybookCgController getDailybookCgByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDailybookCg(@RequestBody DailybookCg dailybookCg) {
	DailybookCg response = null;
	try {
            response = service.createDailybookCg(dailybookCg);
	} catch (BadRequestException e) {
            log.error("DailybookCgController createDailybookCg Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DailybookCg>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDailybookCg(@RequestBody DailybookCg dailybookCg) {
        try {
            service.deactivateDailybookCg(dailybookCg);
        } catch (BadRequestException e) {
            log.error("DailybookCgController deactivateDailybookCg Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DailybookCgController deactivateDailybookCg DONE: {}", dailybookCg.getId());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
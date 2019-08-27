package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.ComprobantePago;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ComprobantePagoServices;
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
@RequestMapping(value = "/integridad/v1/comppago")
public class ComprobantePagoController {
    @Autowired
    ComprobantePagoServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getComprobantePagoById(@PathVariable("id") UUID id) {
        ComprobantePago response = null;
	try {
            response = service.getComprobantePagoById(id);
	} catch (BadRequestException e) {
            log.error("ComprobantePagoController getComprobantePagoById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ComprobantePagoController getComprobantePagoById DONE");
        return new ResponseEntity<ComprobantePago>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getComprobantePagoByClientId(@PathVariable("id") UUID id) {
        Iterable<ComprobantePago> response = null;
        try {
            response = service.getComprobantePagoByProviderId(id);
        } catch (BadRequestException e) {
            log.error("ComprobantePagoController getComprobantePagoByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ComprobantePagoController getComprobantePagoByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getComprobantePagoByUserClientId(@PathVariable("id") UUID id) {
        Iterable<ComprobantePago> response = null;
        try {
            response = service.getComprobantePagoByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("ComprobantePagoController getComprobantePagoByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ComprobantePagoController getComprobantePagoByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createComprobantePago(@RequestBody ComprobantePago comprobantePago) {
        ComprobantePago response = null;
	try {
            response = service.createComprobantePago(comprobantePago);
	} catch (BadRequestException e) {
            log.error("ComprobantePagoController createComprobantePago Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ComprobantePagoController createComprobantePago DONE");
        return new ResponseEntity<ComprobantePago>(response, HttpStatus.CREATED);
    }
}
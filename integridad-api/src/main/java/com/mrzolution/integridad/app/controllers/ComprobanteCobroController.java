package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.ComprobanteCobro;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ComprobanteCobroServices;
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
@RequestMapping(value = "/integridad/v1/compcobro")
public class ComprobanteCobroController {
    @Autowired
    ComprobanteCobroServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getComprobanteCobroById(@PathVariable("id") UUID id) {
        ComprobanteCobro response = null;
	try {
            response = service.getComprobanteCobroById(id);
	} catch (BadRequestException e) {
            log.error("ComprobanteCobroController getComprobanteCobroById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ComprobanteCobroController getComprobanteCobroById DONE");
        return new ResponseEntity<ComprobanteCobro>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/bill/{numb}/userclient/{id}")
    public ResponseEntity getComprobanteCobroByBillNumberAndUserClient(@PathVariable("numb") String billNumber, @PathVariable("id") UUID userClientId ) {
        ComprobanteCobro response = null;
        try {
            response = service.getComprobanteCobroByBillNumberAndUserClient(billNumber, userClientId);
        } catch (BadRequestException e) {
            log.error("ComprobanteCobroController getComprobanteCobroByBillNumberAndUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ComprobanteCobroController getComprobanteCobroByBillNumberAndUserClient DONE");
        return new ResponseEntity<ComprobanteCobro>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/client/{id}")
    public ResponseEntity getComprobanteCobroByClientId(@PathVariable("id") UUID id) {
        Iterable<ComprobanteCobro> response = null;
        try {
            response = service.getComprobanteCobroByClientId(id);
        } catch (BadRequestException e) {
            log.error("ComprobanteCobroController getComprobanteCobroByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ComprobanteCobroController getComprobanteCobroByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getComprobanteCobroByUserClientId(@PathVariable("id") UUID id) {
        Iterable<ComprobanteCobro> response = null;
        try {
            response = service.getComprobanteCobroByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("ComprobanteCobroController getComprobanteCobroByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ComprobanteCobroController getComprobanteCobroByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createComprobanteCobro(@RequestBody ComprobanteCobro comprobanteCobro) {
        ComprobanteCobro response = null;
	try {
            response = service.createComprobanteCobro(comprobanteCobro);
	} catch (BadRequestException e) {
            log.error("ComprobanteCobroController createComprobanteCobro Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ComprobanteCobroController createComprobanteCobro DONE");
        return new ResponseEntity<ComprobanteCobro>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateComprobanteCobro(@RequestBody ComprobanteCobro comprobanteCobro) {
        try {
            service.deactivateComprobanteCobro(comprobanteCobro);
        } catch (BadRequestException e) {
            log.error("ComprobanteCobroController deactivateComprobanteCobro Exception thrown: {}", e.getMessage());
    	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ComprobanteCobroController deactivateComprobanteCobro DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
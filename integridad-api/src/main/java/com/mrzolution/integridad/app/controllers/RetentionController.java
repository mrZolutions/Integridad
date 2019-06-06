package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Retention;
import com.mrzolution.integridad.app.domain.report.RetentionReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.RetentionServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/retention")
public class RetentionController {
    @Autowired
    RetentionServices service;

    @RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
    public ResponseEntity getDatil(@RequestBody com.mrzolution.integridad.app.domain.eretention.Retention requirement, @PathVariable("id") UUID userClientId) {
	String response = null;
	try {
            response = service.getDatil(requirement, userClientId);
	} catch(Exception e) {
            log.error("RetentionController getDatil Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getRetentionById(@PathVariable("id") UUID id) {
	Retention response = null;
	try {
            response = service.getRetentionById(id);
	} catch(BadRequestException e) {
            log.error("RetentionController getRetentionById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Retention>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/retention/provider/{id}")
    public ResponseEntity getAllRetentionsByProviderId(@PathVariable("id") UUID id) {
        Iterable<Retention> response = null;
        try {
            response = service.getRetentionByProviderId(id);
        } catch (BadRequestException e) {
            log.error("RetentionController getAllRetentionsByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/retention/provider/docnum/{id}/{seq}")
    public ResponseEntity getRetentionByProviderIdAndDocumentNumber(@PathVariable("id") UUID id, @PathVariable("seq") String documentNumber) {
        Iterable<Retention> response = null;
        try {
            response = service.getRetentionByProviderIdAndDocumentNumber(id, documentNumber);
        } catch (BadRequestException e) {
            log.error("RetentionController getRetentionByProviderIdAndDocumentNumber Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createRetention(@RequestBody Retention retention) {
	Retention response = null;
	try {
            response = service.createRetention(retention);
	} catch(BadRequestException e) {
            log.error("RetentionController createRetention Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Retention>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/rep/retentions/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getAllByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
	List<RetentionReport> response = null;
	try {
            response = service.getAllByUserClientIdAndDates(userClientId, dateOne, dateTwo);
	} catch(BadRequestException e) {
            log.error("RetentionController getAllByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateRetention(@RequestBody Retention retention) {
        try {
            service.deactivateRetention(retention);
        } catch (BadRequestException e) {
            log.error("RetentionController deactivateRetention Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
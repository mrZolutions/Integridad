package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Retention;
import com.mrzolution.integridad.app.domain.ebill.Requirement;
import com.mrzolution.integridad.app.domain.report.RetentionReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BillServices;
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
	log.info("RetentionController getAllDatil");
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
    public ResponseEntity getById(@PathVariable("id") UUID id) {
	log.info("RetentionController getId: {}", id);
	Retention response = null;
	try {
            response = service.getById(id);
	} catch(BadRequestException e) {
            log.error("RetentionController getId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Retention>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/retention/provider/{id}")
    public ResponseEntity getAllRetentionsByProviderId(@PathVariable("id") UUID id) {
        log.info("RetentionController getAllRetentionsByProviderId: {}", id);
        Iterable<Retention> response = null;
        try {
            response = service.getRetentionByProviderId(id);
        } catch (BadRequestException e) {
            log.error("RetentionController getAllRetentionsByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Retention retention) {
	log.info("RetentionController create: {}", retention.getStringSeq());
	Retention response = null;
	try {
            response = service.create(retention);
	} catch(BadRequestException e) {
            log.error("RetentionController create Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<Retention>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/rep/retentions/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getAllByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
	log.info("RetentionController getByUserClientIdAndDatesActives: {}, {}, {}", userClientId, dateOne, dateTwo);
	List<RetentionReport> response = null;
	try {
            response = service.getAllBySubIdAndDates(userClientId, dateOne, dateTwo);
	} catch(BadRequestException e) {
            log.error("RetentionController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}

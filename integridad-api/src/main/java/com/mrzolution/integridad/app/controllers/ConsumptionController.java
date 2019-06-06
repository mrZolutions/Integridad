package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.report.CsmItemReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ConsumptionServices;
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
@RequestMapping(value = "/integridad/v1/consumption")
public class ConsumptionController {
    @Autowired
    ConsumptionServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getAllConsumptionById(@PathVariable("id") UUID id) {
        Consumption response = null;
        try {
            response = service.getConsumptionById(id);
	} catch (BadRequestException e) {
            log.error("ConsumptionController getAllConsumptionById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Consumption>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createConsumption(@RequestBody Consumption consumption) {
        Consumption response = null;
        try {
            response = service.createConsumption(consumption);
	} catch (BadRequestException e) {
            log.error("ConsumptionController createConsumption Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Consumption>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/client/{id}")
    public ResponseEntity getAllConsumptionByClientId(@PathVariable("id") UUID id) {
        Iterable<Consumption> response = null;
        try {
            response = service.getConsumptionByClientId(id);
	} catch (BadRequestException e) {
            log.error("ConsumptionController getAllConsumptionByClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<CsmItemReport> response = null;
        try {
            response = service.getByUserClientIdAndDatesActives(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("ConsumptionController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}
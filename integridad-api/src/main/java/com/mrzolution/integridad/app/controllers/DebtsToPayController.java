package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DebtsToPayServices;
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
@RequestMapping(value = "/integridad/v1/debts")
public class DebtsToPayController {
    @Autowired
    DebtsToPayServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getById(@PathVariable("id") UUID id) {
        log.info("DebtsToPayController getId: {}", id);
	DebtsToPay response = null;
	try {
            response = service.getById(id);
	} catch(BadRequestException e) {
            log.error("DebtsToPayController getId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<DebtsToPay>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/debts/provider/{id}")
    public ResponseEntity getAllDebtsByProviderId(@PathVariable("id") UUID id) {
        log.info("DebtsToPayController getAllDebtsByProviderId: {}", id);
        Iterable<DebtsToPay> response = null;
        try {
            response = service.getDebtsByProviderId(id);
        } catch(BadRequestException e) {
            log.error("DebtsToPayController getAllDebtsByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody DebtsToPay debtsToPay) {
	DebtsToPay response = null;
	try {
            response = service.create(debtsToPay);
	} catch(BadRequestException e) {
            log.error("DebtsToPayController create Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DebtsToPay>(response, HttpStatus.CREATED);
    }
}

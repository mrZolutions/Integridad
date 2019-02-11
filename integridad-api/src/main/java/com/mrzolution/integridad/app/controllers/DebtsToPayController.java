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
    public ResponseEntity getDebtsToPayById(@PathVariable("id") UUID id) {
        log.info("DebtsToPayController getDebtsToPayById: {}", id);
	DebtsToPay response = null;
	try {
            response = service.getDebtsToPayById(id);
	} catch (BadRequestException e) {
            log.error("DebtsToPayController getDebtsToPayById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DebtsToPay>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/debts/provider/{id}")
    public ResponseEntity getDebtsToPayByProviderId(@PathVariable("id") UUID id) {
        log.info("DebtsToPayController getDebtsByProviderId: {}", id);
        Iterable<DebtsToPay> response = null;
        try {
            response = service.getDebtsToPayByProviderId(id);
        } catch (BadRequestException e) {
            log.error("DebtsToPayController getDebtsByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createDebtsToPay(@RequestBody DebtsToPay debtsToPay) {
	DebtsToPay response = null;
	try {
            response = service.createDebtsToPay(debtsToPay);
	} catch (BadRequestException e) {
            log.error("DebtsToPayController createDebtsToPay Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<DebtsToPay>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateDebtsToPay(@RequestBody DebtsToPay debtsToPay) {
        try {
            service.deactivateDebtsToPay(debtsToPay);
        } catch (BadRequestException e) {
            log.error("DebtsToPayController deactivateDebtsToPay Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DebtsToPayController deactivateDebtsToPay DONE: {}", debtsToPay.getId());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}

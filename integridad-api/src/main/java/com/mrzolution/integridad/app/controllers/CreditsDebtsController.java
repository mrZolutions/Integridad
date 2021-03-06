package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.report.CreditsDebtsReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CreditsDebtsServices;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mrzolutions-daniel
 */

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/creditsdebts")
public class CreditsDebtsController {
    @Autowired
    CreditsDebtsServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/creditsdebts/debts/{id}")
    public ResponseEntity getCreditsDebtsByDebtsToPayId(@PathVariable("id") UUID id) {
        Iterable<CreditsDebts> response = null;
        try {
            response = service.getCreditsDebtsByDebtsToPayId(id);
        } catch (BadRequestException e) {
            log.error("CreditsDebtsController getCreditsDebtsByDebtsToPayId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CreditsDebtsController getCreditsDebtsByDebtsToPayId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/pendingreport/{userClientId}/{dateTwo}")
    public ResponseEntity getCreditsDebtsPendingOfDebtsToPayByUserClientId(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateTwo") long dateTwo) {
        List<CreditsDebtsReport> response = null;
        try {
            response = service.getCreditsDebtsPendingOfDebtsToPayByUserClientId(userClientId, dateTwo);
	} catch (BadRequestException e) {
            log.error("CreditsDebtsController getCreditsDebtsPendingOfDebtsToPayByUserClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CreditsDebtsController getCreditsDebtsPendingOfDebtsToPayByUserClientId DONE");
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
}
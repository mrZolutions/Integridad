package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.report.CreditsReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CreditsServices;
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
@RequestMapping(value = "/integridad/v1/creditsbybill")
public class CreditsController {
    @Autowired
    CreditsServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/credits/bill/{id}")
    public ResponseEntity getCreditsOfBillById(@PathVariable("id") UUID id) {
        Iterable<Credits> response = null;
        try {
            response = service.getCreditsOfBillByBillId(id);
        } catch (BadRequestException e) {
            log.error("CreditsControler getCreditsByBillById Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/pendingreport/{userClientId}/{dateTwo}")
    public ResponseEntity getCreditsPendingOfBillByUserClientId(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateTwo") long dateTwo) {
        List<CreditsReport> response = null;
        try {
            response = service.getCreditsPendingOfBillByUserClientId(userClientId, dateTwo);
	} catch (BadRequestException e) {
            log.error("CreditsControler getCreditsOfBillByUserClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
}   
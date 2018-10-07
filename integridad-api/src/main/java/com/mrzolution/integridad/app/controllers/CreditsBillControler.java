package com.mrzolution.integridad.app.controllers;
/**
 *
 * @author mrzolutions-daniel
 */
import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CreditsServices;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/creditsbybill")
public class CreditsBillControler {
    @Autowired
    CreditsServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/credits/bill/{id}")
    public ResponseEntity getAllCreditsOfBillById(@PathVariable("id") UUID id){
        log.info("CreditsBillControler getAllCreditsByBillById: {}", id);
        Iterable<Credits> response = null;
        try {
            response = service.getCreditsOfBillByBillId(id);
        }catch(BadRequestException e){
            log.error("CreditsBillControler getAllCreditsByBillById Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}   

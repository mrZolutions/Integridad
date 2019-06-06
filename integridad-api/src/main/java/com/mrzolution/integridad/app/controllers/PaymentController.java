package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Payment;
import com.mrzolution.integridad.app.domain.report.CCResumenReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.PaymentServices;
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
@RequestMapping(value = "/integridad/v1/payment")
public class PaymentController {
    @Autowired
    PaymentServices service;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createPayment(@RequestBody Payment payment) {
        Payment response = null;
        try {
            response = service.createPayment(payment);
	} catch (BadRequestException e) {
            log.error("PaymentController createPayment Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Payment>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/ccresreport/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getAllPaymentsByUserClientIdAndDates(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<CCResumenReport> response = null;
        try {
            response = service.getPaymentsByUserClientIdAndDates(userClientId, dateOne, dateTwo);
	} catch (BadRequestException e) {
            log.error("PaymentController getAllPaymentsByUserClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{banco}/{nrodoc}")
    public ResponseEntity getPaymentsByUserClientIdWithBankAndNroDocument(@PathVariable("userClientId") UUID userClientId, @PathVariable("banco") String banco, @PathVariable("nrodoc") String nrodoc) {
        Iterable<Payment> response = null;
        try {
            response = service.getPaymentsByUserClientIdWithBankAndNroDocument(userClientId, banco, nrodoc);
        } catch (BadRequestException e) {
            log.error("PaymentController getPaymentsByUserClientIdWithBankAndNroDocument Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
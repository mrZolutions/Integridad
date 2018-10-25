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
    public ResponseEntity create(@RequestBody Payment payment){
        log.info("PaymentController create");
        Payment response = null;
        try {
            response = service.create(payment);
	}catch(BadRequestException e){
            log.error("PaymentController create Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Payment>(response, HttpStatus.CREATED);
    };
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/ccresreport/{userClientId}")
    public ResponseEntity getAllPaymentsByUserClientId(@PathVariable("userClientId") UUID userClientId){
        log.info("PaymentController getAllPaymentsByUserClientId: {}", userClientId);
        List<CCResumenReport> response = null;
        try {
            response = service.getPaymentsByUserClientId(userClientId);
	}catch(BadRequestException e) {
	log.error("PaymentController getAllPaymentsByUserClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    };
}

package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.PaymentDebtsServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/integridad/v1/paymentdebts")
public class PaymentDebtsController {
    @Autowired
    PaymentDebtsServices service;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createPaymentDebts(@RequestBody PaymentDebts paymentDebts) {
        log.info("PaymentDebtsController createPaymentDebts");
        PaymentDebts response = null;
        try {
            response = service.createPaymentDebts(paymentDebts);
	} catch (BadRequestException e) {
            log.error("PaymentController createPaymentDebts Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<PaymentDebts>(response, HttpStatus.CREATED);
    }
}

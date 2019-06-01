package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.PaymentDebts;
import com.mrzolution.integridad.app.domain.report.CPResumenPaymentDebtsReport;
import com.mrzolution.integridad.app.domain.report.StatementProviderReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.PaymentDebtsServices;
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
            log.error("PaymentDebtsController createPaymentDebts Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<PaymentDebts>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/ccrespdreport/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getPaymentsDebtsByUserClientIdAndDates(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        log.info("PaymentDebtsController getPaymentsDebtsByUserClientIdAndDates: {}", userClientId);
        List<CPResumenPaymentDebtsReport> response = null;
        try {
            response = service.getPaymentsDebtsByUserClientIdAndDates(userClientId, dateOne, dateTwo);
	} catch (BadRequestException e) {
            log.error("PaymentDebtsController getPaymentsDebtsByUserClientIdAndDates Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/statement/{id}/{dateTwo}")
    public ResponseEntity getStatementProviderReport(@PathVariable("id") UUID id, @PathVariable("dateTwo") long dateTwo) {
        log.info("PaymentDebtsController getStatementProviderReport: {}", id);
        List<StatementProviderReport> response = null;
        try {
            response = service.getStatementProviderReport(id, dateTwo);
	} catch (BadRequestException e) {
            log.error("PaymentDebtsController getStatementProviderReport Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{banco}/{nrodoc}")
    public ResponseEntity getPaymentsDebtsByUserClientIdWithBankAndNroDocument(@PathVariable("userClientId") UUID userClientId, @PathVariable("banco") String banco, @PathVariable("nrodoc") String nrodoc) {
        Iterable<PaymentDebts> response = null;
        try {
            response = service.getPaymentsDebtsByUserClientIdWithBankAndNroDocument(userClientId, banco, nrodoc);
        } catch (BadRequestException e) {
            log.error("PaymentDebtsController getPaymentsDebtsByUserClientIdWithBankAndNroDocument Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
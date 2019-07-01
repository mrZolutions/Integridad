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
        PaymentDebts response = null;
        try {
            response = service.createPaymentDebts(paymentDebts);
	} catch (BadRequestException e) {
            log.error("PaymentDebtsController createPaymentDebts Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("PaymentDebtsController createPaymentDebts DONE");
        return new ResponseEntity<PaymentDebts>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/ccrespdreport/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getPaymentDebtsByUserClientIdAndDates(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<CPResumenPaymentDebtsReport> response = null;
        try {
            response = service.getPaymentDebtsByUserClientIdAndDates(userClientId, dateOne, dateTwo);
	} catch (BadRequestException e) {
            log.error("PaymentDebtsController getPaymentDebtsByUserClientIdAndDates Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("PaymentDebtsController getPaymentDebtsByUserClientIdAndDates DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/statement/{id}/{dateTwo}")
    public ResponseEntity getStatementProviderReport(@PathVariable("id") UUID id, @PathVariable("dateTwo") long dateTwo) {
        List<StatementProviderReport> response = null;
        try {
            response = service.getStatementProviderReport(id, dateTwo);
	} catch (BadRequestException e) {
            log.error("PaymentDebtsController getStatementProviderReport Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("PaymentDebtsController getStatementProviderReport");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{banco}/{nrodoc}")
    public ResponseEntity getPaymentDebtsByUserClientIdWithBankAndNroDocument(@PathVariable("userClientId") UUID userClientId, @PathVariable("banco") String banco, @PathVariable("nrodoc") String nrodoc) {
        Iterable<PaymentDebts> response = null;
        try {
            response = service.getPaymentDebtsByUserClientIdWithBankAndNroDocument(userClientId, banco, nrodoc);
        } catch (BadRequestException e) {
            log.error("PaymentDebtsController getPaymentDebtsByUserClientIdWithBankAndNroDocument Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("PaymentDebtsController getPaymentDebtsByUserClientIdWithBankAndNroDocument DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getPaymentsDebtsByProviderId(@PathVariable("id") UUID id) {
        Iterable<PaymentDebts> response = null;
        try {
            response = service.getPaymentDebtsByProviderId(id);
        } catch (BadRequestException e) {
            log.error("PaymentDebtsController getPaymentDebtsByProviderId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("PaymentDebtsController getPaymentsDebtsByProviderId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivatePaymentDebts(@RequestBody PaymentDebts paymentDebts) {
        try {
            service.deactivatePaymentDebts(paymentDebts);
        } catch (BadRequestException e) {
            log.error("PaymentDebtsController deactivatePaymentDebts Exception thrown: {}", e.getMessage());
    	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("PaymentDebtsController deactivatePaymentDebts DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
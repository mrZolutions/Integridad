package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CashierServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/cashier")
public class CashierController {
    @Autowired
    CashierServices service;

    @RequestMapping(method = RequestMethod.GET, value="/subsidiary/{id}")
    public ResponseEntity getBySubsidiaryActivesLazy(@PathVariable("id") UUID subsidiaryId) {
        Iterable<Cashier> response = null;
	try {
            response = service.getAllBySubsidiaryActivesLazy(subsidiaryId);
	} catch (BadRequestException e) {
            log.info("CashierController getBySubsidiaryActivesLazy Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CashierController getBySubsidiaryActivesLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateCashier(@RequestBody Cashier cashier) {
        try {
            service.updateCashier(cashier);
	} catch (BadRequestException e) {
            log.error("CashierController updateCashier Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CashierController updateCashier DONE");
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

}
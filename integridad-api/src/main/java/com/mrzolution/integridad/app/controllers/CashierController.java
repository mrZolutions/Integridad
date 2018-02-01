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
    public ResponseEntity getAllBySubsiduaryActivesLazy(@PathVariable("id") UUID subsidiaryId){
		log.info("CashierController getAllBySubsiduaryActivesLazy id: {}", subsidiaryId);
		Iterable<Cashier> response = null;
		try {
			response = service.getAllBySubsiduaryActivesLazy(subsidiaryId);
		}catch(BadRequestException e) {
			log.info("SubsidiaryController getByUserClientId Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Cashier cashier){
		log.info("CashierController update");
		try {
			service.update(cashier);
		}catch(BadRequestException e) {
			log.error("CashierController update Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}

}

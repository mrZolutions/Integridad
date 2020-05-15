package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.ModuleMenu;
import com.mrzolution.integridad.app.domain.ProcessCashier;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ModuleMenuServices;
import com.mrzolution.integridad.app.services.ProcessCashierServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/processCashier")
public class ProcessCashierController {
    @Autowired
    ProcessCashierServices services;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody ProcessCashier processCashier) {
        ProcessCashier response = null;
	try {
            response = services.save(processCashier);
	} catch (BadRequestException e) {
            log.error("ProcessCashierController save Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ProcessCashierController save DONE");
	return new ResponseEntity<ProcessCashier>(response, HttpStatus.ACCEPTED);
    }

}
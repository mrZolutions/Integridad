package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DetailCellar;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DetailCellarServices;
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
 * @author daniel-one
 */

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/cellar/details")
public class DetailCellarController {
    @Autowired
    DetailCellarServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/details/{id}")
    public ResponseEntity getDetailsOfCellarsByUserClientId(@PathVariable("id") UUID id) {
        Iterable<DetailCellar> response = null;
        try {
            response = service.getDetailsOfCellarsByUserClientId(id);
        } catch (BadRequestException e) {
            log.error("DetailCellarController getDetailsOfCellarsByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DetailCellarController getDetailsOfCellarsByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
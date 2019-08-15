package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DetailDailybookContabServices;
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
@RequestMapping(value = "/integridad/v1/dailybook/details")
public class DetailDailybookContabController {
    @Autowired
    DetailDailybookContabServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getCodeContaByDatesAndUserClientId(@PathVariable("code") String code, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo, @PathVariable("id") String id) {
        Iterable<DetailDailybookContab> response = null;
        try {
            response = service.getCodeContaByDatesAndUserClientId(code, dateOne, dateTwo, id);
        } catch (BadRequestException e) {
            log.error("DetailDailybookContabController getAllByUserClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DetailDailybookContabController getAllByUserClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
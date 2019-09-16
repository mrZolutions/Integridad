package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.report.KardexOfProductReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.KardexServices;
import java.util.List;
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
@RequestMapping(value = "/integridad/v1/product/kardex")
public class KardexController {
    @Autowired
    KardexServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="rep/{id}/{provID}/{dateOne}/{dateTwo}")
    public ResponseEntity getKardexActivesByUserClientIdAndProductIdAndDates(@PathVariable("id") String id, @PathVariable("provID") UUID provID, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<KardexOfProductReport> response = null;
        try {
            response = service.getKardexActivesByUserClientIdAndProductIdAndDates(id, provID, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("KardexController getKardexActivesByUserClientIdAndProductIdAndDates Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("KardexController getKardexActivesByUserClientIdAndProductIdAndDates DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
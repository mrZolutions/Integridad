package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.report.EspecificMajorReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DetailDailybookContabServices;
import java.util.List;
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
    
    @RequestMapping(method = RequestMethod.GET, value="rep/mayoresp/{id}/{code}/{dateOne}/{dateTwo}")
    public ResponseEntity getEspecificMajorReportByUserClientIdAndDates(@PathVariable("id") String id, @PathVariable("code") String code, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<EspecificMajorReport> response = null;
        try {
            response = service.getEspecificMajorReportByUserClientIdAndDates(id, code, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("DetailDailybookContabController getEspecificMajorReportByUserClientIdAndDates Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DetailDailybookContabController getEspecificMajorReportByUserClientIdAndDates DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
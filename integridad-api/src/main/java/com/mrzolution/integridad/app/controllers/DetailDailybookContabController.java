package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import com.mrzolution.integridad.app.domain.report.AllDailyReport;
import com.mrzolution.integridad.app.domain.report.EspecificMajorReport;
import com.mrzolution.integridad.app.domain.report.GeneralMajorReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.DetailDailybookContabServices;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity getEspfcMajorReportByUserClientIdAndDates(@PathVariable("id") String id, @PathVariable("code") String code, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<EspecificMajorReport> response = null;
        try {
            response = service.getEspfcMajorReportByUserClientIdAndDates(id, code, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("DetailDailybookContabController getEspecificMajorReportByUserClientIdAndDates Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DetailDailybookContabController getEspecificMajorReportByUserClientIdAndDates DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="rep/mayorgen/{id}/{codeOne}/{codeTwo}/{dateOne}/{dateTwo}")
    public ResponseEntity getGenMajorReportByUsrClntIdAndCodeContaAndDate(@PathVariable("id") String id, @PathVariable("codeOne") String codeOne, @PathVariable("codeTwo") String codeTwo, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<GeneralMajorReport> response = null;
        try {
            response = service.getGenMajorReportByUsrClntIdAndCodeContaAndDate(id, codeOne, codeTwo, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("DetailDailybookContabController getGenMajorReportByUsrClntIdAndCtaCtbleAndDate Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("DetailDailybookContabController getGenMajorReportByUsrClntIdAndCodeCtbleAndDate DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    //TODO reporte nuevo pedido por PABLO *********************************
    @RequestMapping(method = RequestMethod.GET, value="rep/all/{id}/{dateOne}/{dateTwo}")
    public ResponseEntity getAllDailyReportByUsrClntIdAndDate(@PathVariable("id") String id,@PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<AllDailyReport> response = null;
        try {
            response = service.getAllDailyReportByUserClntIdAndDate(id, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("DetailDailybookContabController getAllDailyReportByUsrClntIdAndDate Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DetailDailybookContabController getAllDailyReportByUsrClntIdAndDate DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, value="/{dailyId}/{type}")
    public ResponseEntity upsertDetails(@RequestBody List<DetailDailybookContab> details, @PathVariable("type") String type, @PathVariable("dailyId") String dailyId){
        UUID response = null;
        try{
            response = service.upsertDailyBooks(details, type, dailyId);
        } catch (BadRequestException e) {
            log.error("DetailDailybookContabController upsertDetails Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("DetailDailybookContabController upsertDetails DONE");
        return new ResponseEntity<UUID>(response, HttpStatus.CREATED);
    }
}
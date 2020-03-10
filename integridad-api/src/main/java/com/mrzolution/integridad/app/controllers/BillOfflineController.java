package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.ebill.RequirementBillOffline;
import com.mrzolution.integridad.app.domain.report.ItemOfflineReport;
import com.mrzolution.integridad.app.domain.report.SalesOfflineReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BillOfflineServices;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.services.ComprobanteCobroServices;
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
 * @author daniel-one
 */

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/billoffline")
public class BillOfflineController {
    @Autowired
    BillOfflineServices service;
    @Autowired
    ComprobanteCobroServices comprobanteCobroServices;
    
    @RequestMapping(method = RequestMethod.POST, value="/{typeDocument}" )
    public ResponseEntity createBillOffline(@RequestBody RequirementBillOffline requirement, @PathVariable("typeDocument") int typeDocument) {
        BillOffline response = null;
        try {
            response = service.createBillOffline(requirement.getBill(), typeDocument);
            comprobanteCobroServices.createComprobanteCobro(requirement.getComprobanteCobro());
        } catch (BadRequestException e) {
            log.error("BillOfflineController createBillOffline Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BillOfflineController createBillOffline DONE");
        return new ResponseEntity<BillOffline>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getBillOfflineById(@PathVariable("id") UUID id) {
        BillOffline response = null;
        try {
            response = service.getBillOfflineById(id);
        } catch (BadRequestException e) {
            log.error("BillOfflineController getBillOfflineById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillOfflineController getBillOfflineById DONE");
        return new ResponseEntity<BillOffline>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/seq/{seq}/{subId}")
    public ResponseEntity getBillsOfflineByStringSeqAndSubId(@PathVariable("seq") String stringSeq, @PathVariable("subId") UUID subId) {
        Iterable<BillOffline> response = null;
        try {
            response = service.getBillsOfflineByStringSeqAndSubId(stringSeq, subId);
        } catch (BadRequestException e) {
            log.error("BillOfflineController getBillsOfflineByStringSeq Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillOfflineController getBillsOfflineByStringSeqAndSubId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    //Selecciona todas las Facturas del Cliente
    @RequestMapping(method = RequestMethod.GET, value="/bill/client/{id}")
    public ResponseEntity getBillsOfflineByClientId(@PathVariable("id") UUID id) {
        Iterable<BillOffline> response = null;
        try {
            response = service.getBillsOfflineByClientId(id, 1);
        } catch (BadRequestException e) {
            log.error("BillOfflineController getBillsOfflineByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BillOfflineController getBillsOfflineByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/type_document/{value}")
    public ResponseEntity getBillsOfflineByTypeDocument(@PathVariable("value") int value) {
        Iterable<BillOffline> response =null;
        try {
            response = service.getBillsOfflineByTypeDocument(value);
        } catch (BadRequestException e) {
            log.error("BillOfflineController getBillsOfflineByTypeDocument Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillOfflineController getBillsOfflineByTypeDocument DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateBillOffline(@RequestBody BillOffline billOffline) {
        try {
            service.deactivateBillOffline(billOffline);
        } catch (BadRequestException e) {
            log.error("BillOfflineController deactivateBillOffline Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillOfflineController deactivateBillOffline DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
    
    //Reporte de Ventas Offline
    @RequestMapping(method = RequestMethod.GET, value="/rep/sales/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getByUserClientIdAndDates(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<SalesOfflineReport> response = null;
        try {
            response = service.getAllBySubIdAndDates(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("BillOfflineController getByUserClientIdAndDates Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillOfflineController getByUserClientIdAndDates DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    //Reporte de Productos Vendidos Offline
    @RequestMapping(method = RequestMethod.GET, value="/rep/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<ItemOfflineReport> response = null;
        try {
            response = service.getBySubIdAndDatesActives(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("BillOfflineController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillOfflineController getByUserClientIdAndDatesActives DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}
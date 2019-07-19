package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.ebill.Requirement;
import com.mrzolution.integridad.app.domain.report.CashClosureReport;
import com.mrzolution.integridad.app.domain.report.ItemReport;
import com.mrzolution.integridad.app.domain.report.SalesReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BillServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/bill")
public class BillController {
    @Autowired
    BillServices service;

    @RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
    public ResponseEntity getDatil(@RequestBody Requirement requirement, @PathVariable("id") UUID userClientId) {
        String response = null;
        try {
            response = service.getDatil(requirement, userClientId);
        } catch (Exception e) {
            log.error("BillController getDatil Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getDatil DONE");
        return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
    }
     
    //Selecciona todas las Facturas del Cliente
    @RequestMapping(method = RequestMethod.GET, value="/bill/client/{id}")
    public ResponseEntity getBillByClientId(@PathVariable("id") UUID id) {
        Iterable<Bill> response = null;
        try {
            response = service.getBillByClientId(id, 1);
        } catch (BadRequestException e) {
            log.error("BillController getBillByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BillController getBillByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    //Selecciona todas las Facturas del Cliente con Saldo != '0.00'
    @RequestMapping(method = RequestMethod.GET, value="/bill/client/saldo/{id}")
    public ResponseEntity getBillByClientIdWithSaldo(@PathVariable("id") UUID id) {
        Iterable<Bill> response = null;
        try {
            response = service.getBillByClientIdWithSaldo(id, 1);
        } catch (BadRequestException e) {
            log.error("BillController getBillByClientIdWithSaldo Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BillController getBillByClientIdWithSaldo DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
        
    @RequestMapping(method = RequestMethod.GET, value="/quotation/client/{id}")
    public ResponseEntity getQuotationByClientId(@PathVariable("id") UUID id) {
        Iterable<Bill> response = null;
        try {
            response = service.getBillByClientId(id, 0);
        } catch (BadRequestException e) {
            log.error("BillController getQuotationByClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getQuotationByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/seq/{seq}/{subId}")
    public ResponseEntity getBillByStringSeq(@PathVariable("seq") String stringSeq, @PathVariable("subId") UUID subId) {
        Iterable<Bill> response = null;
        try {
            response = service.getByStringSeqAndSubId(stringSeq, subId);
        } catch (BadRequestException e) {
            log.error("BillController getBillByStringSeq Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getBillByStringSeq DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/rep/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getByUserClientIdAndDatesActives(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<ItemReport> response = null;
        try {
            response = service.getBySubIdAndDatesActives(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("BillController getByUserClientIdAndDatesActives Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getByUserClientIdAndDatesActives DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    //Reporte de Ventas
    @RequestMapping(method = RequestMethod.GET, value="/rep/sales/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getByUserClientIdAndDates(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<SalesReport> response = null;
        try {
            response = service.getAllBySubIdAndDates(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("BillController getByUserClientIdAndDates Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getByUserClientIdAndDates DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getBillById(@PathVariable("id") UUID id) {
        Bill response = null;
        try {
            response = service.getBillById(id);
        } catch (BadRequestException e) {
            log.error("BillController getBillById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getBillById DONE");
        return new ResponseEntity<Bill>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, value="/{typeDocument}" )
    public ResponseEntity createBill(@RequestBody Bill bill, @PathVariable("typeDocument") int typeDocument) {
        Bill response = null;
        try {
            response = service.createBill(bill, typeDocument);
        } catch (BadRequestException e) {
            log.error("BillController createBill Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BillController createBill DONE");
        return new ResponseEntity<Bill>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity deactivateBill(@RequestBody Bill bill) {
        try {
            service.deactivateBill(bill);
        } catch (BadRequestException e) {
            log.error("BillController deactivateBill Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController deactivateBill DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
    
    //Reporte de Cierre de Caja
    @RequestMapping(method = RequestMethod.GET, value="/rep/closure/{userClientId}/{dateOne}/{dateTwo}")
    public ResponseEntity getForCashClosureReportAndDate(@PathVariable("userClientId") UUID userClientId, @PathVariable("dateOne") long dateOne, @PathVariable("dateTwo") long dateTwo) {
        List<CashClosureReport> response = null;
        try {
            response = service.getForCashClosureReport(userClientId, dateOne, dateTwo);
        } catch (BadRequestException e) {
            log.error("BillController getForCashClosureReportAndDate Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BillController getForCashClosureReportAndDate DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
}
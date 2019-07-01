package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CuentaContableServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/cuenta_contable")
public class CuentaContableController {  
    @Autowired
    CuentaContableServices service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createCuentaContable(@RequestBody CuentaContable cuentaContable) {
        CuentaContable response = null;
	try {
            response = service.createCuentaContable(cuentaContable);
	} catch(BadRequestException e) {
            log.error("CuentaContableController createCuentaContable Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CuentaContableController createCuentaContable DONE");
	return new ResponseEntity<CuentaContable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateCuentaContable(@RequestBody CuentaContable cuentaContable) {
        try {
            service.updateCuentaContable(cuentaContable);
        } catch (BadRequestException e) {
            log.error("CuentaContableController updateCuentaContable Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CuentaContableController updateCuentaContable DONE");
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getCuentaContableByUserClient(@PathVariable("id") UUID id) {
        Iterable<CuentaContable> response = null;
	try {
            response = service.getCuentaContableByUserClient(id);
	} catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CuentaContableController getCuentaContableByUserClient DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/nobank/{id}")
    public ResponseEntity getCuentaContableByUserClientNoBank(@PathVariable("id") UUID id) {
        Iterable<CuentaContable> response = null;
	try {
            response = service.getCuentaContableByUserClientNoBank(id);
	} catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByUserClientNoBank Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CuentaContableController getCuentaContableByUserClientNoBank DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/bank/{id}")
    public ResponseEntity getCuentaContableByUserClientAndBank(@PathVariable("id") UUID id) {
        Iterable<CuentaContable> response = null;
	try {
            response = service.getCuentaContableByUserClientAndBank(id);
	} catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByUserClientAndBank Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CuentaContableController getCuentaContableByUserClientAndBank DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/type/{id}/{typ}")
    public ResponseEntity getCuentaContableByType(@PathVariable("id") UUID id, @PathVariable("typ") String typ) {
        Iterable<CuentaContable> response = null;
        try {
            response = service.getCuentaContableByType(id, typ);
        } catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CuentaContableController getCuentaContableByType DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/cuenta/typeacc/{typ}/{atyp}")
    public ResponseEntity getCuentaContableByTypeAndAccountType(@PathVariable("typ") String typ, @PathVariable("atyp") String atyp) {
        Iterable<CuentaContable> response = null;
        try {
            response = service.getCuentaContableByTypeAndAccountType(typ, atyp);
        } catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByTypeAndAccountType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CuentaContableController getCuentaContableByTypeAndAccountType DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
}
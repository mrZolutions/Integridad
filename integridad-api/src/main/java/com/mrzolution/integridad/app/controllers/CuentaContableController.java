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
	log.info("CuentaContableController create");
	CuentaContable response = null;
	try {
            response = service.createCuentaContable(cuentaContable);
	} catch(BadRequestException e) {
            log.error("CuentaContableController create Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<CuentaContable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateCuentaContable(@RequestBody CuentaContable cuentaContable) {
        log.info("CuentaContableController updateCuentaContable: {}", cuentaContable);
        CuentaContable response = null;
        try {
            service.updateCuentaContable(cuentaContable);
        } catch (BadRequestException e) {
            log.error("CuentaContableController updateCuentaContable Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
  
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll() {
        Iterable<CuentaContable> result = null;
        try {
            result = service.getAll();
        } catch (BadRequestException e) {

        }
        return new ResponseEntity<Iterable>(result, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/lazy/client/{id}")
    public ResponseEntity getCuentaContableByUserClient(@PathVariable("id") UUID id) {
	log.info("CuentaContableController getCuentaContableByUserClient");
	Iterable<CuentaContable> response = null;
	try {
            response = service.getCuentaContableByUserClient(id);
	} catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/cuenta/type/{typ}")
    public ResponseEntity getCuentaContableByType(@PathVariable("typ") String typ) {
        log.info("CuentaContableController getCuentaContableByType");
        Iterable<CuentaContable> response = null;
        try {
            response = service.getCuentaContableByType(typ);
        } catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/cuenta/typeacc/{typ}/{atyp}")
    public ResponseEntity getCuentaContableByTypeAndAccountType(@PathVariable("typ") String typ, @PathVariable("atyp") String atyp) {
        log.info("CuentaContableController getCuentaContableByTypeAndAccountType");
        Iterable<CuentaContable> response = null;
        try {
            response = service.getCuentaContableByTypeAndAccountType(typ, atyp);
        } catch (BadRequestException e) {
            log.error("CuentaContableController getCuentaContableByTypeAndAccountType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
}

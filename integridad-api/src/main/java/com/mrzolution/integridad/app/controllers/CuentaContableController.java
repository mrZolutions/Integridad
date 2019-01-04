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
    public ResponseEntity create(@RequestBody CuentaContable cuentaContable) {
	log.info("CuentaContableController create");
	CuentaContable response = null;
	try {
            response = service.create(cuentaContable);
	} catch(BadRequestException e) {
            log.error("CuentaContableController create Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<CuentaContable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody CuentaContable cuentaContable) {
        log.info("CuentaContableController update: {}", cuentaContable);
        CuentaContable response = null;
        try {
            service.update(cuentaContable);
        } catch (BadRequestException e) {
            log.error("CuentaContableController create Exception thrown: {}", e.getMessage());
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
    public ResponseEntity getLazyByUserClient(@PathVariable("id") UUID id) {
	log.info("CuentaContableController getLazyByUserClient");
	Iterable<CuentaContable> response = null;
	try {
            response = service.getLazyByUserClient(id);
	} catch (BadRequestException e) {
            log.error("CuentaContableController getLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/cuenta/type/{typ}")
    public ResponseEntity getAllByType(@PathVariable("typ") String typ) {
        log.info("CuentaContableController getAllByType");
        Iterable<CuentaContable> response = null;
        try {
            response = service.getByType(typ);
        } catch (BadRequestException e) {
            log.error("CuentaContableController getAllByType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/cuenta/typeacc/{typ}/{atyp}")
    public ResponseEntity getAllTypeAndAccountType(@PathVariable("typ") String typ, @PathVariable("atyp") String atyp) {
        log.info("CuentaContableController getAllTypeAndAccountType");
        Iterable<CuentaContable> response = null;
        try {
            response = service.getByTypeAndAccountType(typ, atyp);
        } catch (BadRequestException e) {
            log.error("CuentaContableController getAllTypeAndAccountType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
}

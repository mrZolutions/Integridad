package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.CreditNoteCellar;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CreditNoteCellarServices;
import java.util.UUID;
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
@RequestMapping(value = "/integridad/v1/crednotcellar")
public class CreditNoteCellarController {
    @Autowired
    CreditNoteCellarServices service;
    
    //Creación de la Nota de Crédito
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createCreditNoteCellar(@RequestBody CreditNoteCellar creditNoteCellar) {
	CreditNoteCellar response = null;
	try {
            response = service.createCreditNoteCellar(creditNoteCellar);
	} catch (BadRequestException e) {
            log.error("CreditNoteCellarController createCreditNoteCellar Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CreditNoteCellarController createCreditNoteCellar DONE");
	return new ResponseEntity<CreditNoteCellar>(response, HttpStatus.CREATED);
    }
    
    //Selecciona todas las Notas de Crédito del Proveedor
    @RequestMapping(method = RequestMethod.GET, value="/provider/{id}")
    public ResponseEntity getCreditNotesCellarByProviderId(@PathVariable("id") UUID id) {
        Iterable<CreditNoteCellar> response = null;
        try {
            response = service.getCreditNotesCellarByProviderId(id);
        } catch (BadRequestException e) {
            log.error("CreditNoteCellarController getCreditNotesCellarByProviderId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CreditNoteCellarController getCreditNotesCellarByProviderId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    //Selecciona la Nota de Crédito por ID
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getCreditNoteCellarById(@PathVariable("id") UUID id) {
        CreditNoteCellar response = null;
        try {
            response = service.getCreditNoteCellarById(id);
        } catch (BadRequestException e) {
            log.error("CreditNoteCellarController getCreditNoteCellarById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CreditNoteCellarController getCreditNoteCellarById DONE");
        return new ResponseEntity<CreditNoteCellar>(response, HttpStatus.ACCEPTED);
    }
}
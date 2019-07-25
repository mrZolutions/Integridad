package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.CreditNote;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CreditNoteServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/creditnote")
public class CreditNoteController {
    @Autowired
    CreditNoteServices service;

    @RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
    public ResponseEntity getDatil(@RequestBody com.mrzolution.integridad.app.domain.ecreditNote.CreditNote requirement, @PathVariable("id") UUID userClientId) {
        String response = null;
	try {
            response = service.getDatil(requirement, userClientId);
	} catch (Exception e) {
            log.error("CreditNoteController getDatil Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CreditNoteController getDatil DONE");
	return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createCreditNote(@RequestBody CreditNote creditNote) {
	CreditNote response = null;
	try {
            response = service.createCreditNote(creditNote);
	} catch (BadRequestException e) {
            log.error("CreditNoteController createCreditNote Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CreditNoteController createCreditNote DONE");
	return new ResponseEntity<CreditNote>(response, HttpStatus.CREATED);
    }
    
    //Selecciona todas las Notas de Crédito del Cliente
    @RequestMapping(method = RequestMethod.GET, value="/client/{id}")
    public ResponseEntity getCreditNoteByClientId(@PathVariable("id") UUID id) {
        Iterable<CreditNote> response = null;
        try {
            response = service.getCreditNotesByClientId(id);
        } catch (BadRequestException e) {
            log.error("CreditNoteController getCreditNoteByClientId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("CreditNoteController getCreditNoteByClientId DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    //Selecciona la Nota de Crédito por ID
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getCreditNoteById(@PathVariable("id") UUID id) {
        CreditNote response = null;
        try {
            response = service.getCreditNoteById(id);
        } catch (BadRequestException e) {
            log.error("CreditNoteController getCreditNoteById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CreditNoteController getCreditNoteById DONE");
        return new ResponseEntity<CreditNote>(response, HttpStatus.ACCEPTED);
    }

}
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
@RequestMapping(value = "/integridad/v1/credit")
public class CreditController {
    @Autowired
    CreditNoteServices service;

    @RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
    public ResponseEntity getDatil(@RequestBody com.mrzolution.integridad.app.domain.ecreditNote.CreditNote requirement, @PathVariable("id") UUID userClientId) {
	log.info("CreditController getAllDatil");
	String response = null;
	try {
            response = service.getDatil(requirement, userClientId);
	} catch (Exception e) {
            log.error("CreditController getDatil Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody CreditNote creditNote) {
	log.info("CreditController create: {}", creditNote.getCreditSeq());
	CreditNote response = null;
	try {
            response = service.create(creditNote);
	} catch (BadRequestException e) {
            log.error("CreditController create Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<CreditNote>(response, HttpStatus.CREATED);
    }

}

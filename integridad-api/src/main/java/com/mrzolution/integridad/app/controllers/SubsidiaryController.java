package com.mrzolution.integridad.app.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.SubsidiaryServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/subsidiary")
public class SubsidiaryController {
    @Autowired
    SubsidiaryServices service;

    @RequestMapping(method = RequestMethod.GET, value="/user_client/{id}")
    public ResponseEntity getByUserClientId(@PathVariable("id") UUID userClientId) {
	log.info("SubsidiaryController getByUserClientId: {}", userClientId);
	Iterable<Subsidiary> response = null;
	try {
            response = service.getAllActivesByUserClientId(userClientId);
	} catch (BadRequestException e) {
            log.info("SubsidiaryController getByUserClientId Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getSubsidiaryById(@PathVariable("id") UUID id) {
	log.info("SubsidiaryController getSubsidiaryById: {}", id);
	Subsidiary response = null;
	try {
            response = service.getSubsidiaryById(id);
	} catch (BadRequestException e) {
            log.info("SubsidiaryController getSubsidiaryById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Subsidiary>(response, HttpStatus.OK);
    }
}

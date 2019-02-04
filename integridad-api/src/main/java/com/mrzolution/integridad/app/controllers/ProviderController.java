package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Provider;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ProviderServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/provider")
public class ProviderController {
    @Autowired
    ProviderServices service;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createProvider(@RequestBody Provider provider) {
	log.info("ProviderController createProvider");
	Provider response = null;
	try {
            response = service.createProvider(provider);
	} catch (BadRequestException e) {
            log.error("ProviderController createProvider Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Provider>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateProvider(@RequestBody Provider provider) {
	log.info("ProviderController updateProvider: {}", provider);
	Provider response = null;
	try {
            service.updateProvider(provider);
	}catch (BadRequestException e) {
            log.error("ProviderController updateProvider Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/lazy/client/{id}")
    public ResponseEntity getLazyByUserClient(@PathVariable("id") UUID id) {
	log.info("ProviderController getLazyByUserClient");
	Iterable<Provider> response = null;
	try {
            response = service.getLazyByUserClient(id);
	} catch (BadRequestException e) {
            log.error("ProviderController getLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getLazy() {
	log.info("ProviderController getLazy");
	Iterable<Provider> response = null;
	try {
            response = service.getAllLazy();
	} catch (BadRequestException e) {
            log.error("ProviderController getLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getProviderById(@PathVariable("id") UUID id) {
        log.info("ProviderController getProviderById:{}", id);
	Provider response = null;
	try {
            response = service.getProviderById(id);
	} catch (BadRequestException e) {
            log.error("ProviderController getProviderById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Provider>(response, HttpStatus.CREATED);
    }
}

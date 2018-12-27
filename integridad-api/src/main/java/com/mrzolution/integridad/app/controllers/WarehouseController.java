package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Warehouse;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.WarehouseServices;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mrzolutions-daniel
 */

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/warehouse")
public class WarehouseController {
    @Autowired
    WarehouseServices service;
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{id}")
    public ResponseEntity getAllWarehouseByUserClientId(@PathVariable("id") UUID id) {
        log.info("WarehouseController getAllWarehouseByUserClientId");
	Iterable<Warehouse> response = null;
	try {
            response = service.getWarehouseByUserClient(id);
	} catch (BadRequestException e) {
            log.error("WarehouseController getAllWarehouseByUserClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getAllWarehouseById(@PathVariable("id") UUID id) {
        log.info("WarehouseController getAllWarehouseById:{}", id);
	Warehouse response = null;
	try {
            response = service.getWarehouseById(id);
	} catch (BadRequestException e) {
            log.error("WarehouseController getAllWarehouseById Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Warehouse>(response, HttpStatus.CREATED);
    }
    
}

package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.SubGroup;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.SubGroupServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/sub_group")
public class SubGroupController {
    @Autowired
    SubGroupServices service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createSubGroup(@RequestBody SubGroup subGroup) {
	log.info("SubGroupController createSubGroup: {}", subGroup);
	SubGroup response = null;
	try {
            response = service.createSubGroup(subGroup);
	} catch (BadRequestException e) {
            log.error("SubGroupController createSubGroup Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<SubGroup>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateSubGroup(@RequestBody SubGroup subGroup) {
	log.info("SubGroupController updateSubGroup: {}", subGroup);
	try {
            service.updateSubGroup(subGroup);
	} catch (BadRequestException e) {
            log.error("SubGroupController updateSubGroup Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
	
    @RequestMapping(method = RequestMethod.DELETE, value = "/{subGrouplineId}")
    public ResponseEntity deleteSubGroup(@PathVariable("subGroupLineId") UUID subGroupId) {
	log.info("SubGroupController deleteSubGroup: {}", subGroupId);
	SubGroup response = null;
	try {
            response = service.deleteSubGroup(subGroupId);
	} catch (BadRequestException e) {
            log.error("SubGroupController deleteSubGroup Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<SubGroup>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives/{groupLineId}")
    public ResponseEntity getAllActivesByGroupLineId(@PathVariable("groupLineId") UUID groupLindeId) {
	log.info("SubGroupController getAllActivesByLineGroupId: {}", groupLindeId);
	Iterable<SubGroup> response = null;
	try {
            response = service.getAllActivesByGroupLineId(groupLindeId);
	} catch (BadRequestException e) {
            log.error("SubGroupController getAllActivesByLineId Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{lineId}")
    public ResponseEntity getAllActivesByGroupLineIdLazy(@PathVariable("lineId") UUID groupLlineId) {
        log.info("SubGroupController getAllActivesByGroupLineIdLazy: {}", groupLlineId);
	Iterable<SubGroup> response = null;
	try {
            response = service.getAllActivesByGroupLineIdLazy(groupLlineId);
	} catch (BadRequestException e) {
            log.error("SubGroupController getAllActivesByGroupLineIdLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

}
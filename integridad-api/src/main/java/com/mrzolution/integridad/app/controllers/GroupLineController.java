package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.GroupLineServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/group_line")
public class GroupLineController {
    @Autowired
    GroupLineServices service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createGroupLine(@RequestBody GroupLine groupLine) {
        GroupLine response = null;
	try {
            response = service.createGroupLine(groupLine);
	} catch (BadRequestException e) {
            log.error("GroupLineController createGroupLine Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("GroupLineController createGroupLine DONE");
	return new ResponseEntity<GroupLine>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateGroupLine(@RequestBody GroupLine groupLine) {
        try {
            service.updateGroupLine(groupLine);
	} catch (BadRequestException e) {
            log.error("GroupLineController update Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("GroupLineController updateGroupLine DONE");
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
	
    @RequestMapping(method = RequestMethod.DELETE, value = "/{grouplineId}")
    public ResponseEntity deleteGroupLine(@PathVariable("groupLineId") UUID groupLineId) {
        GroupLine response = null;
	try {
            response = service.deleteGroupLine(groupLineId);
	} catch (BadRequestException e) {
            log.error("GroupLineController deleteGroupLine Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("GroupLineController deleteGroupLine DONE");
	return new ResponseEntity<GroupLine>(response, HttpStatus.ACCEPTED);
    }

	
    @RequestMapping(method = RequestMethod.GET, value="/actives/{lineId}")
    public ResponseEntity getAllActivesByLineId(@PathVariable("lineId") UUID lineId) {
        Iterable<GroupLine> response = null;
	try {
            response = service.getAllActivesByLineId(lineId);
	} catch (BadRequestException e) {
            log.error("GroupLineController getAllActivesByLineId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("GroupLineController getAllActivesByLineId DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{lineId}")
    public ResponseEntity getAllActivesByProjectIdLazy(@PathVariable("lineId") UUID lineId) {
        Iterable<GroupLine> response = null;
	try {
            response = service.getAllActivesByLineIdLazy(lineId);
	} catch (BadRequestException e) {
            log.error("GroupLineController getAllActivesByLineIdLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("GroupLineController getAllActivesByLineIdLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
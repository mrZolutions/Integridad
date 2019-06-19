package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Line;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.LineServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/line")
public class LineController {
    @Autowired
    LineServices service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createLine(@RequestBody Line line) {
        Line response = null;
	try {
            response = service.createLine(line);
	} catch (BadRequestException e) {
            log.error("LineController createLine Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("LineController createLine DONE");
	return new ResponseEntity<Line>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateLine(@RequestBody Line line) {
        try {
            service.updateLine(line);
	} catch (BadRequestException e) {
            log.error("LineController updateLine Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("LineController updateLine DONE");
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
	
    @RequestMapping(method = RequestMethod.DELETE, value = "/{lineId}")
    public ResponseEntity deleteLine(@PathVariable("lineId") UUID lineId) {
        Line response = null;
	try {
            response = service.deleteLine(lineId);
	} catch (BadRequestException e) {
            log.error("LineController deleteLine Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("LineController deleteLine DONE");
	return new ResponseEntity<Line>(response, HttpStatus.ACCEPTED);
    }

	
    @RequestMapping(method = RequestMethod.GET, value="/actives/{projectId}")
    public ResponseEntity getAllActivesByProjectId(@PathVariable("projectId") UUID projectId) {
        Iterable<Line> response = null;
	try {
            response = service.getAllActivesByUserClientId(projectId);
	} catch (BadRequestException e) {
            log.error("LineController getAllActivesByProjectId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("LineController getAllActivesByProjectId DONE");
    	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{projectId}")
    public ResponseEntity getAllActivesByProjectIdLazy(@PathVariable("projectId") UUID projectId) {
        Iterable<Line> response = null;
	try {
            response = service.getAllActivesByUserClientIdLazy(projectId);
	} catch (BadRequestException e) {
            log.error("LineController getAllActivesByProjectIdLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("LineController getAllActivesByProjectIdLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
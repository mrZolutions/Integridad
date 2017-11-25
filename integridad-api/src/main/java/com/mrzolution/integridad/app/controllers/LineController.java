package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Line;
import com.mrzolution.integridad.app.domain.ProductType;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.LineServices;
import com.mrzolution.integridad.app.services.ProductTypeServices;
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
    public ResponseEntity create(@RequestBody Line line){
		log.info("LineController create: {}", line);
		Line response = null;
		try {
			response = service.create(line);
		}catch(BadRequestException e) {
			log.error("LineController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Line>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Line line){
		log.info("LineController update: {}", line);
		try {
			service.update(line);
		}catch(BadRequestException e) {
			log.error("LineController update Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{lineId}")
    public ResponseEntity delete(@PathVariable("lineId") UUID lineId){
		log.info("LineController delete: {}", lineId);
		Line response = null;
		try {
			response = service.delete(lineId);
		}catch(BadRequestException e) {
			log.error("LineController delete Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Line>(response, HttpStatus.ACCEPTED);
	}

	
	@RequestMapping(method = RequestMethod.GET, value="/actives/{projectId}")
    public ResponseEntity getAllActivesByProjectId(@PathVariable("projectId") UUID projectId){
		log.info("LineController getAllActivesByProjectId: {}", projectId);
		Iterable<Line> response = null;
		try {
			response = service.getAllActivesByUserClientId(projectId);
		}catch(BadRequestException e) {
			log.error("LineController getAllActivesByProjectId Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{projectId}")
	public ResponseEntity getAllActivesByProjectIdLazy(@PathVariable("projectId") UUID projectId){
		log.info("LineController getAllActivesByProjectIdLazy: {}", projectId);
		Iterable<Line> response = null;
		try {
			response = service.getAllActivesByUserClientIdLazy(projectId);
		}catch(BadRequestException e) {
			log.error("LineController getAllActivesByProjectIdLazy Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

}

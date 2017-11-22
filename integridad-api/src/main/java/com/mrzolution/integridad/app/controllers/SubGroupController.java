package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.GroupLine;
import com.mrzolution.integridad.app.domain.SubGroup;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.GroupLineServices;
import com.mrzolution.integridad.app.services.SubGroupServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/group_line")
public class SubGroupController {

	@Autowired
	SubGroupServices service;


	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody SubGroup subGroup){
		log.info("SubGroupController create: {}", subGroup);
		SubGroup response = null;
		try {
			response = service.create(subGroup);
		}catch(BadRequestException e) {
			log.error("SubGroupController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<SubGroup>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody SubGroup subGroup){
		log.info("SubGroupController update: {}", subGroup);
		try {
			service.update(subGroup);
		}catch(BadRequestException e) {
			log.error("SubGroupController update Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{subGrouplineId}")
    public ResponseEntity delete(@PathVariable("subGroupLineId") UUID subGroupId){
		log.info("SubGroupController delete: {}", subGroupId);
		SubGroup response = null;
		try {
			response = service.delete(subGroupId);
		}catch(BadRequestException e) {
			log.error("SubGroupController delete Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<SubGroup>(response, HttpStatus.ACCEPTED);
	}

	
	@RequestMapping(method = RequestMethod.GET, value="/actives/{groupLineId}")
    public ResponseEntity getAllActivesByGroupLineId(@PathVariable("groupLineId") UUID groupLindeId){
		log.info("SubGroupController getAllActivesByLineGroupId: {}", groupLindeId);
		Iterable<SubGroup> response = null;
		try {
			response = service.getAllActivesByGroupLineId(groupLindeId);
		}catch(BadRequestException e) {
			log.error("SubGroupController getAllActivesByLineId Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{lineId}")
	public ResponseEntity getAllActivesByGroupLineIdLazy(@PathVariable("lineId") UUID groupLlineId){
		log.info("SubGroupController getAllActivesByGroupLineIdLazy: {}", groupLlineId);
		Iterable<SubGroup> response = null;
		try {
			response = service.getAllActivesByGroupLineIdLazy(groupLlineId);
		}catch(BadRequestException e) {
			log.error("SubGroupController getAllActivesByGroupLineIdLazy Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

}

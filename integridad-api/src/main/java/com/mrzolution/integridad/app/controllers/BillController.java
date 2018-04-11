package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.ebill.Requirement;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BillServices;
import com.mrzolution.integridad.app.services.BrandServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/bill")
public class BillController {

	@Autowired
	BillServices service;

	@RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
	public ResponseEntity getDatil(@RequestBody Requirement requirement, @PathVariable("id") UUID userClientId){
		log.info("BillController getAllDatil");
		String response = null;
		try {
			response = service.getDatil(requirement, userClientId);
		}catch(Exception e) {
			log.error("BillController getDatil Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/client/{id}")
	public ResponseEntity getByClientId(@PathVariable("id") UUID id){
		log.info("BillController getByClientId: {}", id);
		Iterable<Bill> response = null;
		try {
			response = service.getByClientIdLazy(id);
		}catch(BadRequestException e) {
			log.error("BillController getByClientId Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/seq/{seq}/{subId}")
	public ResponseEntity getByStringSeq(@PathVariable("seq") String stringSeq, @PathVariable("subId") UUID subId){
		log.info("BillController getByStringSeq: {}, {}", stringSeq, subId);
		Iterable<Bill> response = null;
		try {
			response = service.getByStringSeqAndSubId(stringSeq, subId);
		}catch(BadRequestException e) {
			log.error("BillController getByStringSeq Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity getById(@PathVariable("id") UUID id){
		log.info("BillController getId: {}", id);
		Bill response = null;
		try {
			response = service.getById(id);
		}catch(BadRequestException e) {
			log.error("BillController getId Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Bill>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Bill bill){
		log.info("BillController create: {}", bill.getBillSeq());
		Bill response = null;
		try {
			response = service.create(bill);
		}catch(BadRequestException e) {
			log.error("BillController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Bill>(response, HttpStatus.CREATED);
	}

}

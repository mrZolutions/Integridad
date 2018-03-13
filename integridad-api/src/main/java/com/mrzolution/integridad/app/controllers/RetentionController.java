package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Retention;
import com.mrzolution.integridad.app.domain.ebill.Requirement;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BillServices;
import com.mrzolution.integridad.app.services.RetentionServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/retention")
public class RetentionController {

	@Autowired
	RetentionServices service;

	@RequestMapping(method = RequestMethod.POST, value="/clave_acceso/{id}")
	public ResponseEntity getDatil(@RequestBody com.mrzolution.integridad.app.domain.eretention.Retention requirement, @PathVariable("id") UUID userClientId){
		log.info("RetentionController getAllDatil");
		String response = null;
		try {
			response = service.getDatil(requirement, userClientId);
		}catch(Exception e) {
			log.error("RetentionController getDatil Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}

//	@RequestMapping(method = RequestMethod.GET, value="/client/{id}")
//	public ResponseEntity getByClientId(@PathVariable("id") UUID id){
//		log.info("RetentionController getByClientId: {}", id);
//		Iterable<Bill> response = null;
//		try {
//			response = service.getByClientIdLazy(id);
//		}catch(BadRequestException e) {
//			log.error("BillController getByClientId Exception thrown: {}", e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//	    }
//		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value="/seq/{seq}/{subId}")
//	public ResponseEntity getByStringSeq(@PathVariable("seq") String stringSeq, @PathVariable("subId") UUID subId){
//		log.info("BillController getByStringSeq: {}, {}", stringSeq, subId);
//		Iterable<Bill> response = null;
//		try {
//			response = service.getByStringSeqAndSubId(stringSeq, subId);
//		}catch(BadRequestException e) {
//			log.error("BillController getByStringSeq Exception thrown: {}", e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//		}
//		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
//	}

	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ResponseEntity getById(@PathVariable("id") UUID id){
		log.info("RetentionController getId: {}", id);
		Retention response = null;
		try {
			response = service.getById(id);
		}catch(BadRequestException e) {
			log.error("RetentionController getId Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Retention>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Retention retention){
		log.info("RetentionController create: {}", retention.getStringSeq());
		Retention response = null;
		try {
			response = service.create(bill);
		}catch(BadRequestException e) {
			log.error("BillController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Retention>(response, HttpStatus.CREATED);
	}
//
//	@RequestMapping(method = RequestMethod.PUT)
//    public ResponseEntity update(@RequestBody Brand brand){
//		log.info("BrandController update: {}", brand);
//		try {
//			service.update(brand);
//		}catch(BadRequestException e) {
//			log.error("BrandController update Exception thrown: {}", e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//	    }
//		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
//	}
//
//	@RequestMapping(method = RequestMethod.DELETE, value = "/{brandId}")
//    public ResponseEntity delete(@PathVariable("brandId") UUID brandId){
//		log.info("BrandController delete: {}", brandId);
//		Brand response = null;
//		try {
//			response = service.delete(brandId);
//		}catch(BadRequestException e) {
//			log.error("BrandController delete Exception thrown: {}", e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//	    }
//		return new ResponseEntity<Brand>(response, HttpStatus.ACCEPTED);
//	}

	

//
//	@RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{projectId}")
//	public ResponseEntity getAllActivesByProjectIdLazy(@PathVariable("projectId") UUID projectId){
//		log.info("BrandController getAllActivesByProjectIdLazy");
//		Iterable<Brand> response = null;
//		try {
//			response = service.getAllActivesLazy(projectId);
//		}catch(BadRequestException e) {
//			log.error("BrandController getActivesByProjectIdLazy Exception thrown: {}", e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//		}
//		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
//	}

}

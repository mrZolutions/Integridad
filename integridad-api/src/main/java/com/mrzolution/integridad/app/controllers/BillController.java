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

	@RequestMapping(method = RequestMethod.POST, value="/clave_acceso")
	public ResponseEntity getDatil(@RequestBody Requirement requirement){
		log.info("BillController getAllDatil");
		String response = null;
		try {
			response = service.getDatil(requirement);
		}catch(Exception e) {
			log.error("BillController getDatil Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
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

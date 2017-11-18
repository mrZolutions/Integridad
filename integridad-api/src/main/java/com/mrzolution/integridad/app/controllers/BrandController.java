package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.ProductType;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ProductTypeServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/product_type")
public class BrandController {

	@Autowired
	ProductTypeServices service;


	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody ProductType productType){
		log.info("ProductTypeController create: {}", productType);
		ProductType response = null;
		try {
			response = service.create(productType);
		}catch(BadRequestException e) {
			log.error("ProductTypeController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<ProductType>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody ProductType productType){
		log.info("ProductTypeController update: {}", productType);
		try {
			service.update(productType);
		}catch(BadRequestException e) {
			log.error("ProductTypeController update Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{productTypeId}")
    public ResponseEntity delete(@PathVariable("productTypeId") UUID productTypeId){
		log.info("ProductTypeController delete: {}", productTypeId);
		ProductType response = null;
		try {
			response = service.delete(productTypeId);
		}catch(BadRequestException e) {
			log.error("ProductTypeController delete Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<ProductType>(response, HttpStatus.ACCEPTED);
	}

	
	@RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getAllActives(){
		log.info("ProductTypeController getAllActives");
		Iterable<ProductType> response = null;
		try {
			response = service.getAllActives();
		}catch(BadRequestException e) {
			log.error("ProductTypeController getActives Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/actives_lazy")
	public ResponseEntity getAllActivesLazy(){
		log.info("ProductTypeController getAllActivesLazy");
		Iterable<ProductType> response = null;
		try {
			response = service.getAllActivesLazy();
		}catch(BadRequestException e) {
			log.error("ProductTypeController getActivesLazy Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

}

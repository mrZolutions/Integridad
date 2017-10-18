package com.mrzolution.integridad.app.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ProductServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/product")
public class ProductController {
	
	@Autowired
	ProductServices service;


	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Product product){
		log.info("ProductController create: {}", product);
		Product response = null;
		try {
			response = service.create(product);
		}catch(BadRequestException e) {
			log.error("ProductController create Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Product>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Product product){
		log.info("ProductController update: {}", product);
		try {
			service.update(product);
		}catch(BadRequestException e) {
			log.error("ProductController update Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{productId}")
    public ResponseEntity delete(@PathVariable("productId") UUID productId){
		log.info("ProductController delete: {}", productId);
		Product response = null;
		try {
			response = service.delete(productId);
		}catch(BadRequestException e) {
			log.error("ProductController delete Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Product>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{productId}")
    public ResponseEntity getById(@PathVariable(value = "productId") UUID productId){
		log.info("ProductController getById");
		Product response = null;
		try {
			response = service.getById(productId);
		}catch(BadRequestException e) {
			log.error("ProductController getgetById Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Product>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getAllActives(){
		log.info("ProductController getAllActives");
		Iterable<Product> response = null;
		try {
			response = service.getAllActives();
		}catch(BadRequestException e) {
			log.error("ProductController getLazy Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/actives/user_client/{userClientId}")
    public ResponseEntity getAllActivesByUserClientId(@PathVariable("userClientId") UUID userClientId){
		log.info("ProductController getAllActivesByUserClientId: {}", userClientId);
		Iterable<Product> response = null;
		try {
			response = service.getAllActivesByUserClientIdAndActive(userClientId);
		}catch(BadRequestException e) {
			log.error("ProductController getAllActivesByUserClientId Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/actives/subsidiary/{subsidiaryId}")
    public ResponseEntity getAllActivesBySubsidiaryId(@PathVariable("subsidiaryId") UUID subsidiaryId){
		log.info("ProductController getAllActivesBySubsidiaryId: {}", subsidiaryId);
		Iterable<Product> response = null;
		try {
			response = service.getAllActivesBySubsidiaryIdAndActive(subsidiaryId);
		}catch(BadRequestException e) {
			log.error("ProductController getAllActivesBySubsidiaryId Exception thrown: {}", e.getMessage());	    
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}
	
	
}
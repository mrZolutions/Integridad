package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.ProductType;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BrandServices;
import com.mrzolution.integridad.app.services.ProductTypeServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/brand")
public class BrandController {

	@Autowired
	BrandServices service;


	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Brand brand){
		log.info("BrandController create: {}", brand);
		Brand response = null;
		try {
			response = service.create(brand);
		}catch(BadRequestException e) {
			log.error("BrandController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Brand>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Brand brand){
		log.info("BrandController update: {}", brand);
		try {
			service.update(brand);
		}catch(BadRequestException e) {
			log.error("BrandController update Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{brandId}")
    public ResponseEntity delete(@PathVariable("brandId") UUID brandId){
		log.info("BrandController delete: {}", brandId);
		Brand response = null;
		try {
			response = service.delete(brandId);
		}catch(BadRequestException e) {
			log.error("BrandController delete Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Brand>(response, HttpStatus.ACCEPTED);
	}

	
	@RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getAllActives(){
		log.info("BrandController getAllActives");
		Iterable<Brand> response = null;
		try {
			response = service.getAllActives();
		}catch(BadRequestException e) {
			log.error("BrandController getActives Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET, value="/actives_lazy")
	public ResponseEntity getAllActivesLazy(){
		log.info("BrandController getAllActivesLazy");
		Iterable<Brand> response = null;
		try {
			response = service.getAllActivesLazy();
		}catch(BadRequestException e) {
			log.error("BrandController getActivesLazy Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
	}

}

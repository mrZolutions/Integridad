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
public class ProductTypeController {
    @Autowired
    ProductTypeServices service;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createProductType(@RequestBody ProductType productType) {
	log.info("ProductTypeController createProductType: {}", productType);
	ProductType response = null;
	try {
            response = service.createProductType(productType);
	} catch (BadRequestException e) {
            log.error("ProductTypeController createProductType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<ProductType>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateProductType(@RequestBody ProductType productType) {
	log.info("ProductTypeController updateProductType: {}", productType);
	try {
            service.updateProductType(productType);
	} catch (BadRequestException e) {
            log.error("ProductTypeController updateProductType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
	
    @RequestMapping(method = RequestMethod.DELETE, value = "/{productTypeId}")
    public ResponseEntity deleteProductType(@PathVariable("productTypeId") UUID productTypeId) {
    	log.info("ProductTypeController deleteProductType: {}", productTypeId);
	ProductType response = null;
	try {
            response = service.deleteProductType(productTypeId);
	} catch(BadRequestException e) {
            log.error("ProductTypeController deleteProductType Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<ProductType>(response, HttpStatus.ACCEPTED);
    }

	
    @RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getAllActives() {
	log.info("ProductTypeController getAllActives");
	Iterable<ProductType> response = null;
	try {
            response = service.getAllActives();
	} catch (BadRequestException e) {
            log.error("ProductTypeController getActives Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives_lazy")
    public ResponseEntity getAllActivesLazy() {
	log.info("ProductTypeController getAllActivesLazy");
	Iterable<ProductType> response = null;
	try {
            response = service.getAllActivesLazy();
	} catch (BadRequestException e) {
            log.error("ProductTypeController getActivesLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

}

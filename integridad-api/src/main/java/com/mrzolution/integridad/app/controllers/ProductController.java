package com.mrzolution.integridad.app.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity createProduct(@RequestBody Product product) {
	Product response = null;
	try {
            response = service.createProduct(product);
	} catch (BadRequestException e) {
            log.error("ProductController createProduct Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController createProduct DONE");
	return new ResponseEntity<Product>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateProduct(@RequestBody Product product) {
        try {
            service.updateProduct(product);
	} catch (BadRequestException e) {
            log.error("ProductController updateProduct Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController updateProduct DONE");
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
	
    @RequestMapping(method = RequestMethod.DELETE, value = "/{productId}")
    public ResponseEntity deleteProduct(@PathVariable("productId") UUID productId) {
        Product response = null;
	try {
            response = service.deleteProduct(productId);
	} catch (BadRequestException e) {
            log.error("ProductController deleteProduct Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController deleteProduct DONE");
	return new ResponseEntity<Product>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/{productId}")
    public ResponseEntity getAllProductById(@PathVariable(value = "productId") UUID productId) {
        Product response = null;
	try {
            response = service.getProductById(productId);
	} catch (BadRequestException e) {
            log.error("ProductController getAllProductById Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getAllProductById DONE");
	return new ResponseEntity<Product>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getAllActives() {
        Iterable<Product> response = null;
	try {
            response = service.getAllActives();
	} catch (BadRequestException e) {
            log.error("ProductController getAllActives Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getAllActives DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/actives/user_client/{userClientId}")
    public ResponseEntity getAllActivesByUserClientId(@PathVariable("userClientId") UUID userClientId) {
        Iterable<Product> response = null;
	try {
            response = service.getAllActivesByUserClientIdAndActive(userClientId);
	} catch (BadRequestException e) {
            log.error("ProductController getAllActivesByUserClientId Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getAllActivesByUserClientId DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
        
    @RequestMapping(method = RequestMethod.GET, value="/actives/subsidiary/{subsidiaryId}/{page}")
    public ResponseEntity getAllActivesBySubsidiaryId(@PathVariable("subsidiaryId") UUID subsidiaryId, @PathVariable("page") int page, @RequestParam(required = false, name = "var") String variable) {
        Page<Product> response = null;
	try {
            response = service.getAllActivesBySubsidiaryIdAndActive(subsidiaryId, variable, new PageRequest(page, 50, Sort.Direction.ASC, "product"));
	} catch (BadRequestException e) {
            log.error("ProductController getAllActivesBySubsidiaryId Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getAllActivesBySubsidiaryId DONE");
        return new ResponseEntity<Page>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives/subsidiary/bill/{subsidiaryId}/{page}")
    public ResponseEntity getAllActivesBySubsidiaryIdForBill(@PathVariable("subsidiaryId") UUID subsidiaryId, @PathVariable("page") int page, @RequestParam(required = false, name = "var") String variable) {
        log.info("ProductController getAllActivesBySubsidiaryId: {}", subsidiaryId);
        Page<Product> response = null;
        try {
            response = service.getAllActivesBySubsidiaryIdForBill(subsidiaryId, variable, new PageRequest(page, 50, Sort.Direction.ASC, "product"));
        } catch (BadRequestException e) {
            log.error("ProductController getAllActivesBySubsidiaryId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Page>(response, HttpStatus.CREATED);
    }

}
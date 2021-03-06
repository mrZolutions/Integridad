package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BrandServices;
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
    public ResponseEntity createBrand(@RequestBody Brand brand) {
	Brand response = null;
	try {
            response = service.createBrand(brand);
	} catch (BadRequestException e) {
            log.error("BrandController createBrand Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BrandController createBrand DONE");
	return new ResponseEntity<Brand>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateBrand(@RequestBody Brand brand) {
        try {
            service.updateBrand(brand);
	} catch (BadRequestException e) {
            log.error("BrandController updateBrand Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BrandController updateBrand DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
	
    @RequestMapping(method = RequestMethod.DELETE, value = "/{brandId}")
    public ResponseEntity deleteBrand(@PathVariable("brandId") UUID brandId) {
        Brand response = null;
	try {
            response = service.deleteBrand(brandId);
	} catch (BadRequestException e) {
            log.error("BrandController deleteBrand Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BrandController deleteBrand DONE");
	return new ResponseEntity<Brand>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getAllActives() {
        Iterable<Brand> response = null;
	try {
            response = service.getAllActives();
	} catch (BadRequestException e) {
            log.error("BrandController getActives Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("BrandController getAllActives DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives_lazy/{projectId}")
    public ResponseEntity getAllActivesByProjectIdLazy(@PathVariable("projectId") UUID projectId) {
        Iterable<Brand> response = null;
	try {
            response = service.getAllActivesLazy(projectId);
	} catch (BadRequestException e) {
            log.error("BrandController getActivesByProjectIdLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("BrandController getAllActivesByProjectIdLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
}
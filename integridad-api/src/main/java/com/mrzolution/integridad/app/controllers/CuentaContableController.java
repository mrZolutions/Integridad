package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.domain.Provider;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CuentaContableServices;
import com.mrzolution.integridad.app.services.ProviderServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/cuenta_contable")
public class CuentaContableController {

	@Autowired
	CuentaContableServices service;


	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody CuentaContable cuentaContable){
		log.info("CuentaContableController create");
		CuentaContable response = null;
		try {
			response = service.create(cuentaContable);
		}catch(BadRequestException e) {
			log.error("CuentaContableController create Exception thrown: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
		return new ResponseEntity<CuentaContable>(response, HttpStatus.CREATED);
	}

//	@RequestMapping(method = RequestMethod.PUT)
//    public ResponseEntity update(@RequestBody Provider provider){
//		log.info("ProviderController update: {}", provider);
//		Provider response = null;
//		try {
//			service.update(provider);
//		}catch(BadRequestException e) {
//			log.error("ProviderController update Exception thrown: {}", e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//	    }
//		return new ResponseEntity<Void>(HttpStatus.CREATED);
//	}

	@RequestMapping(method = RequestMethod.GET, value="/lazy/client/{id}")
	public ResponseEntity getLazyByUserClient(@PathVariable("id") UUID id){
		log.info("CuentaContableController getLazyByUserClient");
		Iterable<CuentaContable> response = null;
		try {
			response = service.getLazyByUserClient(id);
		}catch(BadRequestException e) {
			log.error("CuentaContableController getLazy Exception thrown: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
	}

//	@RequestMapping(method = RequestMethod.GET, value="/lazy")
//    public ResponseEntity getLazy(){
//		log.info("ProviderController getLazy");
//		Iterable<Provider> response = null;
//		try {
//			response = service.getAllLazy();
//		}catch(BadRequestException e) {
//			log.error("ProviderController getLazy Exception thrown: {}", e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//	    }
//		return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value="/{id}")
//	public ResponseEntity getById(@PathVariable("id") UUID id){
//		log.info("ProviderController geById:{}", id);
//		Provider response = null;
//		try {
//			response = service.getById(id);
//		}catch(BadRequestException e) {
//			log.error("ProviderController getById Exception thrown: {}", e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//		}
//		return new ResponseEntity<Provider>(response, HttpStatus.CREATED);
//	}
}

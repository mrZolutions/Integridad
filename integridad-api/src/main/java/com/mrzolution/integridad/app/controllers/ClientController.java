package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.report.ClientReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ClientServices;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/client")
public class ClientController {
    @Autowired
    ClientServices service;
	
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createClient(@RequestBody Client client) {
	Client response = null;
	try {
            response = service.createClient(client);
	} catch (BadRequestException e) {
            log.error("ClientController createClient Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ClientController createClient DONE");
	return new ResponseEntity<Client>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateClient(@RequestBody Client client) {
        Client response = null;
	try {
            service.updateClient(client);
	} catch (BadRequestException e) {
            log.error("ClientController updateClient Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ClientController updateClient DONE");
	return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getAllClientActives() {
        Iterable<Client> response = null;
	try {
            response = service.getAllClientActives();
	} catch (BadRequestException e) {
            log.error("ClientController getAllClientActives Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ClientController getAllClientActives DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/lazy/{id}")
    public ResponseEntity getLazyByUserClient(@PathVariable("id") UUID userClientId){
        Iterable<Client> response = null;
	try {
            response = service.getAllLazyByUserClientid(userClientId);
	} catch (BadRequestException e) {
            log.error("ClientController getLazyByUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ClientController getLazyByUserClient DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/userclient/{userClientId}/{identification}")
    public ResponseEntity getClientByUserClientAndIdentification(@PathVariable("userClientId") UUID userClientId, @PathVariable("identification") String identification) {
        Iterable<Client> response = null;
        try {
            response = service.getClientByUserClientAndIdentification(userClientId, identification);
        } catch (BadRequestException e) {
            log.error("ClientController getClientByUserClientAndIdentification Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ClientController getClientByUserClientAndIdentification DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/client/{userClientId}")
    public ResponseEntity getClientsReport(@PathVariable("userClientId") UUID userClientId) {
        List<ClientReport> response = null;
        try {
            response = service.getClientsReport(userClientId);
	} catch (BadRequestException e) {
            log.error("ClientController getClientsReport Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ClientController getClientsReport DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, value="/list")
    public ResponseEntity createClientesList(@RequestBody List<Client> clientes) {
        int response = 0;
        try {
            response = service.createClienteList(clientes);
        } catch(BadRequestException e) {
            log.error("CuentaContableController createCuentaContableList Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("CuentaContableController createCuentaContableList DONE");
        return new ResponseEntity<Integer>(response, HttpStatus.CREATED);
    }
}
package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Asociado;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.AsociadoServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/asociados")
public class AsociadoController {
    @Autowired
    AsociadoServices service;

    @RequestMapping(method = RequestMethod.GET, value="/lazy")
    public ResponseEntity getAllActivesLazy() {
	Iterable<Asociado> response = null;
	try {
            response = service.getAllActivesLazy();
	} catch (BadRequestException e) {
            log.info("AsociadoController getAllActivesLazy Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("AsociadoController getAllActivesLazy DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.OK);
    }
}
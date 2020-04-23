package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.OptionConfigCuentas;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.OptionConfigCuentasServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/optionConfigCuentas")
public class OpionsConfigCuentasController {

    @Autowired
    OptionConfigCuentasServices services;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getOptionConfigCuentas(){
        Iterable<OptionConfigCuentas> response = null;
        try {
            response = services.getAll();

        } catch (BadRequestException e) {
            log.error("OpionsConfigCuentasController getOptionConfigCuentas Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.info("OpionsConfigCuentasController getOptionConfigCuentas DONE");
        return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
}

package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.ConfigCuentas;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ConfigCuentasServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/configCuentas")
public class ConfigCuentasController {

    @Autowired
    ConfigCuentasServices services;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createConfigCuentas(@RequestBody ConfigCuentas configCuentas){
        ConfigCuentas response = null;
        try {
            response = services.saveConfigCuentas(configCuentas);

        } catch (BadRequestException e) {
            log.error("ConfigCuentasController createConfigCuentas Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.info("ConfigCuentasController createConfigCuentas DONE");
        return new ResponseEntity<ConfigCuentas>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userClientId}")
    public ResponseEntity saveConfigCuentasList(@RequestBody List<ConfigCuentas> configCuentasList, @PathVariable("userClientId") UUID userClientId){
        List<ConfigCuentas> response = null;
        try {
            response = services.saveConfigCuentasList(configCuentasList, userClientId);

        } catch (BadRequestException e) {
            log.error("ConfigCuentasController saveConfigCuentasList Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.info("ConfigCuentasController saveConfigCuentasList DONE");
        return new ResponseEntity<List>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{id}")
    public ResponseEntity getConfigCuentasByUserClient(@PathVariable("id") UUID userClientId){
        List<ConfigCuentas> response = null;
        try {
            response = services.getCuentasByUserCliendId(userClientId);

        } catch (BadRequestException e) {
            log.error("ConfigCuentasController getConfigCuentasByUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.info("ConfigCuentasController getConfigCuentasByUserClient DONE");
        return new ResponseEntity<List>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/userClient/{id}/code/{code}")
    public ResponseEntity getConfigCuentasByUserClientAndOptionCode(@PathVariable("id") UUID userClientId, @PathVariable("code") String code){
        ConfigCuentas response = null;
        try {
            response = services.getCuentasByUserCliendIdAndOptionCode(userClientId, code);

        } catch (BadRequestException e) {
            log.error("ConfigCuentasController getConfigCuentasByUserClient Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.info("ConfigCuentasController getConfigCuentasByUserClient DONE");
        return new ResponseEntity<ConfigCuentas>(response, HttpStatus.CREATED);
    }
}

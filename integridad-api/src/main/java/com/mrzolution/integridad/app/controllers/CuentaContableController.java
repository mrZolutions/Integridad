package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.CuentaContableServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/integridad/v1/cuenta_contable")
public class CuentaContableController {

    @Autowired
    CuentaContableServices service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll() {
        Iterable<CuentaContable> result = null;
        try {
            result = service.getAll();
        }catch (BadRequestException e) {

        }
        return new ResponseEntity<Iterable>(result, HttpStatus.ACCEPTED);
    }
}

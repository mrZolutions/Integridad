package com.mrzolution.integridad.app.controllers;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.ModuleMenu;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.BrandServices;
import com.mrzolution.integridad.app.services.ModuleMenuServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/modules")
public class ModuleMenuController {
    @Autowired
    ModuleMenuServices services;

    @RequestMapping(method = RequestMethod.GET )
    public ResponseEntity getAll() {
        Iterable<ModuleMenu> response = null;
	try {
            response = services.getAll();
	} catch (BadRequestException e) {
            log.error("ModuleMenuController getAll Exception thrown: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ModuleMenuController getAll DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.ACCEPTED);
    }

}
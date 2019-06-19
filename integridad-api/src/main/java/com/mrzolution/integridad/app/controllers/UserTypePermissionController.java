package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.domain.UserTypePermissions;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.UserTypePermissionsServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/user_type_permission")
public class UserTypePermissionController {
    @Autowired
    UserTypePermissionsServices service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createUserTypePermissions(@RequestBody UserTypePermissions userTypePermission) {
	UserTypePermissions response = null;
	try {
            response = service.createUserTypePermissions(userTypePermission);
	} catch (BadRequestException e) {
            log.error("UserTypePermissionController createUserTypePermissions Exception thrown: {}", e.getMessage());    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("UserTypePermissionController createUserTypePermissions DONE");
	return new ResponseEntity<UserTypePermissions>(response, HttpStatus.CREATED);
    }
	
    @RequestMapping(method = RequestMethod.POST, value="/user_type")
    public ResponseEntity getByUserType(@RequestBody UserType userType) {
	Iterable<UserTypePermissions> response = null;
	try {
            response = service.getByUserType(userType);
	} catch (BadRequestException e) {
            log.info("UserTypePermissionController getByUserType Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("UserTypePermissionController getByUserType DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.OK);
    }
}
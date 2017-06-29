package com.mrzolution.integridad.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.services.UserIntegridadServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/user")
public class UserIntegridadController {
	
	@Autowired
	UserIntegridadServices service;

	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserIntegridad> create(@RequestBody UserIntegridad userIntegridad){
		UserIntegridad response = service.create(userIntegridad);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}

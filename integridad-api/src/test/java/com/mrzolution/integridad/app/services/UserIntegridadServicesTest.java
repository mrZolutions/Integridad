package com.mrzolution.integridad.app.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserIntegridadServicesTest {
	
	@InjectMocks
	UserIntegridadServices service;
	
	@Mock
	UserIntegridadRepository userIntegridadRepository;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@Test
	public void createTest(){
		UserIntegridad user = UserIntegridad.newAuditFirmTest();
		
		service.create(user);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(1)).save(Mockito.any(UserIntegridad.class));
	}

}

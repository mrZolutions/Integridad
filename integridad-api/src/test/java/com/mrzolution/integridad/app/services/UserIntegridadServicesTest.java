package com.mrzolution.integridad.app.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserIntegridadServicesTest {
	
	@InjectMocks
	UserIntegridadServices service;
	
	@Mock
	UserIntegridadRepository userIntegridadRepository;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	UserIntegridad user;
	
	@Before
	public void setupTest(){
		user = UserIntegridad.newUserIntegridadTest();
	}
	
	@Test
	public void createTest(){
		service.create(user);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(1)).save(Mockito.any(UserIntegridad.class));
	}
	
	@Test(expected=BadRequestException.class)
	public void validateUniqueMailTest(){
		String email = "daniel@yahoo.com"; 
		user.setEmail(email);
		
		Mockito.when(userIntegridadRepository.findByEmailContainingIgnoreCase(email)).thenReturn(UserIntegridad.newUserIntegridadTest());
		
		service.create(user);
	}
	
	@Test
	public void authenticationTest(){
		String email = "daniel@yahoo.com"; 
		user.setEmail(email);
		
		Mockito.when(userIntegridadRepository.findByEmailContainingIgnoreCase(email)).thenReturn(UserIntegridad.newUserIntegridadTest());
		UserIntegridad authenticated = service.authenticate(user);
		
		Assert.assertNotNull(authenticated);
	}
	
	@Test(expected=BadRequestException.class)
	public void wrongMailAuthenticationTest(){
		String email = "daniel@yahoo.com"; 
		user.setEmail(email);
		
		Mockito.when(userIntegridadRepository.findByEmailContainingIgnoreCase(email)).thenReturn(null);
		UserIntegridad authenticated = service.authenticate(user);
		
		Assert.assertNull(authenticated);
	}
	
	@Test(expected=BadRequestException.class)
	public void wrongPasswordAuthenticationTest(){
		String email = "daniel@yahoo.com";
		String password = "12345";
		user.setEmail(email);
		user.setPassword(password);
		
		UserIntegridad userWrongPass = UserIntegridad.newUserIntegridadTest();
		password = "123456";
		userWrongPass.setEmail(email);
		userWrongPass.setPassword(password);
		
		Mockito.when(userIntegridadRepository.findByEmailContainingIgnoreCase(email)).thenReturn(userWrongPass);
		UserIntegridad authenticated = service.authenticate(user);
		
		Assert.assertNull(authenticated);
	}

}

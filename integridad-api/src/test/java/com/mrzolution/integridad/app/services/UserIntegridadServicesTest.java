package com.mrzolution.integridad.app.services;

import java.util.UUID;

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
import com.mrzolution.integridad.app.domain.UserType;
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
	
	@Mock
	MailingService mailingService;
	
	UserIntegridad user;
	
	@Before
	public void setupTest(){
		user = UserIntegridad.newUserIntegridadTest();
	}
	
	@Test
	public void createTest(){
		Mockito.when(userIntegridadRepository.save(user)).thenReturn(user);
		
		UserIntegridad created = service.create(user);
		System.out.println(created);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(1)).save(Mockito.any(UserIntegridad.class));
		Mockito.verify(mailingService, Mockito.times(1)).sendEmailREgister(Mockito.any(UserIntegridad.class));
		
		Assert.assertNotNull(created.getValidation());
		Assert.assertFalse(created.isActive());
	}
	
	@Test(expected=BadRequestException.class)
	public void validateUniqueMailTest(){
		String email = "daniel@yahoo.com"; 
		user.setEmail(email);
		
		Mockito.when(userIntegridadRepository.findByEmailIgnoreCaseAndActive(email, true)).thenReturn(UserIntegridad.newUserIntegridadTest());
		
		service.create(user);
	}
	
	@Test(expected=BadRequestException.class)
	public void wrongMailAuthenticationTest(){
		String email = "daniel@yahoo.com"; 
		user.setEmail(email);
		
		Mockito.when(userIntegridadRepository.findByEmailIgnoreCaseAndActive(email, true)).thenReturn(null);
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
		
		Mockito.when(userIntegridadRepository.findByEmailIgnoreCaseAndActive(email, true)).thenReturn(userWrongPass);
		UserIntegridad authenticated = service.authenticate(user);
		
		Assert.assertNull(authenticated);
	}
	
	@Test
	public void successAuthenticationTest() throws Exception {
		String email = "daniel@yahoo.com";
		String password = "12345";
		user.setUserType(UserType.newUserTypeTest());
		user.setEmail(email);
		user.setPassword(password);
		
		Mockito.when(userIntegridadRepository.findByEmailIgnoreCaseAndActive(email, true)).thenReturn(user);
		Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		UserIntegridad authenticated = service.authenticate(user);
		
		ListValidation.checkListsAndFatherNull(UserIntegridad.class, authenticated);
		
		Assert.assertNotNull(authenticated);
	}
	
	@Test
	public void activateTest(){
		UUID userId = UUID.randomUUID();
		String validation = "yyyyy";
		
		user.setActive(false);
		Mockito.when(userIntegridadRepository.findByIdAndValidation(userId, validation)).thenReturn(user);
		Mockito.when(userIntegridadRepository.save(user)).thenReturn(user);
		
		UserIntegridad activated = service.activate(userId, validation);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(1)).save(Mockito.any(UserIntegridad.class));
		
		Assert.assertNotNull(activated);
	}
	
	@Test(expected=BadRequestException.class)
	public void wrongActiveTest(){
		UUID userId = UUID.randomUUID();
		String validation = "yyyyy";
		
		user.setActive(false);
		Mockito.when(userIntegridadRepository.findByIdAndValidation(userId, validation)).thenReturn(null);
		
		UserIntegridad activated = service.activate(userId, validation);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(0)).save(Mockito.any(UserIntegridad.class));
		
		Assert.assertNull(activated);
	}
	
	@Test(expected=BadRequestException.class)
	public void wrongActivate2Test(){
		UUID userId = UUID.randomUUID();
		String validation = "yyyyy";
		
		user.setActive(true);
		Mockito.when(userIntegridadRepository.findByIdAndValidation(userId, validation)).thenReturn(user);
		
		UserIntegridad activated = service.activate(userId, validation);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(0)).save(Mockito.any(UserIntegridad.class));
		
		Assert.assertNull(activated);
	}
	
	@Test
	public void recoverPasswordTest(){
		String mail = "daniel@yahoo.com";
		
		user.setActive(true);
		user.setEmail(mail);
		Mockito.when(userIntegridadRepository.findByEmailIgnoreCaseAndActive(mail, true)).thenReturn(user);
		Mockito.when(userIntegridadRepository.save(user)).thenReturn(user);
		
		UserIntegridad recovered = service.recoverPassword(mail);
		
		Mockito.verify(userIntegridadRepository, Mockito.times(1)).save(Mockito.any(UserIntegridad.class));
		Mockito.verify(mailingService, Mockito.times(1)).sendEmailRecoveryPass(Mockito.any(UserIntegridad.class), Mockito.anyString());
		
		Assert.assertNotNull(recovered);
	}

}

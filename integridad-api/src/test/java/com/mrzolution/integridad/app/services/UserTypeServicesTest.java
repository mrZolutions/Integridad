package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.repositories.UserIntegridadRepository;
import com.mrzolution.integridad.app.repositories.UserTypeRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserTypeServicesTest {
	
	@InjectMocks
	UserTypeServices service;

	@Mock
	UserTypeRepository userTypeRepository;
	
	@Mock
	UserIntegridadRepository userIntegridadRepository;
	
	UserType userType;
	
	@Before
	public void setupTest(){
		userType = UserType.newUserTypeTest();
	}
		
	@Test
	public void validatePopulateChildren() throws Exception {
		UUID id = UUID.randomUUID();
		UserIntegridad userIntegridad1 = UserIntegridad.newUserIntegridadTest();
		UserIntegridad userIntegridad2 = UserIntegridad.newUserIntegridadTest();
		List<UserIntegridad> userIntegridadList = new ArrayList<>();
		userIntegridadList.add(userIntegridad1);
		userIntegridadList.add(userIntegridad2);
		
		Mockito.when(userIntegridadRepository.findByUserType(userType)).thenReturn(userIntegridadList);
		Mockito.when(userTypeRepository.findOne(id)).thenReturn(userType);
		UserType retrieved = service.getById(id);
		
		ListValidation.childsLisAndFathertValidation(UserType.class, retrieved);
		
		Assert.assertNotNull(retrieved);
	}

}

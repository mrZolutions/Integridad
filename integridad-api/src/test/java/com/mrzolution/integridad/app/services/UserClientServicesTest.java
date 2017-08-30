package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.repositories.UserClientRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserClientServicesTest {
	
	@InjectMocks
	UserClientServices service;
	
	@Mock
	UserClientRepository userClientRepository;
	
	UserClient client;
	
	@Before
	public void setupTest(){
		client = UserClient.newUserClientTest();
	}
	
	@Test
	public void getAllActives() throws Exception{
		client.setActive(true);
		
		List<UserClient> userClientList = new ArrayList<>();
		userClientList.add(client);
		
		Mockito.when(userClientRepository.findByActive(true)).thenReturn(userClientList);
		
		Iterable<UserClient> response = service.getAllActivesLazy();
		
		for(UserClient user : response){
			ListValidation.checkListsAndFatherNull(UserClient.class, user);
		}
		
		Assert.assertTrue(Iterables.size(response) == 1);
	}

}

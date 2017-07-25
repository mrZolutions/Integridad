package com.mrzolution.integridad.app.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.repositories.ClientRepository;

@RunWith(MockitoJUnitRunner.class)
public class ClientServicesTest {
	
	@InjectMocks
	ClientServices service;
	
	@Mock
	ClientRepository clientRepository;
	
	Client client;
	
	@Before
	public void setupTest(){
		client = Client.newClientTest();
	}
	
	@Test
	public void test(){
		
	}

}

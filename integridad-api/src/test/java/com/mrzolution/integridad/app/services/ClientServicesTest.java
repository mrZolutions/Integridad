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

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.ClientRepository;

@RunWith(MockitoJUnitRunner.class)
public class ClientServicesTest {
	
	@InjectMocks
	ClientServices service;
	
	@Mock
	ClientRepository clientRepository;
	
	@Mock
	BillRepository billRepository;
	
	Client client;
	
	@Before
	public void setupTest(){
		client = Client.newClientTest();
	}
	
	@Test
	public void getLazyTest() throws Exception{
		List<Client> clients = new ArrayList<>();
		clients.add(client);
		
		Mockito.when(clientRepository.findByActive(true)).thenReturn(clients);
		
		Iterable<Client> clientList = service.getAllClientActives();
		Assert.assertEquals(1, Iterables.size(clientList));
		for(Client client: clientList){
			ListValidation.checkListsAndFatherNull(Client.class, client);
		}
		
	}

	@Test
	public void getLazyByUserClientTest() throws Exception{
		List<Client> clients = new ArrayList<>();
		clients.add(client);

		Mockito.when(clientRepository.findActivesByUserClientId(Mockito.any())).thenReturn(clients);

		Iterable<Client> clientList = service.getAllLazyByUserClientid(UUID.randomUUID());
		Assert.assertEquals(1, Iterables.size(clientList));
		for(Client client: clientList){
			ListValidation.checkListsAndFatherNull(Client.class, client);
		}

	}
	
	@Test
	public void validatePopulateChildren() throws Exception {
		UUID id = UUID.randomUUID();
		Bill bill1 = Bill.newBillTest();
		Bill bill2 = Bill.newBillTest();
		List<Bill> billList = new ArrayList<>();
		billList.add(bill1);
		billList.add(bill2);
		
		Mockito.when(billRepository.findByClient(client)).thenReturn(billList);
		Mockito.when(clientRepository.findOne(id)).thenReturn(client);
		Client retrieved = service.getClientById(id);
		
//		ListValidation.childsLisAndFathertValidation(Client.class, retrieved);
		
		Assert.assertNotNull(retrieved);
	}
	
	@Test(expected=BadRequestException.class)
	public void validateExistCodeAppOnCreate(){
		service.createClient(client);
	}

}

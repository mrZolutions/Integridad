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
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.ClientRepository;

@RunWith(MockitoJUnitRunner.class)
public class BillServicesTest {
	
	@InjectMocks
	BillServices service;
	
	@Mock
	BillRepository billRepository;	
	
//	@Mock
//	ClientRepository clientRepository;
//	
//	
	Bill bill;
//	
	@Before
	public void setupTest(){
		bill = Bill.newBillTest();
	}
	
	@Test
	public void getByUserLazyTest() throws Exception{
		List<Bill> bills = new ArrayList<>();
		bills.add(bill);
		
		UserIntegridad user = new UserIntegridad();
		
		Mockito.when(billRepository.findByUserIntegridad(user)).thenReturn(bills);
		
		Iterable<Bill> billList = service.getByUserLazy(user);
		Assert.assertEquals(1, Iterables.size(billList));
		for(Bill bill: billList){
			ListValidation.checkListsAndFatherNull(Bill.class, bill);
		}
		
	}
//	
//	@Test
//	public void validatePopulateChildren() throws Exception {
//		UUID id = UUID.randomUUID();
//		Bill bill1 = Bill.newBillTest();
//		Bill bill2 = Bill.newBillTest();
//		List<Bill> billList = new ArrayList<>();
//		billList.add(bill1);
//		billList.add(bill2);
//		
//		Mockito.when(billRepository.findByClient(client)).thenReturn(billList);
//		Mockito.when(clientRepository.findOne(id)).thenReturn(client);
//		Client retrieved = service.getById(id);
//		
//		ListValidation.childsLisAndFathertValidation(Client.class, retrieved);
//		
//		Assert.assertNotNull(retrieved);
//	}

}

package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Cashier;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class SubsidiaryServicesTest {
	
	@InjectMocks
	SubsidiaryServices service;

	@Mock
	SubsidiaryRepository subsidiaryRepository;
	@Mock
	CashierRepository cashierRepository;
	@Mock
	CashierChildRepository cashierChildRepository;

	Subsidiary subsidiary;
	Cashier cashier;
	
	List<Cashier> cashierList = new ArrayList<>();
	
	@Before
	public void setupTest(){
		subsidiary = Subsidiary.newSubsidiaryTest();
		subsidiary.setUserClient(null);

		cashier = Cashier.newCashierTest();
		cashier.setSubsidiary(null);
	}
	
	@Test
	public void createCallCashierRepository(){
		cashierList.add(cashier);
		subsidiary.setCashiers(cashierList);

		Mockito.when(subsidiaryRepository.save(Mockito.any(Subsidiary.class))).thenReturn(Subsidiary.newSubsidiaryTest());
		
		Subsidiary response = service.create(subsidiary);
		
		Mockito.verify(subsidiaryRepository, Mockito.times(1)).save(Mockito.any(Subsidiary.class));
		Mockito.verify(cashierRepository, Mockito.times(1)).save(cashier);
		
		Assert.assertTrue(!response.getCashiers().isEmpty());
		
	}

	@Test
    public void onUpdateShouldCallRightChildrenRepository(){
    	UUID id = UUID.randomUUID();
        subsidiary.setId(id);

        UUID idChildOld = UUID.randomUUID();
        UUID idChildNew = UUID.randomUUID();
        UUID idChildUpdate = UUID.randomUUID();

        List<Cashier> cashierListNew = new ArrayList<>();
		Cashier cashierN = new Cashier();
        cashierN.setId(idChildNew);
        cashierN.setSubsidiary(subsidiary);
        cashierListNew.add(cashierN);

		Cashier cashierU = new Cashier();
        cashierU.setId(idChildUpdate);
        cashierU.setSubsidiary(subsidiary);
        cashierListNew.add(cashierU);

        List<UUID> subsidiaryListOld = new ArrayList<>();
        subsidiaryListOld.add(idChildOld);
        subsidiaryListOld.add(idChildUpdate);

        subsidiary.setCashiers(cashierListNew);

        Mockito.when(cashierChildRepository.findByFather(subsidiary)).thenReturn(subsidiaryListOld);
        Mockito.when(subsidiaryRepository.save(subsidiary)).thenReturn(subsidiary);

        service.update(subsidiary);

        Mockito.verify(cashierRepository, Mockito.times(1)).save(cashierN);
        Mockito.verify(cashierRepository, Mockito.times(1)).save(cashierU);
        Mockito.verify(cashierRepository, Mockito.times(1)).delete(idChildOld);

    }
//
//	@Test
//	public void getByIdTest() throws Exception{
//		UUID id = UUID.randomUUID();
//
//		subsidiary.setUserClient(client);
//		subsidiaryList.add(subsidiary);
//		client.setSubsidiaries(subsidiaryList);
//
//		Mockito.when(userClientRepository.findOne(id)).thenReturn(client);
//		Mockito.when(subsidiaryRepository.findByUserClient(client)).thenReturn(subsidiaryList);
//
//		UserClient retrieved = service.getById(id);
//		ListValidation.childsLisAndFathertValidation(UserClient.class, retrieved);
//
//		Assert.assertNotNull(retrieved);
//	}

}

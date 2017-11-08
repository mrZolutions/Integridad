package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.domain.Cashier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.SubsidiaryChildRepository;
import com.mrzolution.integridad.app.repositories.SubsidiaryRepository;
import com.mrzolution.integridad.app.repositories.UserClientRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserClientServicesTest {
	
	@InjectMocks
	UserClientServices service;
	
	@Mock
	UserClientRepository userClientRepository;
	@Mock
	SubsidiaryServices subsidiaryServices;
	@Mock
	SubsidiaryRepository subsidiaryRepository;
	@Mock
	SubsidiaryChildRepository subsidiaryChildRepository;
	
	UserClient client;
	Subsidiary subsidiary;
	Cashier cashier;
	
	List<Subsidiary> subsidiaryList = new ArrayList<>();
	List<Cashier> cashierList = new ArrayList<>();
	
	@Before
	public void setupTest(){
		client = UserClient.newUserClientTest();
		subsidiary = Subsidiary.newSubsidiaryTest();
		subsidiary.setUserClient(null);

		cashier = Cashier.newCashierTest();
		cashier.setSubsidiary(null);
	}
	
	@Test
	public void createCallSubsidiaryRepository(){
		cashierList.add(cashier);
		subsidiary.setCashiers(cashierList);
		subsidiaryList.add(subsidiary);
		client.setSubsidiaries(subsidiaryList);
		
		Mockito.when(userClientRepository.save(Mockito.any(UserClient.class))).thenReturn(UserClient.newUserClientTest());
		
		UserClient response = service.create(client);
		
		Mockito.verify(userClientRepository, Mockito.times(1)).save(Mockito.any(UserClient.class));
		Mockito.verify(subsidiaryServices, Mockito.times(1)).create(subsidiary);
		
		Assert.assertTrue(!response.getSubsidiaries().isEmpty());
		
	}
	
	@Test(expected=BadRequestException.class)
	public void validatAtLeastOneSubsidiary(){
		client.setSubsidiaries(null);
		service.create(client);
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
	
	@Test
    public void onUpdateShouldCallRightChildrenRepository(){
    	UUID id = UUID.randomUUID();
        client.setId(id);
        
        UUID idChildOld = UUID.randomUUID();
        UUID idChildNew = UUID.randomUUID();
        UUID idChildUpdate = UUID.randomUUID();
        
        List<Subsidiary> subsidiaryListNew = new ArrayList<>();
        Subsidiary subsidiaryN = new Subsidiary();
        subsidiaryN.setId(idChildNew);
        subsidiaryN.setUserClient(client);
        subsidiaryListNew.add(subsidiaryN);

        Subsidiary subsidiaryU = new Subsidiary();
        subsidiaryU.setId(idChildUpdate);
        subsidiaryU.setUserClient(client);
        subsidiaryListNew.add(subsidiaryU);
        
        List<UUID> subsidiaryListOld = new ArrayList<>();
        subsidiaryListOld.add(idChildOld);
        subsidiaryListOld.add(idChildUpdate);
        
        client.setSubsidiaries(subsidiaryListNew);
        
        Mockito.when(subsidiaryChildRepository.findByFather(client)).thenReturn(subsidiaryListOld);
        Mockito.when(userClientRepository.save(client)).thenReturn(client);
        
        service.update(client);
        
        Mockito.verify(subsidiaryRepository, Mockito.times(1)).save(subsidiaryN);
        Mockito.verify(subsidiaryRepository, Mockito.times(1)).save(subsidiaryU);
        Mockito.verify(subsidiaryRepository, Mockito.times(1)).delete(idChildOld);
    	
    }
	
	@Test
	public void getByIdTest() throws Exception{
		UUID id = UUID.randomUUID();
		
		subsidiary.setUserClient(client);
		subsidiaryList.add(subsidiary);
		client.setSubsidiaries(subsidiaryList);
		
		Mockito.when(userClientRepository.findOne(id)).thenReturn(client);
		Mockito.when(subsidiaryRepository.findByUserClient(client)).thenReturn(subsidiaryList);
		
		UserClient retrieved = service.getById(id);
		ListValidation.childsLisAndFathertValidation(UserClient.class, retrieved);
		
		Assert.assertNotNull(retrieved);
	}

}

package com.mrzolution.integridad.app.services;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.*;
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
public class RetentionServicesTest {

	@InjectMocks
	RetentionServices service;

	@Mock
	RetentionRepository retentionRepository;
	@Mock
	DetailRetentionRepository detailRetentionRepository;
	@Mock
	DetailRetentionChildRepository detailRetentionChildRepository;
	@Mock
	SubsidiaryRepository subsidiaryRepository;
	@Mock
	CashierRepository cashierRepository;
//	@Mock
//	UserClientRepository userClientRepository;
	
	Retention retention;
	
	@Before
	public void setupTest(){
		retention = Retention.newRetentionTest();
	}
	
	@Test
	public void getByUserLazyTest() throws Exception{
		List<Retention> retentions = new ArrayList<>();
		retentions.add(retention);
		
		UserIntegridad user = new UserIntegridad();
		
		Mockito.when(retentionRepository.findRetentionByUserIntegridad(user)).thenReturn(retentions);
		
		Iterable<Retention> retentionList = service.getByUserLazy(user);
		Assert.assertEquals(1, Iterables.size(retentionList));
		for(Retention retention: retentionList){
			ListValidation.checkListsAndFatherNull(Retention.class, retention);
		}
		
	}
	
	@Test
	public void getByIdTest() throws Exception{
		UUID id = UUID.randomUUID();

		DetailRetention detail = DetailRetention.newDetailRetentionTest();
		List<DetailRetention> details = new ArrayList<>();
		details.add(detail);
		retention.setDetailRetentions(details);

		Mockito.when(retentionRepository.findOne(id)).thenReturn(retention);
		Mockito.when(detailRetentionRepository.findByRetention(retention)).thenReturn(details);

		Retention retrieved = service.getById(id);
		ListValidation.childsLisAndFathertValidation(Retention.class, retrieved);

		Assert.assertNotNull(retrieved);
	}

    @Test
    public void onUpdateShouldCallRightChildrenRepository(){
    	UUID id = UUID.randomUUID();
        retention.setId(id);

        UUID idChildOld = UUID.randomUUID();
        UUID idChildNew = UUID.randomUUID();
        UUID idChildUpdate = UUID.randomUUID();

        List<DetailRetention> detailListNew = new ArrayList<>();
		DetailRetention detailN = new DetailRetention();
        detailN.setId(idChildNew);
        detailN.setRetention(retention);
        detailListNew.add(detailN);

		DetailRetention detailU = new DetailRetention();
        detailU.setId(idChildUpdate);
        detailU.setRetention(retention);
        detailListNew.add(detailU);

        List<UUID> detailListOld = new ArrayList<>();
        detailListOld.add(idChildOld);
        detailListOld.add(idChildUpdate);

        retention.setDetailRetentions(detailListNew);

        Mockito.when(detailRetentionChildRepository.findByFather(retention)).thenReturn(detailListOld);
        Mockito.when(retentionRepository.save(retention)).thenReturn(retention);

        service.updateRetention(retention);

        Mockito.verify(detailRetentionRepository, Mockito.times(1)).save(Mockito.any(Iterable.class));
//        Mockito.verify(detailRetentionRepository, Mockito.times(1)).save(detailU);
        Mockito.verify(detailRetentionRepository, Mockito.times(1)).delete(idChildOld);

    }

	@Test
	public void createCallDetailRepository(){
		UUID idCashier = UUID.randomUUID();
		DetailRetention detail = DetailRetention.newDetailRetentionTest();
		List<DetailRetention> detailList = new ArrayList<>();
		detailList.add(detail);
		retention.getUserIntegridad().getCashier().setId(idCashier);
		retention.getUserIntegridad().getCashier().setBillNumberSeq(1);
		Cashier cashier = retention.getUserIntegridad().getCashier();
		retention.setDetailRetentions(detailList);

		Mockito.when(cashierRepository.findOne(idCashier)).thenReturn(cashier);
		Mockito.when(retentionRepository.save(Mockito.any(Retention.class))).thenReturn(Retention.newRetentionTest());
		Mockito.when(subsidiaryRepository.findOne(Mockito.any(UUID.class))).thenReturn(Subsidiary.newSubsidiaryTest());

		Retention response = service.createRetention(retention);

		Mockito.verify(retentionRepository, Mockito.times(1)).save(Mockito.any(Retention.class));
		Mockito.verify(detailRetentionRepository, Mockito.times(1)).save(detail);

		Assert.assertTrue(!response.getDetailRetentions().isEmpty());

	}

	@Test
	public void createAddOneToSeqOnSubsidiary(){
		UUID idCashier = UUID.randomUUID();
		UUID idSubsidiary = UUID.randomUUID();
		retention.getSubsidiary().setId(idSubsidiary);
		retention.getSubsidiary().setBillNumberSeq(1);
		retention.getUserIntegridad().getCashier().setId(idCashier);
		retention.getUserIntegridad().getCashier().setBillNumberSeq(1);
		Cashier cashier = retention.getUserIntegridad().getCashier();
		Subsidiary subsidiary = retention.getSubsidiary();
		DetailRetention detail = DetailRetention.newDetailRetentionTest();
		List<DetailRetention> detailList = new ArrayList<>();
		detailList.add(detail);
		retention.setDetailRetentions(detailList);

		Mockito.when(retentionRepository.save(Mockito.any(Retention.class))).thenReturn(Retention.newRetentionTest());
		Mockito.when(cashierRepository.findOne(idCashier)).thenReturn(cashier);
		Mockito.when(subsidiaryRepository.findOne(idSubsidiary)).thenReturn(subsidiary);

		service.createRetention(retention);


		Mockito.verify(cashierRepository, Mockito.times(1)).findOne(idCashier);
		Mockito.verify(cashierRepository, Mockito.times(1)).save(cashier);

	}

	@Test(expected=BadRequestException.class)
	public void validatAtLeastOneDetail(){
		retention.setDetailRetentions(null);
		service.createRetention(retention);
	}
}

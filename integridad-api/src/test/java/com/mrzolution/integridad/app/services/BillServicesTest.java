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
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.domain.Subsidiary;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.repositories.BillRepository;
import com.mrzolution.integridad.app.repositories.DetailChildRepository;
import com.mrzolution.integridad.app.repositories.DetailRepository;
import com.mrzolution.integridad.app.repositories.SubsidiaryRepository;

@RunWith(MockitoJUnitRunner.class)
public class BillServicesTest {
	
	@InjectMocks
	BillServices service;
	
	@Mock
	BillRepository billRepository;
	@Mock
	DetailRepository detailRepository;
	@Mock
	DetailChildRepository detailChildRepository;
	@Mock
	SubsidiaryRepository subsidiaryRepository;
	
	Bill bill;
	
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
	
	@Test
	public void getByIdTest() throws Exception{
		UUID id = UUID.randomUUID();
		
		Detail detail = Detail.newDetailTest();
		List<Detail> details = new ArrayList<>();
		details.add(detail);
		bill.setDetails(details);
		
		Mockito.when(billRepository.findOne(id)).thenReturn(bill);
		Mockito.when(detailRepository.findByBill(bill)).thenReturn(details);
		
		Bill retrieved = service.getById(id);
		ListValidation.childsLisAndFathertValidation(Bill.class, retrieved);
		
		Assert.assertNotNull(retrieved);
	}
	
	@Test
    public void onUpdateShouldCallRightChildrenRepository(){
    	UUID id = UUID.randomUUID();
        bill.setId(id);
        
        UUID idChildOld = UUID.randomUUID();
        UUID idChildNew = UUID.randomUUID();
        UUID idChildUpdate = UUID.randomUUID();
        
        List<Detail> detailListNew = new ArrayList<>();
        Detail detailN = new Detail();
        detailN.setId(idChildNew);
        detailN.setBill(bill);
        detailListNew.add(detailN);

        Detail detailU = new Detail();
        detailU.setId(idChildUpdate);
        detailU.setBill(bill);
        detailListNew.add(detailU);
        
        List<UUID> detailListOld = new ArrayList<>();
        detailListOld.add(idChildOld);
        detailListOld.add(idChildUpdate);
        
        bill.setDetails(detailListNew);
        
        Mockito.when(detailChildRepository.findByFather(bill)).thenReturn(detailListOld);
        Mockito.when(billRepository.save(bill)).thenReturn(bill);
        
        service.update(bill);
        
        Mockito.verify(detailRepository, Mockito.times(1)).save(detailN);
        Mockito.verify(detailRepository, Mockito.times(1)).save(detailU);
        Mockito.verify(detailRepository, Mockito.times(1)).delete(idChildOld);
    	
    }
	
	@Test
	public void createCallDetailRepository(){
		Detail detail = Detail.newDetailTest();
		List<Detail> detailList = new ArrayList<>();
		detailList.add(detail);
		bill.setDetails(detailList);
		
		Mockito.when(billRepository.save(Mockito.any(Bill.class))).thenReturn(Bill.newBillTest());
		Mockito.when(subsidiaryRepository.findOne(Mockito.any(UUID.class))).thenReturn(Subsidiary.newSubsidiaryTest());
		
		Bill response = service.create(bill);
		
		Mockito.verify(billRepository, Mockito.times(1)).save(Mockito.any(Bill.class));
		Mockito.verify(detailRepository, Mockito.times(1)).save(detail);
		
		Assert.assertTrue(!response.getDetails().isEmpty());
		
	}
	
	@Test
	public void createAddOneToSeqOnSubsidiary(){
		UUID idSubsidiary = UUID.randomUUID();
		bill.getSubsidiary().setId(idSubsidiary);
		bill.getSubsidiary().setBillNumberSeq(1);
		Subsidiary subsidiary = bill.getSubsidiary(); 
		Detail detail = Detail.newDetailTest();
		List<Detail> detailList = new ArrayList<>();
		detailList.add(detail);
		bill.setDetails(detailList);
		
		Mockito.when(billRepository.save(Mockito.any(Bill.class))).thenReturn(Bill.newBillTest());
		Mockito.when(subsidiaryRepository.findOne(idSubsidiary)).thenReturn(subsidiary);
		
		service.create(bill);
		
		subsidiary.setBillNumberSeq(2);
		
		Mockito.verify(subsidiaryRepository, Mockito.times(1)).findOne(idSubsidiary);
		Mockito.verify(subsidiaryRepository, Mockito.times(1)).save(subsidiary);
		
	}
	
	@Test(expected=BadRequestException.class)
	public void validatAtLeastOneDetail(){
		bill.setDetails(null);
		service.create(bill);
	}
}

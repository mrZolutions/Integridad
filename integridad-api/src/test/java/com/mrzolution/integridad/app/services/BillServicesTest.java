package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.domain.*;
import com.mrzolution.integridad.app.repositories.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.exceptions.BadRequestException;

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
        KardexRepository kardexRepository;
        @Mock
        KardexChildRepository kardexChildRepository;
	@Mock
	SubsidiaryRepository subsidiaryRepository;
	@Mock
	CashierRepository cashierRepository;
	@Mock
	ProductBySubsidiairyRepository productBySubsidiairyRepository;
	@Mock
	PagoRepository pagoRepository;
	@Mock
	UserClientRepository userClientRepository;
	@Mock
	CreditsRepository creditsRepository;
	@Mock
	PaymentRepository paymentRepository;
	@Mock
	ConfigCuentasServices configCuentasServices;
	@Mock
	CuentaContableByProductRepository cuentaContableByProductRepository;
	@Mock
	DailybookFvServices dailybookFvServices;
	@Mock
	CuentaContableRepository cuentaContableRepository;

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
                List<Kardex> detailsk = new ArrayList<>();
		details.add(detail);
		bill.setDetails(details);
                
		Mockito.when(billRepository.findOne(id)).thenReturn(bill);
		Mockito.when(detailRepository.findByBill(bill)).thenReturn(details);
                Mockito.when(kardexRepository.findByBill(bill)).thenReturn(detailsk);
		Mockito.when(pagoRepository.findByBill(bill)).thenReturn(new ArrayList<>());
		
		Bill retrieved = service.getBillById(id);
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
        
        service.updateBill(bill);
        
        Mockito.verify(detailRepository, Mockito.times(1)).save(Mockito.any(Iterable.class));
//        Mockito.verify(detailRepository, Mockito.times(1)).save(detailU);
        Mockito.verify(detailRepository, Mockito.times(1)).delete(idChildOld);
    	
    }

	@Test
	public void createCallDetailRepository(){
		UUID idCashier = UUID.randomUUID();
		Pago pago = Pago.newPagoTest();
                Detail detail = Detail.newDetailTest();
                Kardex detailk = Kardex.newKardexTest();
		List<Pago> pagoList = new ArrayList<>();
		List<Detail> detailList = new ArrayList<>();
                List<Kardex> detailsKardex = new ArrayList<>();
		detail.getProduct().getProductType().setCode("SER");
                detailk.getProduct().getProductType().setCode("SER");
		pagoList.add(pago);
                detailList.add(detail);
                detailsKardex.add(detailk);
		
                
                bill.getUserIntegridad().getCashier().setId(idCashier);
		bill.getUserIntegridad().getCashier().setBillNumberSeq(1);
		
                ProductBySubsidiary ps = ProductBySubsidiary.newProductBySubsidiaryTest();
		ps.setQuantity(Long.valueOf(1));
		
                Cashier cashier = bill.getUserIntegridad().getCashier();
		
                bill.setPagos(pagoList);
                bill.setDetails(detailList);
                bill.setDetailsKardex(detailsKardex);

		Mockito.when(productBySubsidiairyRepository.
				findBySubsidiaryIdAndProductId(Mockito.any(UUID.class), Mockito.any(UUID.class))).thenReturn(ps);
		Mockito.when(cashierRepository.findOne(idCashier)).thenReturn(cashier);
		Mockito.when(billRepository.save(Mockito.any(Bill.class))).thenReturn(Bill.newBillTest());
		Mockito.when(subsidiaryRepository.findOne(Mockito.any(UUID.class))).thenReturn(Subsidiary.newSubsidiaryTest());
		Mockito.when(configCuentasServices.getCuentasByUserCliendIdAndOptionCode(Mockito.any(UUID.class), Mockito.anyString())).thenReturn(new ConfigCuentas());
		Mockito.when(cuentaContableByProductRepository.findByProductId(Mockito.any(UUID.class))).thenReturn(new ArrayList<>());
		Mockito.when(cashierRepository.findOne(Mockito.any(UUID.class))).thenReturn(cashier);
		Mockito.when(cuentaContableRepository.findByUserClientAndCode(Mockito.any(), Mockito.anyString())).thenReturn(new CuentaContable());
		Mockito.when(dailybookFvServices.createDailybookFv(Mockito.any())).thenReturn(new DailybookFv());

		Bill response = service.createBill(bill, null, null,1);
		
		Mockito.verify(billRepository, Mockito.times(1)).save(Mockito.any(Bill.class));
		Mockito.verify(detailRepository, Mockito.times(1)).save(detail);
		Mockito.verify(pagoRepository, Mockito.times(1)).save(Mockito.any(Pago.class));
		Mockito.verify(cashierRepository, Mockito.times(2)).save(cashier);
                Mockito.verify(kardexRepository, Mockito.times(1)).save(detailk);
		
		Assert.assertTrue(!response.getDetails().isEmpty());
		
	}

	@Test
	public void createQuotationShouldntCallPagoNorCashierNorProductBySub(){
            UUID idCashier = UUID.randomUUID();
		
                Detail detail = Detail.newDetailTest();
		List<Detail> detailList = new ArrayList<>();
		detail.getProduct().getProductType().setCode("SER");
		detailList.add(detail);
		
                bill.getUserIntegridad().getCashier().setId(idCashier);
		bill.getUserIntegridad().getCashier().setQuotationNumberSeq(1);
		
                ProductBySubsidiary ps = ProductBySubsidiary.newProductBySubsidiaryTest();
		ps.setQuantity(Long.valueOf(1));
		
                Cashier cashier = bill.getUserIntegridad().getCashier();
		
                bill.setDetails(detailList);

		Mockito.when(productBySubsidiairyRepository.findBySubsidiaryIdAndProductId(Mockito.any(UUID.class), Mockito.any(UUID.class))).thenReturn(ps);
		Mockito.when(cashierRepository.findOne(idCashier)).thenReturn(cashier);
		Mockito.when(billRepository.save(Mockito.any(Bill.class))).thenReturn(Bill.newBillTest());
		Mockito.when(subsidiaryRepository.findOne(Mockito.any(UUID.class))).thenReturn(Subsidiary.newSubsidiaryTest());
		Mockito.when(cuentaContableByProductRepository.findByProductId(Mockito.any(UUID.class))).thenReturn(new ArrayList<>());
		Mockito.when(cashierRepository.findOne(Mockito.any(UUID.class))).thenReturn(cashier);
		Mockito.when(cuentaContableRepository.findByUserClientAndCode(Mockito.any(), Mockito.anyString())).thenReturn(new CuentaContable());
		Mockito.when(dailybookFvServices.createDailybookFv(Mockito.any())).thenReturn(new DailybookFv());

		Bill response = service.createBill(bill, null,null, 0);

		Mockito.verify(billRepository, Mockito.times(1)).save(Mockito.any(Bill.class));
		Mockito.verify(cashierRepository, Mockito.times(2)).save(cashier);
                Mockito.verify(detailRepository, Mockito.times(1)).save(Mockito.any(Detail.class));
		Mockito.verify(pagoRepository, Mockito.times(0)).save(Mockito.any(Pago.class));

		Assert.assertTrue(!response.getDetails().isEmpty());

	}

	@Test
	public void createAddOneToSeqOnSubsidiary(){
		UUID idCashier = UUID.randomUUID();
		UUID idSubsidiary = UUID.randomUUID();
		bill.getSubsidiary().setId(idSubsidiary);
		bill.getSubsidiary().setBillNumberSeq(1);
		bill.getUserIntegridad().getCashier().setId(idCashier);
		bill.getUserIntegridad().getCashier().setBillNumberSeq(1);
		
                ProductBySubsidiary ps = ProductBySubsidiary.newProductBySubsidiaryTest();
		ps.setQuantity(Long.valueOf(1));
		Cashier cashier = bill.getUserIntegridad().getCashier();
		Subsidiary subsidiary = bill.getSubsidiary(); 
		
                Detail detail = Detail.newDetailTest();
		List<Detail> detailList = new ArrayList<>();
		detail.getProduct().getProductType().setCode("SER");
		detailList.add(detail);
		bill.setDetails(detailList);
                
                Kardex detailk = Kardex.newKardexTest();
                List<Kardex> detailsKardex = new ArrayList<>();
                detailk.getProduct().getProductType().setCode("SER");
                detailsKardex.add(detailk);
                bill.setDetailsKardex(detailsKardex);

		Mockito.when(productBySubsidiairyRepository.
				findBySubsidiaryIdAndProductId(Mockito.any(UUID.class), Mockito.any(UUID.class))).thenReturn(ps);
		Mockito.when(billRepository.save(Mockito.any(Bill.class))).thenReturn(Bill.newBillTest());
		Mockito.when(cashierRepository.findOne(idCashier)).thenReturn(cashier);
//		Mockito.when(subsidiaryRepository.findOne(idSubsidiary)).thenReturn(subsidiary);
		Mockito.when(cuentaContableByProductRepository.findByProductId(Mockito.any(UUID.class))).thenReturn(new ArrayList<>());
		Mockito.when(cashierRepository.findOne(Mockito.any(UUID.class))).thenReturn(cashier);
		Mockito.when(cuentaContableRepository.findByUserClientAndCode(Mockito.any(), Mockito.anyString())).thenReturn(new CuentaContable());
		Mockito.when(dailybookFvServices.createDailybookFv(Mockito.any())).thenReturn(new DailybookFv());
		
		service.createBill(bill, null, null,1);

		
		Mockito.verify(cashierRepository, Mockito.times(1)).findOne(idCashier);
		Mockito.verify(cashierRepository, Mockito.times(2)).save(cashier);

	}
	
	@Test(expected=BadRequestException.class)
	public void validateAtLeastOneDetail(){
		bill.setDetails(null);
		service.createBill(bill, null, null,1);
	}

	@Test(expected=BadRequestException.class)
	public void validateAtLeastOneDetailOnQutation(){
		bill.setDetails(null);
		service.createBill(bill, null, null, 0);
	}

	@Test(expected=BadRequestException.class)
	public void validateAtLeastOnePago(){
		Detail detail = Detail.newDetailTest();
		List<Detail> detailList = new ArrayList<>();
		detailList.add(detail);
		bill.setDetails(detailList);
		bill.setPagos(null);
		service.createBill(bill, null, null, 1);
	}

}

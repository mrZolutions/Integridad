package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.Provider;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiaryChildRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import com.mrzolution.integridad.app.repositories.ProviderRepository;
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
public class ProviderServicesTest {
	
	@InjectMocks
	ProviderServices service;

	@Mock
	ProviderRepository providerRepository;

	Provider provider;

	@Before
	public void setupTest(){
		provider = Provider.newProviderTest();
		provider.setCodeIntegridad("123");
	}
	
	@Test
	public void getByIdTest() throws Exception{
		UUID id = UUID.randomUUID();
		provider.setId(id);
		
		Mockito.when(providerRepository.findOne(id)).thenReturn(provider);

		Provider retrieved = service.getById(id);
		ListValidation.childsLisAndFathertValidation(Provider.class, retrieved);
		
		Assert.assertNotNull(retrieved);
	}

//	@Test
//	public void onUpdateShouldCallRightChildrenRepository(){
//		UUID id = UUID.randomUUID();
//		product.setId(id);
//
//		UUID idChildOld = UUID.randomUUID();
//		UUID idChildNew = UUID.randomUUID();
//		UUID idChildUpdate = UUID.randomUUID();
//
//		List<ProductBySubsidiary> productBySubListNew = new ArrayList<>();
//		ProductBySubsidiary prodBySubN = new ProductBySubsidiary();
//		prodBySubN.setId(idChildNew);
//		prodBySubN.setProduct(product);
//		productBySubListNew.add(prodBySubN);
//
//		ProductBySubsidiary prodBySubU = new ProductBySubsidiary();
//		prodBySubU.setId(idChildUpdate);
//		prodBySubU.setProduct(product);
//		productBySubListNew.add(prodBySubU);
//
//		List<UUID> detailListOld = new ArrayList<>();
//		detailListOld.add(idChildOld);
//		detailListOld.add(idChildUpdate);
//
//		product.setProductBySubsidiaries(productBySubListNew);
//
//		Mockito.when(productBySubsidiaryChildRepository.findByFather(product)).thenReturn(detailListOld);
//		Mockito.when(productRepository.save(product)).thenReturn(product);
//
//		service.update(product);
//
//		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1)).save(prodBySubN);
//		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1)).save(prodBySubU);
//		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1)).delete(idChildOld);
//
//	}

	@Test
	public void createCallProviderBySubRepository(){

		Mockito.when(providerRepository.save(Mockito.any(Provider.class))).thenReturn(provider);

		Provider response = service.create(provider);

		Mockito.verify(providerRepository, Mockito.times(1))
				.save(Mockito.any(Provider.class));
//		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1))
//				.save(Mockito.any(ProductBySubsidiary.class));

//		Assert.assertTrue(response.getProductBySubsidiaries() == null);

	}
	
}

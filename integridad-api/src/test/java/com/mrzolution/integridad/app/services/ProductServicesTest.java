package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiaryChildRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.repositories.ProductRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProductServicesTest {
	
	@InjectMocks
	ProductServices service;
	
	@Mock
	ProductRepository productRepository;
	@Mock
	ProductBySubsidiairyRepository productBySubsidiaryRepository;
	@Mock
	ProductBySubsidiaryChildRepository productBySubsidiaryChildRepository;
	
	Product product;
	ProductBySubsidiary productBySubsidiary;

	List<ProductBySubsidiary> productBySubsidiaryList = new ArrayList<>();
	
	@Before
	public void setupTest(){
		product = Product.newProducTest();

		productBySubsidiary = ProductBySubsidiary.newProductBySubsidiaryTest();
		productBySubsidiary.setProduct(product);

		productBySubsidiaryList.add(productBySubsidiary);

		product.setProductBySubsidiaries(productBySubsidiaryList);
	}
	
	//@Test
	//public void getByIdTest() throws Exception{
	//	UUID id = UUID.randomUUID();
	//	product.setId(id);
	//	
	//	Mockito.when(productRepository.findOne(id)).thenReturn(product);
	//	Mockito.when(productBySubsidiaryRepository.findByProductId(id)).thenReturn(productBySubsidiaryList);

	//	Product retrieved = service.getProductById(id);
	//	ListValidation.childsLisAndFathertValidation(Product.class, retrieved);
		
	//	Assert.assertNotNull(retrieved);
	//}

	@Test
	public void onUpdateShouldCallRightChildrenRepository(){
		UUID id = UUID.randomUUID();
		product.setId(id);

		UUID idChildOld = UUID.randomUUID();
		UUID idChildNew = UUID.randomUUID();
		UUID idChildUpdate = UUID.randomUUID();

		List<ProductBySubsidiary> productBySubListNew = new ArrayList<>();
		ProductBySubsidiary prodBySubN = new ProductBySubsidiary();
		prodBySubN.setId(idChildNew);
		prodBySubN.setProduct(product);
		productBySubListNew.add(prodBySubN);

		ProductBySubsidiary prodBySubU = new ProductBySubsidiary();
		prodBySubU.setId(idChildUpdate);
		prodBySubU.setProduct(product);
		productBySubListNew.add(prodBySubU);

		List<UUID> detailListOld = new ArrayList<>();
		detailListOld.add(idChildOld);
		detailListOld.add(idChildUpdate);

		product.setProductBySubsidiaries(productBySubListNew);

		Mockito.when(productBySubsidiaryChildRepository.findByFather(product)).thenReturn(detailListOld);
		Mockito.when(productRepository.save(product)).thenReturn(product);

		service.updateProduct(product);

		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1)).save(Mockito.any(Iterable.class));
//		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1)).save(prodBySubU);
		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1)).delete(idChildOld);

	}

	@Test
	public void createCallProdBySubRepository(){

		Mockito.when(productRepository.findByCodeIntegridadAndClientId(Mockito.anyString(), Mockito.any(UUID.class)))
				.thenReturn(new ArrayList<>());
		Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);

		Product response = service.createProduct(product);

		Mockito.verify(productRepository, Mockito.times(1))
				.save(Mockito.any(Product.class));
		Mockito.verify(productBySubsidiaryRepository, Mockito.times(1))
				.save(Mockito.any(ProductBySubsidiary.class));

		Assert.assertTrue(response.getProductBySubsidiaries() == null);

	}
	
}

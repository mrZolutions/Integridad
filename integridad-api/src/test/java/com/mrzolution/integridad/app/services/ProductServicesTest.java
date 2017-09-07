package com.mrzolution.integridad.app.services;

import java.util.UUID;

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
	
	Product product;
	
	@Before
	public void setupTest(){
		product = Product.newProducTest();
	}
	
	@Test
	public void getByIdTest() throws Exception{
		UUID id = UUID.randomUUID();
		product.setId(id);
		
		Mockito.when(productRepository.findOne(id)).thenReturn(product);
		
		Product retrieved = service.getById(id);
		ListValidation.childsLisAndFathertValidation(Product.class, retrieved);
		
		Assert.assertNotNull(retrieved);
	}
	
}

package com.mrzolution.integridad.app.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.repositories.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductServices {
	
	@Autowired
	ProductRepository productRepository;
	
	public Product create(Product product){
		return productRepository.save(product);
	}
	
	public Product update(Product product){
		return productRepository.save(product);
	}
	
	public Product getById(UUID id){
		return productRepository.findOne(id);
	}
	
	public Iterable<Product> getAllActives(){
		log.info("ProductServices getAllActives");
		return productRepository.findByActive(true);
		
	}
}

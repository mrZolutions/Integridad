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
		log.info("ProductServices create");
		return productRepository.save(product);
	}
	
	public Product update(Product product){
		log.info("ProductServices update");
		return productRepository.save(product);
	}
	
	public Product getById(UUID id){
		log.info("ProductServices getById: {}", id);
		Product findOne = productRepository.findOne(id);
		populateChildren(findOne);
		return findOne;
	}
	
	public Iterable<Product> getAllActives(){
		log.info("ProductServices getAllActives");
		Iterable<Product> actives = productRepository.findByActive(true);
		actives.forEach(this::populateChildren);
		return actives;
		
	}
	
	private void populateChildren(Product product) {
		log.info("ProductServices populateChildren productId: {}", product.getId());
		product.setFatherListToNull();
		log.info("ProductServices populateChildren FINISHED productId: {}", product.getId());
		
	}
}

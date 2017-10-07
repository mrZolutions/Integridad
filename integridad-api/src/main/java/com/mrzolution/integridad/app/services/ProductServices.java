package com.mrzolution.integridad.app.services;

import java.util.Date;
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
		product.setActive(true);
		product.setDateCreated(new Date().getTime());
		product.setLastDateUpdated(new Date().getTime());
		return productRepository.save(product);
	}
	
	public void update(Product product){
		log.info("ProductServices update");
		product.setLastDateUpdated(new Date().getTime());
		product.getSubsidiary().setFatherListToNull();
		product.getUserClient().setFatherListToNull();
		productRepository.save(product);
	}
	
	public Product getById(UUID id){
		log.info("ProductServices getById: {}", id);
		Product findOne = productRepository.findOne(id);
		populateChildren(findOne);
		return findOne;
	}
	
	public Product delete(UUID productId) {
		log.info("ProductServices delete: {}", productId);
		Product findOne = productRepository.findOne(productId);
		findOne.setListsNull();
		findOne.setActive(false);
		update(findOne);
		return findOne;
	}
	
	public Iterable<Product> getAllActives(){
		log.info("ProductServices getAllActives");
		Iterable<Product> actives = productRepository.findByActive(true);
		actives.forEach(this::populateChildren);
		return actives;
		
	}
	
	public Iterable<Product> getAllActivesByUserClientIdAndActive(UUID userClientId) {
		log.info("ProductServices getAllActivesByUserClientIdAndActive");
		Iterable<Product> actives = productRepository.findByUserClientIdAndActive(userClientId);
		actives.forEach(prodcut -> {
			prodcut.setFatherListToNull();
		});
		return actives;
	}
	
	private void populateChildren(Product product) {
		log.info("ProductServices populateChildren productId: {}", product.getId());
		product.setFatherListToNull();
		log.info("ProductServices populateChildren FINISHED productId: {}", product.getId());
		
	}
}

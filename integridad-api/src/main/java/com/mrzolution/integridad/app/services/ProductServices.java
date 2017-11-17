package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiaryChildRepository;
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
	@Autowired
	ProductBySubsidiairyRepository productBySubsidiairyRepository;
	@Autowired
	ProductBySubsidiaryChildRepository productBySubsidiaryChildRepository;
	
	public Product create(Product product){
		log.info("ProductServices create");
		product.setActive(true);
		product.setDateCreated(new Date().getTime());
		product.setLastDateUpdated(new Date().getTime());

		List<ProductBySubsidiary> productBySubsidiaryList = product.getProductBySubsidiaries();
		product.setListsNull();

		Product saved = productRepository.save(product);
		productBySubsidiaryList.forEach(productBySubsidiary -> {
			productBySubsidiary.setProduct(saved);
			productBySubsidiary.setFatherListToNull();

			productBySubsidiairyRepository.save(productBySubsidiary);
		});

		return saved;
	}
	
	public void update(Product product){
		log.info("ProductServices update: {}", product.getId());
		product.setLastDateUpdated(new Date().getTime());

		Father<Product, ProductBySubsidiary> father =
				new Father<>(product, product.getProductBySubsidiaries());
		FatherManageChildren fatherUpdateChildren =
				new FatherManageChildren(father, productBySubsidiaryChildRepository, productBySubsidiairyRepository);
		fatherUpdateChildren.updateChildren();

		log.info("ProductServices CHILDREN updated: {}", product.getId());

		product.setListsNull();
		product.setFatherListToNull();
		Product updated = productRepository.save(product);
		log.info("ProductServices update id: {}", updated.getId());
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
		actives.forEach(product -> {
			product.setFatherListToNull();
			populateChildren(product);
		});
		return actives;
	}
	
	public Iterable<Product> getAllActivesBySubsidiaryIdAndActive(UUID subsidiaryId) {
		log.info("ProductServices getAllActivesBySubsidiaryIdAndActive");
		Iterable<UUID> productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActive(subsidiaryId);
		List<Product> listReturn = new ArrayList<>();
		productIdList.forEach(id ->{
			listReturn.add(getById(id));
			Product product = getById(id);
		});

//		Iterable<Product> actives = productRepository.findBySubsidiaryIdAndActive(subsidiaryId);
//		actives.forEach(prodcut -> {
//			prodcut.setFatherListToNull();
//		});
		return listReturn;
	}
	
	private void populateChildren(Product product) {
		log.info("ProductServices populateChildren productId: {}", product.getId());
		List<ProductBySubsidiary> productBySubsidiaryList = new ArrayList<>();
		Iterable<ProductBySubsidiary> productBySubsidiaries = productBySubsidiairyRepository.findByProductId(product.getId());

		productBySubsidiaries.forEach(productBySubsidiaryConsumer -> {
			productBySubsidiaryConsumer.setListsNull();
			productBySubsidiaryConsumer.setFatherListToNull();
			productBySubsidiaryConsumer.getSubsidiary().setFatherListToNull();
			productBySubsidiaryConsumer.getSubsidiary().setListsNull();
			productBySubsidiaryConsumer.setProduct(null);

			productBySubsidiaryList.add(productBySubsidiaryConsumer);
		});

		product.setProductBySubsidiaries(productBySubsidiaryList);
		product.setFatherListToNull();
		log.info("ProductServices populateChildren FINISHED productId: {}", product.getId());
		
	}
}

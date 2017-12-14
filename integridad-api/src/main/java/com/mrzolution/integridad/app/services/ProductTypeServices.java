package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.ProductType;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiaryChildRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import com.mrzolution.integridad.app.repositories.ProductTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ProductTypeServices {

	@Autowired
	ProductTypeRepository productTypeRepository;
	@Autowired
	ProductRepository productRepository;
	
	public ProductType create(ProductType productType){
		log.info("ProductTypeServices create");
		productType.setActive(true);

		ProductType saved = productTypeRepository.save(productType);

		saved.setListsNull();

		return saved;
	}
	
	public void update(ProductType productType){
		log.info("ProductTypeServices update: {}", productType.getId());

		productType.setListsNull();
		ProductType updated = productTypeRepository.save(productType);
		log.info("ProductTypeServices update id: {}", updated.getId());
	}

	public ProductType delete(UUID productTypeId) {
		log.info("ProductServices delete: {}", productTypeId);
		ProductType findOne = productTypeRepository.findOne(productTypeId);
		findOne.setListsNull();
		findOne.setActive(false);
		update(findOne);
		return findOne;
	}

	public Iterable<ProductType> getAllActives(){
		log.info("ProductTypeServices getAllActives");
		Iterable<ProductType> actives = productTypeRepository.findByActive(true);
		actives.forEach(this::populateChildren);
		return actives;

	}

	public Iterable<ProductType> getAllActivesLazy(){
		log.info("ProductTypeServices getAllActivesLazy");
		Iterable<ProductType> actives = productTypeRepository.findByActive(true);
		actives.forEach(productType -> {productType.setListsNull();});
		return actives;

	}
//
//	public Product getById(UUID id){
//		log.info("ProductServices getById: {}", id);
//		Product findOne = productRepository.findOne(id);
//		populateChildren(findOne);
//		return findOne;
//	}
//
//
//
//
//	public Iterable<Product> getAllActivesByUserClientIdAndActive(UUID userClientId) {
//		log.info("ProductServices getAllActivesByUserClientIdAndActive");
//		Iterable<Product> actives = productRepository.findByUserClientIdAndActive(userClientId);
//		actives.forEach(prodcut -> {
//			prodcut.setFatherListToNull();
//		});
//		return actives;
//	}
//
//	public Iterable<Product> getAllActivesBySubsidiaryIdAndActive(UUID subsidiaryId) {
//		log.info("ProductServices getAllActivesBySubsidiaryIdAndActive");
//		Iterable<UUID> productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActive(subsidiaryId);
//		List<Product> listReturn = new ArrayList<>();
//		productIdList.forEach(id ->{
//			listReturn.add(getById(id));
//			Product productType = getById(id);
//		});
//
////		Iterable<Product> actives = productRepository.findBySubsidiaryIdAndActive(subsidiaryId);
////		actives.forEach(prodcut -> {
////			prodcut.setFatherListToNull();
////		});
//		return listReturn;
//	}
//
	private void populateChildren(ProductType productType) {
		log.info("ProductTypeServices populateChildren producTypetId: {}", productType.getId());
		List<Product> productList = new ArrayList<>();
		Iterable<Product> products = productRepository.findByProductTypeIdAndActive(productType.getId());

		products.forEach(productConsumer -> {
			productConsumer.setListsNull();
			productConsumer.setFatherListToNull();
			productConsumer.setProductType(null);

			productList.add(productConsumer);
		});

		productType.setProducts(productList);
		log.info("ProductTypeServices populateChildren FINISHED producTypetId: {}", productType.getId());

	}
}
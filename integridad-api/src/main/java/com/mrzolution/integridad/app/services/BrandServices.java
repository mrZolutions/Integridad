package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductType;
import com.mrzolution.integridad.app.repositories.BrandRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import com.mrzolution.integridad.app.repositories.ProductTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class BrandServices {

	@Autowired
	BrandRepository brandRepository;
	@Autowired
	ProductRepository productRepository;
	
	public Brand create(Brand brand){
		log.info("BrandServices create");
		brand.setActive(true);

		Brand saved = brandRepository.save(brand);

		saved.setListsNull();

		return saved;
	}
	
	public void update(Brand brand){
		log.info("BrandServices update: {}", brand.getId());

		brand.setListsNull();
		Brand updated = brandRepository.save(brand);
		log.info("BrandServices update id: {}", updated.getId());
	}

	public Brand delete(UUID brandId) {
		log.info("BrandServices delete: {}", brandId);
		Brand findOne = brandRepository.findOne(brandId);
		findOne.setListsNull();
		findOne.setActive(false);
		update(findOne);
		return findOne;
	}

	public Iterable<Brand> getAllActives(){
		log.info("BrandServices getAllActives");
		Iterable<Brand> actives = brandRepository.findByActive(true);
		actives.forEach(this::populateChildren);
		return actives;

	}

	public Iterable<Brand> getAllActivesLazy(){
		log.info("BrandServices getAllActivesLazy");
		Iterable<Brand> actives = brandRepository.findByActive(true);
		actives.forEach(brand -> {brand.setListsNull();});
		return actives;

	}

	public Brand getById(UUID id){
		log.info("BrandServices getById: {}", id);
		Brand findOne = brandRepository.findOne(id);
		populateChildren(findOne);
		return findOne;
	}

	private void populateChildren(Brand brand) {
		log.info("BrandServices populateChildren brandId: {}", brand.getId());
		List<Product> productList = new ArrayList<>();
		Iterable<Product> products = productRepository.findByBrandIdAndActive(brand.getId());

		products.forEach(productConsumer -> {
			productConsumer.setListsNull();
			productConsumer.setFatherListToNull();
			productConsumer.setBrand(null);

			productList.add(productConsumer);
		});

		brand.setProducts(productList);
		log.info("BrandServices populateChildren FINISHED brandId: {}", brand.getId());

	}
}

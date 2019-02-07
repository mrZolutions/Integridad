package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.repositories.BrandRepository;
import com.mrzolution.integridad.app.repositories.ProductRepository;
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
	
    public Brand createBrand(Brand brand){
	log.info("BrandServices createBrand");
	brand.setActive(true);
	Brand saved = brandRepository.save(brand);
	saved.setListsNull();
	return saved;
    }
	
    public void updateBrand(Brand brand){
	log.info("BrandServices updateBrand: {}", brand.getId());
	brand.setListsNull();
	Brand updated = brandRepository.save(brand);
	log.info("BrandServices update id: {}", updated.getId());
    }

    public Brand deleteBrand(UUID brandId) {
	log.info("BrandServices deleteBrand: {}", brandId);
	Brand findOne = brandRepository.findOne(brandId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateBrand(findOne);
	return findOne;
    }

    public Iterable<Brand> getAllActives(){
	log.info("BrandServices getAllActives");
	Iterable<Brand> actives = brandRepository.findByActive(true);
	actives.forEach(this::populateChildren);
	return actives;
    }

    public Iterable<Brand> getAllActivesLazy(UUID projectId){
	log.info("BrandServices getAllActivesLazy");
	Iterable<Brand> actives = brandRepository.findByUserClienteIdAndActive(projectId);
	actives.forEach(brand -> {
            brand.setFatherListToNull();
            brand.setListsNull();
	});
	return actives;
    }

    public Brand getBrandById(UUID id){
	log.info("BrandServices getBrandById: {}", id);
	Brand findOne = brandRepository.findOne(id);
	populateChildren(findOne);
	return findOne;
    }

    private void populateChildren(Brand brand) {
	List<Product> productList = new ArrayList<>();
	Iterable<Product> products = productRepository.findByBrandIdAndActive(brand.getId());
	products.forEach(productConsumer -> {
            productConsumer.setListsNull();
            productConsumer.setFatherListToNull();
            productConsumer.setBrand(null);
            productList.add(productConsumer);
	});
	brand.setProducts(productList);
    }
}

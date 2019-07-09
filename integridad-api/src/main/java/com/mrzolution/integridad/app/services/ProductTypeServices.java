package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductType;
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
public class ProductTypeServices {
    @Autowired
    ProductTypeRepository productTypeRepository;
    @Autowired
    ProductRepository productRepository;
	
    public ProductType createProductType(ProductType productType) {
        productType.setActive(true);
	ProductType saved = productTypeRepository.save(productType);
	saved.setListsNull();
        log.info("ProductTypeServices createProductType: {}, {}", saved.getId(), saved.getName());
	return saved;
    }
	
    public void updateProductType(ProductType productType) {
    	log.info("ProductTypeServices updateProductType: {}", productType.getId());
	productType.setListsNull();
	ProductType updated = productTypeRepository.save(productType);
	log.info("ProductTypeServices updateProductType: {}", updated.getId());
    }

    public ProductType deleteProductType(UUID productTypeId) {
        ProductType findOne = productTypeRepository.findOne(productTypeId);
        findOne.setListsNull();
        findOne.setActive(false);
        updateProductType(findOne);
        log.info("ProductServices deleteProductType: {}", productTypeId);
        return findOne;
    }

    public Iterable<ProductType> getAllActives(){
        Iterable<ProductType> actives = productTypeRepository.findByActive(true);
        actives.forEach(this::populateChildren);
        log.info("ProductTypeServices getAllActives");
        return actives;
    }

    public Iterable<ProductType> getAllActivesLazy(){
        Iterable<ProductType> actives = productTypeRepository.findByActive(true);
        actives.forEach(productType -> {productType.setListsNull();});
        log.info("ProductTypeServices getAllActivesLazy");
        return actives;
    }

    private void populateChildren(ProductType productType) {
        List<Product> productList = new ArrayList<>();
	Iterable<Product> products = productRepository.findByProductTypeIdAndActive(productType.getId());
        products.forEach(productConsumer -> {
            productConsumer.setListsNull();
            productConsumer.setFatherListToNull();
            productConsumer.setProductType(null);
            productList.add(productConsumer);
	});
        productType.setProducts(productList);
    }
}
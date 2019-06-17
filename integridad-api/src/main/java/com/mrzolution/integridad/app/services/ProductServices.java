package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.CuentaContableByProduct;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.CuentaContableByProductRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiaryChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;

@Slf4j
@Component
public class ProductServices {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductBySubsidiairyRepository productBySubsidiairyRepository;
    @Autowired
    ProductBySubsidiaryChildRepository productBySubsidiaryChildRepository;
    @Autowired
    CuentaContableByProductRepository cuentaContableByProductRepository;
        
    public Product createProduct(Product product) throws BadRequestException {
    	Iterable<Product> products = productRepository.findByCodeIntegridadAndClientId(product.getCodeIntegridad(), product.getUserClient().getId());
        if (Iterables.size(products) > 0) {
            throw new BadRequestException("CODIGO DUPLICADO");
        }
        product.setActive(true);
        product.setDateCreated(new Date().getTime());
        product.setLastDateUpdated(new Date().getTime());
        List<ProductBySubsidiary> productBySubsidiaryList = product.getProductBySubsidiaries();
        List<CuentaContableByProduct> cuentaContableByProductList = product.getCuentaContableByProducts();
        product.setListsNull();
        Product saved = productRepository.save(product);
        productBySubsidiaryList.forEach(productBySubsidiary -> {
            productBySubsidiary.setProduct(saved);
            productBySubsidiary.setFatherListToNull();
            productBySubsidiairyRepository.save(productBySubsidiary);
        });

        if(cuentaContableByProductList != null){
            cuentaContableByProductList.forEach(cc -> {
                cc.setProduct(saved);
                cc.setFatherListToNull();
                cuentaContableByProductRepository.save(cc)  ;
            });
        }
        return saved;
    }
	
    //@Async("asyncExecutor")
    public void updateProduct(Product product) {
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
	log.info("ProductServices updateProduct DONE id: {}", updated.getId());
    }
	
    public Product getProductById(UUID id) {
	log.info("ProductServices getProductById: {}", id);
	Product findOne = productRepository.findOne(id);
	populateChildren(findOne);
	return findOne;
    }
	
    public Product deleteProduct(UUID productId) {
	log.info("ProductServices deleteProduct: {}", productId);
	Product findOne = productRepository.findOne(productId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateProduct(findOne);
	return findOne;
    }
	
    public Iterable<Product> getAllActives() {
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
        
    public Page<Product> getAllActivesBySubsidiaryIdAndActive(UUID subsidiaryId, String variable, Pageable pageable) {
	log.info("ProductServices getAllActivesBySubsidiaryIdAndActive");
	Page<UUID> productIdList;
	if (variable.equals("null")) {
            log.info("ProductServices getAllActivesBySubsidiaryIdAndActive without variable");
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActive(subsidiaryId, pageable);
	} else {
            log.info("ProductServices getAllActivesBySubsidiaryIdAndActive with variable");
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIAndVariabledAndProductActive(subsidiaryId, variable, new PageRequest(0, 150, Sort.Direction.ASC, "product"));
	}
	List<Product> listReturn = new ArrayList<>();
	productIdList.forEach(page -> {
	    Product p = getProductById(page);
	    p.setCuentaContableByProducts(null);
	    listReturn.add(p);
	});
	Page<Product> products = new PageImpl<>(listReturn, pageable, productIdList.getTotalElements());
	return products;
    }

    public Page<Product> getAllActivesBySubsidiaryIdForBill(UUID subsidiaryId, String variable, Pageable pageable) {
        log.info("ProductServices getAllActivesBySubsidiaryIdForBill");
        Page<UUID> productIdList;
        if (variable.equals("null")) {
            log.info("ProductServices getAllActivesBySubsidiaryIdForBill without variable");
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActiveForBill(subsidiaryId, pageable);
        } else {
            log.info("ProductServices getAllActivesBySubsidiaryIdForBill with variable");
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIAndVariabledAndProductActiveForBill(subsidiaryId, variable, new PageRequest(0, 150, Sort.Direction.ASC, "product"));
        }
        List<Product> listReturn = new ArrayList<>();
        productIdList.forEach(page -> {
            Product p = getProductById(page);
            p.setCuentaContableByProducts(null);
            listReturn.add(p);
        });
        Page<Product> products = new PageImpl<>(listReturn, pageable, productIdList.getTotalElements());
        return products;
    }
        
    private void populateChildren(Product product) {
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
        if (product.getBrand() != null) {
            product.getBrand().setFatherListToNull();
            product.getBrand().setListsNull();
	}
	product.setFatherListToNull();	
    }
}

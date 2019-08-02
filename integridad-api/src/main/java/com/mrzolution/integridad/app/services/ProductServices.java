package com.mrzolution.integridad.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.google.common.collect.Iterables;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.father.Father;
import com.mrzolution.integridad.app.father.FatherManageChildren;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiairyRepository;
import com.mrzolution.integridad.app.repositories.ProductBySubsidiaryChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.report.ExistencyReport;
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
    
    public String observ;
    public String grupo;
    public String subGrupo;
    public String marca;
    public String linea;
    public String codigo;
    public long cantidad;
    public long minimo;
        
    public Product createProduct(Product product) throws BadRequestException {
    	Iterable<Product> products = productRepository.findByCodeIntegridadAndClientId(product.getCodeIntegridad(), product.getUserClient().getId());
        if (Iterables.size(products) > 0) {
            throw new BadRequestException("CODIGO DUPLICADO");
        }
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
        log.info("ProductServices createProduct: {}", saved.getId());
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
	product.setListsNull();
	product.setFatherListToNull();
	Product updated = productRepository.save(product);
    }
	
    public Product getProductById(UUID id) {
	Product findOne = productRepository.findOne(id);
	populateChildren(findOne);
	return findOne;
    }
	
    public Product deleteProduct(UUID productId) {
	Product findOne = productRepository.findOne(productId);
	findOne.setListsNull();
	findOne.setActive(false);
	updateProduct(findOne);
	return findOne;
    }
	
    public Iterable<Product> getAllActives() {
	Iterable<Product> actives = productRepository.findByActive(true);
	actives.forEach(this::populateChildren);
	return actives;
    }
	
    public Iterable<Product> getAllActivesByUserClientIdAndActive(UUID userClientId) {
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
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActive(subsidiaryId, pageable);
	} else {
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIAndVariabledAndProductActive(subsidiaryId, variable, new PageRequest(0, 150, Sort.Direction.ASC, "product"));
	}
	List<Product> listReturn = new ArrayList<>();
	productIdList.forEach(page -> {
            listReturn.add(getProductById(page));
	});
	Page<Product> products = new PageImpl<>(listReturn, pageable, productIdList.getTotalElements());
	return products;
    }
    
    public List<ExistencyReport> getProductsForExistencyReport(UUID userClientId) {
        log.info("ProductServices getProductsForExistency: {}", userClientId);
        codigo = "SER";
        Iterable<Product> productos = productRepository.findProductsForExistencyReport(userClientId, codigo);
        List<ExistencyReport> existencyReportList = new ArrayList<>();
        
        productos.forEach(product -> {
            populateForExistency(product);
            
            Double costoReal = Double.valueOf(0);
            Double costoCash = Double.valueOf(0);
            Double costoCard = Double.valueOf(0);
            Double costoCredit = Double.valueOf(0);
            Double costoMayor = Double.valueOf(0);
            
            for (ProductBySubsidiary pss : product.getProductBySubsidiaries()) {
                if (pss.getQuantity() != null && pss.isActive()) {
                    cantidad = Long.sum(cantidad, pss.getQuantity());
                } else {
                    cantidad = 0;
                }
            }
            
            if (product.getMaxMinimun() != null) {
                minimo = product.getMaxMinimun();
            } else {
                minimo = 0;
            }
            
            if (cantidad <= minimo) {
                observ = "CANTIDAD IGUAL O POR DEBAJO DEL MÍNIMO";
            } else {
                observ = "--";
            }
            
            if (product.getAverageCost() != null) {
                costoReal = product.getAverageCost();
            } else {
                costoReal = 0.0;
            }
            
            if (product.getCashPercentage() != null) {
                costoCash = costoReal + (costoReal * (product.getCashPercentage() / 100));
            } else {
                costoCash = 0.0;
            }
            
            if (product.getCardPercentage() != null) {
                costoCard = costoReal + (costoReal * (product.getCardPercentage() / 100));
            } else {
                costoCard = 0.0;
            }
            
            if (product.getCreditPercentage() != null) {
                costoCredit = costoReal + (costoReal * (product.getCreditPercentage() / 100));
            } else {
                costoCredit = 0.0;
            }
            
            if (product.getMajorPercentage() != null) {
                costoMayor = costoReal + (costoReal * (product.getMajorPercentage() / 100));
            } else {
                costoMayor = 0.0;
            }
            
            if (product.getSubgroup() != null) {
                if (product.getSubgroup().getGroupLine() != null) {
                    if (product.getSubgroup().getGroupLine().getLine() != null) {
                        linea = product.getSubgroup().getGroupLine().getLine().getName();
                    } else {
                        linea = "NO ASIGNADO";
                    }
                    grupo = product.getSubgroup().getGroupLine().getName();
                } else {
                    grupo = "NO ASIGNADO";
                }
                subGrupo = product.getSubgroup().getName();
            } else {
                subGrupo = "NO ASIGNADO";
            }
            
            if (product.getBrand() != null) {
                marca = product.getBrand().getName();
            } else {
                marca = "NO ASIGNADO";
            }
                        
            ExistencyReport existencyReport = new ExistencyReport(product.getCodeIntegridad(), product.getName(), product.getProductType().getName(), costoReal, costoCash,
                                                                  costoCard, costoCredit, costoMayor, minimo, cantidad, grupo, subGrupo, marca, linea, observ);
            existencyReportList.add(existencyReport);
        });
        return existencyReportList;
    }
    
    private void populateForExistency(Product products) {
        List<ProductBySubsidiary> psList = new ArrayList<>();
        Iterable<ProductBySubsidiary> pss = productBySubsidiairyRepository.findByProduct(products);
        pss.forEach(ps -> {
            ps.setListsNull();
            ps.setFatherListToNull();
            ps.setProduct(null);
            psList.add(ps);
        });
        products.setProductBySubsidiaries(psList);
        products.setFatherListToNull();
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
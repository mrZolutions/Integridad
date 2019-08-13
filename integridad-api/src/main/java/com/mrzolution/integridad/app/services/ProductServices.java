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
import com.mrzolution.integridad.app.domain.report.ExistencyCatReport;
import com.mrzolution.integridad.app.domain.report.ExistencyReport;
import com.mrzolution.integridad.app.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

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
    
    public String observ;
    public String grupo;
    public String subGrupo;
    public String marca;
    public String linea;
    public String codigo;
    public long cantidad;
    public long minimo;
        
    @Async("asyncExecutor")
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

        log.info("ProductServices createProduct id: {}", saved.getId());
        return saved;
    }
	
    @Async("asyncExecutor")
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
        log.info("ProductServices updateProduct: {}", updated.getId());
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
        log.info("ProductServices deleteProduct: {}", productId);
	return findOne;
    }
	
    public Iterable<Product> getAllActives() {
	Iterable<Product> actives = productRepository.findByActive(true);
	actives.forEach(this::populateChildren);
        log.info("ProductServices getAllActives");
	return actives;
    }
	
    public Iterable<Product> getAllActivesByUserClientIdAndActive(UUID userClientId) {
	Iterable<Product> actives = productRepository.findByUserClientIdAndActive(userClientId);
	actives.forEach(product -> {
            product.setFatherListToNull();
            populateChildren(product);
	});
        log.info("ProductServices getAllActivesByUserClientIdAndActive");
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
                    cantidad = pss.getQuantity();
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
            
            ExistencyReport existencyReport = new ExistencyReport(product.getCodeIntegridad(), product.getName(), product.getProductType().getName(), costoReal, costoCash,
                                                                  costoCard, costoCredit, costoMayor, minimo, cantidad, grupo, subGrupo, marca, linea, observ);
            existencyReportList.add(existencyReport);
        });
        return existencyReportList;
    }
    
    public List<ExistencyCatReport> getProductsForExistencyCatReport(UUID userClientId) {
        log.info("ProductServices getProductsForExistencyCatReport: {}", userClientId);
        codigo = "SER";
        Iterable<Product> productosCat = productRepository.findProductsForExistencyReport(userClientId, codigo);
        List<ExistencyCatReport> existencyCatReportList = new ArrayList<>();
        
        productosCat.forEach(productCat -> {
            populateForExistency(productCat);
            
            Double costoCatReal = Double.valueOf(0);
            Double costoCatCash = Double.valueOf(0);
            Double costoCatCard = Double.valueOf(0);
            Double costoCatCredit = Double.valueOf(0);
            Double costoCatMayor = Double.valueOf(0);
            
            for (ProductBySubsidiary pss : productCat.getProductBySubsidiaries()) {
                if (pss.getQuantity() != null && pss.isActive()) {
                    cantidad = pss.getQuantity();
                } else {
                    cantidad = 0;
                }
            }
            
            if (productCat.getAverageCost() != null) {
                costoCatReal = productCat.getAverageCost();
            } else {
                costoCatReal = 0.0;
            }
            
            if (productCat.getCashPercentage() != null) {
                costoCatCash = costoCatReal + (costoCatReal * (productCat.getCashPercentage() / 100));
            } else {
                costoCatCash = 0.0;
            }
            
            if (productCat.getCardPercentage() != null) {
                costoCatCard = costoCatReal + (costoCatReal * (productCat.getCardPercentage() / 100));
            } else {
                costoCatCard = 0.0;
            }
            
            if (productCat.getCreditPercentage() != null) {
                costoCatCredit = costoCatReal + (costoCatReal * (productCat.getCreditPercentage() / 100));
            } else {
                costoCatCredit = 0.0;
            }
            
            if (productCat.getMajorPercentage() != null) {
                costoCatMayor = costoCatReal + (costoCatReal * (productCat.getMajorPercentage() / 100));
            } else {
                costoCatMayor = 0.0;
            }
            
            ExistencyCatReport existencyCatReport = new ExistencyCatReport(productCat.getCodeIntegridad(), productCat.getName(), costoCatReal, costoCatCash,
                                                                           costoCatCard, costoCatCredit, costoCatMayor, cantidad);
            existencyCatReportList.add(existencyCatReport);
        });
        return existencyCatReportList;
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
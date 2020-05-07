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
import com.mrzolution.integridad.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.report.ExistencyCatReport;
import com.mrzolution.integridad.app.domain.report.ExistencyReport;
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
    CuentaContableByProductChildRepository cuentaContableByProductChildRepository;
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
    private void createProductChildren(Product saved, List<ProductBySubsidiary> productBySubsidiaryList, List<CuentaContableByProduct> cuentaContableByProductList){
        log.info("ProductServices createProductChildren STARTED");
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
        log.info("ProductServices createProductChildren FINISHED");
    }

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

        createProductChildren(saved, productBySubsidiaryList, cuentaContableByProductList);

        log.info("ProductServices createProduct id: {}", saved.getId());
        return saved;
    }
    
    public Product updateProduct(Product product) {
        product.setLastDateUpdated(new Date().getTime());
        product.setListsNull();
        product.setFatherListToNull();
	    Product updated = productRepository.save(product);
        log.info("ProductServices updateProduct: {}", updated.getId());
        return updated;
    }

    @Async("asyncExecutor")
    public void updateProductChildrenCC(Product product) {
        product.setLastDateUpdated(new Date().getTime());
        Father<Product, CuentaContableByProduct> fatherCC = new Father<>(product, product.getCuentaContableByProducts());
        FatherManageChildren fatherUpdateChildrenCC = new FatherManageChildren(fatherCC, cuentaContableByProductChildRepository, cuentaContableByProductRepository);
        fatherUpdateChildrenCC.updateChildren();

        log.info("ProductServices updateChildrenProductCC: {}", product.getId());
    }

    @Async("asyncExecutor")
    public void updateProductChildrenEdited(Product product) {
        product.setLastDateUpdated(new Date().getTime());
        Father<Product, ProductBySubsidiary> father = new Father<>(product, product.getProductBySubsidiaries());
        FatherManageChildren fatherUpdateChildren = new FatherManageChildren(father, productBySubsidiaryChildRepository, productBySubsidiairyRepository);
        fatherUpdateChildren.updateChildren();

        log.info("ProductServices updateChildrenProductEdited: {}", product.getId());
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
	
    public Iterable<Product> getProductsActives() {
	Iterable<Product> actives = productRepository.findByActive(true);
	actives.forEach(this::populateChildren);
        log.info("ProductServices getProductsActives");
	return actives;
    }
	
    public Iterable<Product> getProductsActivesByUserClientId(UUID userClientId) {
	Iterable<Product> actives = productRepository.findByUserClientIdAndActive(userClientId);
	actives.forEach(product -> {
            product.setFatherListToNull();
            populateChildren(product);
	});
        log.info("ProductServices getProductsActivesByUserClientId");
	return actives;
    }
    
    public Iterable<Product> getProductsActivesByUserClientIdAndCodeInteg(UUID userClientId, String code) {
        Iterable<Product> products = productRepository.findProdByUserClientIdAndCodeIntegActive(userClientId, code);
        products.forEach(prod ->{
            prod.setListsNull();
            prod.setFatherListToNull();
        });
        log.info("ProductServices getProductsActivesByUserClientIdAndCodeInteg: {}, {}", userClientId, code);
        return products;
    }
        
    public Page<Product> getProductsActivesBySubsidiaryId(UUID subsidiaryId, String variable, Pageable pageable) {
	log.info("ProductServices getProductsActivesBySubsidiaryId");
	Page<UUID> productIdList;
	if (variable.equals("null")) {
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActive(subsidiaryId, pageable);
	} else {
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndVariabledAndProductActive(subsidiaryId, variable, new PageRequest(0, 150, Sort.Direction.ASC, "product"));
	}
	List<Product> listReturn = new ArrayList<>();
	productIdList.forEach(page -> {
	    Product p = getProductById(page);
//	    p.setCuentaContableByProducts(null);
	    listReturn.add(p);
	});
	Page<Product> products = new PageImpl<>(listReturn, pageable, productIdList.getTotalElements());
	return products;
    }

    public List<Product> getProductsActivesBySubsidiaryIdBarCode(UUID subsidiaryId, String variable) {
        log.info("ProductServices getProductsActivesBySubsidiaryIdBarCode: " + subsidiaryId + " - " + variable);
        Iterable<Product> productIdList;
        productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndBarCodedAndProductActive(subsidiaryId, variable);
        List<Product> listReturn = new ArrayList<>();
        productIdList.forEach(ps -> {
            populateChildren(ps);
            ps.setCuentaContableByProducts(null);
            listReturn.add(ps);
        });
        return listReturn;
    }
    
    public Page<Product> getProductsActivesBySubsidiaryIdForBill(UUID subsidiaryId, String variable, Pageable pageable) {
        log.info("ProductServices getProductsActivesBySubsidiaryIdForBill");
        Page<UUID> productIdList;
        if (variable.equals("null")) {
            log.info("ProductServices getProductsActivesBySubsidiaryIdForBill without variable");
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndProductActiveForBill(subsidiaryId, pageable);
        } else {
            log.info("ProductServices getProductsActivesBySubsidiaryIdForBill with variable");
            productIdList = productBySubsidiairyRepository.findBySubsidiaryIdAndVariabledAndProductActiveForBill(subsidiaryId, variable, new PageRequest(0, 150, Sort.Direction.ASC, "product"));
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
        Iterable<Product> productos = productRepository.findPrdsForExistReport(userClientId, codigo);
        List<ExistencyReport> existencyReportList = new ArrayList<>();
        
        productos.forEach(product -> {
            populateForExistency(product);
            
            Double costoReal = Double.valueOf(0);
            Double costoCash = Double.valueOf(0);
            Double costoCard = Double.valueOf(0);
            Double costoCredit = Double.valueOf(0);
            Double costoMayor = Double.valueOf(0);
            
            for (ProductBySubsidiary pss : product.getProductBySubsidiaries()) {
                if (pss.isActive()) {
                    if (pss.getQuantity() != null) {
                        cantidad = pss.getQuantity();
                    } else {
                        cantidad = 0;
                    }
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
    
    public List<ExistencyCatReport> getProductsForExistencyCatReport(UUID userClientId) {
        log.info("ProductServices getProductsForExistencyCatReport: {}", userClientId);
        codigo = "SER";
        Iterable<Product> productosCat = productRepository.findPrdsForExistReport(userClientId, codigo);
        List<ExistencyCatReport> existencyCatReportList = new ArrayList<>();
        
        productosCat.forEach(productCat -> {
            populateForExistency(productCat);
            
            Double costoCatReal = Double.valueOf(0);
            Double costoCatCash = Double.valueOf(0);
            long minimo = 0;
            Double promedio = Double.valueOf(0);
            
            for (ProductBySubsidiary pss : productCat.getProductBySubsidiaries()) {
                if (pss.isActive()) {
                    if (pss.getQuantity() != null) {
                        cantidad = pss.getQuantity();
                    } else {
                        cantidad = 0;
                    }
                }
            }
            
            if (productCat.getAverageCostSuggested() != null) {
                promedio = productCat.getAverageCostSuggested();
            } else {
                promedio = 0.0;
            }
            
            if (productCat.getMaxMinimun() != null) {
                minimo = productCat.getMaxMinimun();
            } else {
                minimo = 0;
            }
            
            if (productCat.getAverageCost() != null) {
                costoCatReal = productCat.getAverageCost();
            } else {
                costoCatReal = 0.0;
            }
            
            ExistencyCatReport existencyCatReport = new ExistencyCatReport(productCat.getCodeIntegridad(), productCat.getName(),
                                                                           costoCatReal, cantidad, promedio, minimo);
            existencyCatReportList.add(existencyCatReport);
        });
        return existencyCatReportList;
    }

    public String getLastCodeBySubsidiaryId(UUID subsidiaryId) {
        log.info("ProductServices getLastCodeBySubsidiaryId");
        Page<Product> products = productRepository.findFirstByUserClientIdAndActive(subsidiaryId, new PageRequest(0, 1));
        Product product = products.getContent().get(0);
        return product.getCodeIntegridad();
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

        List<CuentaContableByProduct> cuentasList = new ArrayList<>();
        Iterable<CuentaContableByProduct> cuentas = cuentaContableByProductRepository.findByProductId(product.getId());
        cuentas.forEach(cuenta ->{
            cuenta.setListsNull();
            cuenta.setFatherListToNull();
            cuenta.getCuentaContable().setFatherListToNull();
            cuenta.getCuentaContable().setListsNull();
            cuentasList.add(cuenta);
        });

        product.setProductBySubsidiaries(productBySubsidiaryList);
        product.setCuentaContableByProducts(cuentasList);
        if (product.getBrand() != null) {
            product.getBrand().setFatherListToNull();
            product.getBrand().setListsNull();
        }
        product.setFatherListToNull();
    }
}
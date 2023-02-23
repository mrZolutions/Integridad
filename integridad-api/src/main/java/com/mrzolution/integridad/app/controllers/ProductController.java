package com.mrzolution.integridad.app.controllers;

import java.util.UUID;

import com.mrzolution.integridad.app.domain.CuentaContableByProduct;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.domain.ProductWrapper;
import com.mrzolution.integridad.app.domain.report.ExistencyReportV2;
import com.mrzolution.integridad.app.services.ProductRemoveDetailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.report.ExistencyCatReport;
import com.mrzolution.integridad.app.domain.report.ExistencyReport;
import com.mrzolution.integridad.app.exceptions.BadRequestException;
import com.mrzolution.integridad.app.services.ProductServices;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/integridad/v1/product")
public class ProductController {
    @Autowired
    ProductServices service;
    @Autowired
    ProductRemoveDetailServices productRemoveDetailServices;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createProduct(@RequestBody Product product) {
	Product response = null;
	try {
            response = service.createProduct(product);
	} catch (BadRequestException e) {
            log.error("ProductController createProduct Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController createProduct DONE");
	return new ResponseEntity<Product>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateProduct(@RequestBody Product product) {
        try {
            List<CuentaContableByProduct> cuentaContableByProducts = product.getCuentaContableByProducts();
            service.updateProduct(product);
            product.setCuentaContableByProducts(cuentaContableByProducts);
            service.updateProductChildrenCC(product);
	} catch (BadRequestException e) {
            log.error("ProductController updateProduct Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController updateProduct DONE");
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/edted")
    public ResponseEntity updateProductEdited(@RequestBody Product product) {
        try {
            List<ProductBySubsidiary> productBySubsidiaries = product.getProductBySubsidiaries();
            List<CuentaContableByProduct> cuentaContableByProducts = product.getCuentaContableByProducts();
            service.updateProduct(product);
            product.setProductBySubsidiaries(productBySubsidiaries);
            product.setCuentaContableByProducts(cuentaContableByProducts);
            service.updateProductChildrenEdited(product);
	} catch (BadRequestException e) {
            log.error("ProductController updateProductEdited Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController updateProductEdited DONE");
	return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.PUT, value="/remove")
    public ResponseEntity removeProduct(@RequestBody ProductWrapper productWrapper) {
        try {
            Product productUpdated = service.updateProduct(productWrapper.getProduct());
            productWrapper.setProduct(productUpdated);
            productRemoveDetailServices.createFromWrapper(productWrapper);
        } catch (BadRequestException e) {
            log.error("ProductController removeProduct Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController removeProduct DONE");
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{productId}")
    public ResponseEntity deleteProduct(@PathVariable("productId") UUID productId) {
        Product response = null;
	try {
            response = service.deleteProduct(productId);
	} catch (BadRequestException e) {
            log.error("ProductController deleteProduct Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController deleteProduct DONE");
	return new ResponseEntity<Product>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{productId}")
    public ResponseEntity getProductById(@PathVariable(value = "productId") UUID productId) {
        Product response = null;
	try {
            response = service.getProductById(productId);
	} catch (BadRequestException e) {
            log.error("ProductController getProductById Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductById DONE");
	return new ResponseEntity<Product>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives")
    public ResponseEntity getProductsActives() {
        Iterable<Product> response = null;
	try {
            response = service.getProductsActives();
	} catch (BadRequestException e) {
            log.error("ProductController getProductsActives Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductsActives DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/prod/{userClientId}/{code}")
    public ResponseEntity getProductsActivesByUserClientIdAndCodeInteg(@PathVariable("userClientId") UUID userClientId, @PathVariable("code") String code) {
        Iterable<Product> response = null;
        try {
            response = service.getProductsActivesByUserClientIdAndCodeInteg(userClientId, code);
        } catch (BadRequestException e) {
            log.error("ProductController getProductsActivesByUserClientIdAndCodeInteg Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductsActivesByUserClientIdAndCodeInteg DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives/user_client/{userClientId}")
    public ResponseEntity getProductsActivesByUserClientId(@PathVariable("userClientId") UUID userClientId) {
        Iterable<Product> response = null;
	try {
            response = service.getProductsActivesByUserClientId(userClientId);
	} catch (BadRequestException e) {
            log.error("ProductController getProductsActivesByUserClientId Exception thrown: {}", e.getMessage());	    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductsActivesByUserClientId DONE");
	return new ResponseEntity<Iterable>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives/subsidiary/{subsidiaryId}/{page}/{lineId}")
    public ResponseEntity getProductsActivesBySubsidiaryId(@PathVariable("subsidiaryId") UUID subsidiaryId, @PathVariable("lineId") String lineIdParam,
                                                           @PathVariable("page") int page, @RequestParam(required = false, name = "var") String variable) {
        Page<Product> response = null;
        UUID lineId = null;
        if( !"undefined".equals(lineIdParam)) lineId = UUID.fromString(lineIdParam);
	try {
	    System.out.println(subsidiaryId + " - " + variable + " - " + lineId);
            response = service.getProductsActivesBySubsidiaryId(subsidiaryId, variable, lineId, new PageRequest(page, 50, Sort.Direction.ASC, "product"));
	} catch (BadRequestException e) {
            log.error("ProductController getProductsActivesBySubsidiaryId Exception thrown: {}", e.getMessage());	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductsActivesBySubsidiaryId DONE");
        return new ResponseEntity<Page>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives/subsidiary/bar/{subsidiaryId}")
    public ResponseEntity getProductsActivesBySubsidiaryIdBarCode(@PathVariable("subsidiaryId") UUID subsidiaryId, @RequestParam(required = false, name = "var") String variable) {
        List<Product> response = null;
        try {
            response = service.getProductsActivesBySubsidiaryIdBarCode(subsidiaryId, variable);
        } catch (BadRequestException e) {
            log.error("ProductController getProductsActivesBySubsidiaryIdBarCode Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductsActivesBySubsidiaryIdBarCode DONE");
        return new ResponseEntity<List>(response, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/existency/{userClientId}")
    public ResponseEntity getProductsForExistencyReport(@PathVariable("userClientId") UUID userClientId) {
        List<ExistencyReport> response = null;
        try {
            response = service.getProductsForExistencyReport(userClientId);
	} catch (BadRequestException e) {
            log.error("ProductController getProductsForExistencyReport Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ProductController getProductsForExistencyReport DONE");
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/rep/existency/V2/{userClientId}")
    public ResponseEntity getProductsForExistencyReportV2(@PathVariable("userClientId") UUID userClientId) {
        List<ExistencyReportV2> response = null;
        try {
            response = service.getProductsForExistencyReportV2(userClientId);
        } catch (BadRequestException e) {
            log.error("ProductController getProductsForExistencyReport Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getProductsForExistencyReportV2 DONE");
        return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/rep/existency/cat/{userClientId}")
    public ResponseEntity getProductsForExistencyCatReport(@PathVariable("userClientId") UUID userClientId) {
        List<ExistencyCatReport> response = null;
        try {
            response = service.getProductsForExistencyCatReport(userClientId);
	} catch (BadRequestException e) {
            log.error("ProductController getProductsForExistencyCatReport Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
        log.info("ProductController getProductsForExistencyCatReport DONE");
	return new ResponseEntity<List>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/actives/subsidiary/bill/{subsidiaryId}/{page}")
    public ResponseEntity getProductsActivesBySubsidiaryIdForBill(@PathVariable("subsidiaryId") UUID subsidiaryId, @PathVariable("page") int page, @RequestParam(required = false, name = "var") String variable) {
        log.info("ProductController getAllActivesBySubsidiaryId: {}", subsidiaryId);
        Page<Product> response = null;
        try {
            response = service.getProductsActivesBySubsidiaryIdForBill(subsidiaryId, variable, new PageRequest(page, 50, Sort.Direction.ASC, "product"));
        } catch (BadRequestException e) {
            log.error("ProductController getProductsActivesBySubsidiaryIdForBill Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return new ResponseEntity<Page>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value="/active/code/user_client/{userClientId}")
    public ResponseEntity getLastCodeActiveByUserClientId(@PathVariable("userClientId") UUID userClientId) {
        String response = null;
        try {
            response = service.getLastCodeBySubsidiaryId(userClientId);
        } catch (BadRequestException e) {
            log.error("ProductController getLastCodeActiveByUserClientId Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController getLastCodeActiveByUserClientId DONE");
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value="/list")
    public ResponseEntity createProductLista(@RequestBody List<Product> products) {
        int response = 0;
        try {
            response = service.createProductList(products);
        } catch (BadRequestException e) {
            log.error("ProductController createProduct Exception thrown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        log.info("ProductController createProduct DONE");
        return new ResponseEntity<Integer>(response, HttpStatus.CREATED);
    }
}
package com.mrzolution.integridad.app.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Product;

@Repository
@Qualifier(value="ProductRepository")
public interface ProductRepository extends CrudRepository<Product, UUID> {
    Iterable<Product> findByActive(boolean active);

    @Query("SELECT p FROM Product p WHERE p.id in (:ids) AND p.active = true")
    Iterable<Product> findByListIdAndActive(@Param("ids") List<UUID> productIds);
	
    @Query("SELECT p FROM Product p WHERE p.userClient.id = (:id) AND p.active = true")
    Iterable<Product> findByUserClientIdAndActive(@Param("id") UUID userClientId);
   
    @Query("SELECT p FROM Product p WHERE p.productType.id = (:id) AND p.active = true")
    Iterable<Product> findByProductTypeIdAndActive(@Param("id") UUID productTypeId);

    @Query("SELECT p FROM Product p WHERE p.brand.id = (:id) AND p.active = true")
    Iterable<Product> findByBrandIdAndActive(@Param("id") UUID brandId);

    @Query("SELECT p FROM Product p WHERE p.subgroup.id = (:id) AND p.active = true")
    Iterable<Product> findBySubGroupIdAndActive(@Param("id") UUID brandId);

    @Query("SELECT p FROM Product p WHERE p.userClient.id = (:userClientId) AND p.codeIntegridad = (:code) AND p.active = true")
    Iterable<Product> findProdByUserClientIdAndCodeIntegActive(@Param("userClientId") UUID userClientId, @Param("code") String code);

    @Query("SELECT p FROM Product p WHERE p.codeIntegridad = (:code) AND  p.userClient.id = (:clientId) AND p.active = true")
    Iterable<Product> findByCodeIntegridadAndClientId(@Param("code")String code, @Param("clientId") UUID clientId);
    
    @Query("SELECT p FROM Product p WHERE p.userClient.id = (:userClientId) AND p.productType.code != (:code) AND p.active = true ORDER BY p.name")
    Iterable<Product> findPrdsForExistReport(@Param("userClientId") UUID userClientId, @Param("code") String code);
    
    @Query("SELECT p FROM Product p WHERE p.userClient.id = (:userClientId) AND p.id = (:id) AND p.active = true")
    Product findPrdByUsrClntAndId(@Param("userClientId") UUID userClientId, @Param("id") UUID id);

    @Query("SELECT p FROM Product p WHERE p.userClient.id = (:id) AND p.active = true order by p.dateCreated desc")
    Page<Product> findFirstByUserClientIdAndActive(@Param("id") UUID userClientId, Pageable pageable);
}
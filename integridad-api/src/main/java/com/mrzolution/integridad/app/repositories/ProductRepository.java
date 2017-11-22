package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Product;

@Repository
@Qualifier(value="ProductRepository")
public interface ProductRepository extends CrudRepository<Product, UUID>{
	
	Iterable<Product> findByActive(boolean active);
	
	@Query("SELECT p FROM Product p WHERE p.userClient.id = (:id) and p.active = true")
	Iterable<Product> findByUserClientIdAndActive(@Param("id") UUID userClientId);

	@Query("SELECT p FROM Product p WHERE p.productType.id = (:id) and p.active = true")
	Iterable<Product> findByProductTypeIdAndActive(@Param("id") UUID productTypeId);

	@Query("SELECT p FROM Product p WHERE p.brand.id = (:id) and p.active = true")
	Iterable<Product> findByBrandIdAndActive(@Param("id") UUID brandId);

	@Query("SELECT p FROM Product p WHERE p.subgroup.id = (:id) and p.active = true")
	Iterable<Product> findBySubGroupIdAndActive(@Param("id") UUID brandId);

}

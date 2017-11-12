package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="ProductBySubsidiairyRepository")
public interface ProductBySubsidiairyRepository extends CrudRepository<ProductBySubsidiary, UUID>{
	
	@Query("SELECT p FROM ProductBySubsidiary p WHERE p.userClient.id = (:id)")
	Iterable<ProductBySubsidiary> findByUserClientId(@Param("id") UUID userClientId);
	
	@Query("SELECT p FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:id)")
	Iterable<ProductBySubsidiary> findBySubsidiaryId(@Param("id") UUID subsidiaryId);

	@Query("SELECT p FROM ProductBySubsidiary p WHERE p.product.id = (:id)")
	Iterable<ProductBySubsidiary> findByProductId(@Param("id") UUID productId);
}

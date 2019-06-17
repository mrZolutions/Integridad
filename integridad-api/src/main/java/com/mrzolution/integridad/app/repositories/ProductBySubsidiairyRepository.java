package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="ProductBySubsidiairyRepository")
public interface ProductBySubsidiairyRepository extends CrudRepository<ProductBySubsidiary, UUID>{
	
	@Query("SELECT p FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:id)")
	Iterable<ProductBySubsidiary> findBySubsidiaryId(@Param("id") UUID subsidiaryId);

	@Query("SELECT p FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:idS) and p.product.id = (:idP) and p.active = true")
	ProductBySubsidiary findBySubsidiaryIdAndProductId(@Param("idS") UUID subsidiaryId, @Param("idP") UUID ProductId);
        
	@Query("SELECT distinct p.product.id FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:id) and p.product.active = true")
	Page<UUID> findBySubsidiaryIdAndProductActive(@Param("id") UUID subsidiaryId, Pageable pageable);
        
	@Query("SELECT distinct p.product.id FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:id) and p.product.active = true and (p.product.codeIntegridad like (%:variable%) or p.product.name like (%:variable%))")
	Page<UUID> findBySubsidiaryIAndVariabledAndProductActive(@Param("id") UUID subsidiaryId, @Param("variable") String variable, Pageable pageable);

	//594f9d5a-3236-4702-86e1-6f0d34f0bd14 75591174-db48-4313-a6a0-1f2581ff9ed3 b7583d7b-39e5-4ba1-aa88-5fa5eeee2024
	@Query("SELECT distinct p.product.id FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:id) and p.product.active = true and (p.product.productType.id not in ('594f9d5a-3236-4702-86e1-6f0d34f0bd14', '75591174-db48-4313-a6a0-1f2581ff9ed3', 'b7583d7b-39e5-4ba1-aa88-5fa5eeee2024'))")
	Page<UUID> findBySubsidiaryIdAndProductActiveForBill(@Param("id") UUID subsidiaryId, Pageable pageable);

	@Query("SELECT distinct p.product.id FROM ProductBySubsidiary p WHERE p.subsidiary.id = (:id) and p.product.active = true and (p.product.codeIntegridad like (%:variable%) or p.product.name like (%:variable%)) and (p.product.productType.id not in ('594f9d5a-3236-4702-86e1-6f0d34f0bd14', '75591174-db48-4313-a6a0-1f2581ff9ed3', 'b7583d7b-39e5-4ba1-aa88-5fa5eeee2024'))")
	Page<UUID> findBySubsidiaryIAndVariabledAndProductActiveForBill(@Param("id") UUID subsidiaryId, @Param("variable") String variable, Pageable pageable);

	@Query("SELECT p FROM ProductBySubsidiary p WHERE p.product.id = (:id)")
	Iterable<ProductBySubsidiary> findByProductId(@Param("id") UUID productId);
        
}

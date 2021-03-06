package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.ProductType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="BrandRepository")
public interface BrandRepository extends CrudRepository<Brand, UUID>{

	ProductType findByCode(String code);


	Iterable<Brand> findByActive(boolean active);

	@Query("SELECT p FROM Brand p WHERE p.userClient.id = (:id) AND p.active = true")
	Iterable<Brand> findByUserClienteIdAndActive(@Param("id") UUID userClientId);

}

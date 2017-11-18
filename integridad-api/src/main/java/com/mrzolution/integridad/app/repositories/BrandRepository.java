package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Brand;
import com.mrzolution.integridad.app.domain.ProductType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="BrandRepository")
public interface BrandRepository extends CrudRepository<Brand, UUID>{

	ProductType findByCode(String code);


	Iterable<Brand> findByActive(boolean active);

}

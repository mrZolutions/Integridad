package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ProductType;
import com.mrzolution.integridad.app.domain.UserType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="ProductTypeRepository")
public interface ProductTypeRepository extends CrudRepository<ProductType, UUID>{

	UserType findByCode(String code);

}

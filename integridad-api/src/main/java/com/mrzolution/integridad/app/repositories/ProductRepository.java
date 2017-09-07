package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Product;

@Repository
@Qualifier(value="ProductRepository")
public interface ProductRepository extends CrudRepository<Product, UUID>{
	
	Iterable<Product> findByActive(boolean active);
	
}

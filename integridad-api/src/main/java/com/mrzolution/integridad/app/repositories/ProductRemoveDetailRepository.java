package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ProductRemoveDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="ProductRemoveDetailRepository")
public interface ProductRemoveDetailRepository extends CrudRepository<ProductRemoveDetail, UUID>{}

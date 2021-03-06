package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CuentaContableByProduct;
import com.mrzolution.integridad.app.domain.Product;
import com.mrzolution.integridad.app.domain.ProductBySubsidiary;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CuentaContableByProductChildRepository")
public interface CuentaContableByProductChildRepository extends ChildRepository<Product>, JpaRepository<CuentaContableByProduct, UUID>{

    @Query("SELECT d.id FROM CuentaContableByProduct d WHERE d.product = (:id)")
    Iterable<UUID> findByFather(@Param("id") Product product);

}

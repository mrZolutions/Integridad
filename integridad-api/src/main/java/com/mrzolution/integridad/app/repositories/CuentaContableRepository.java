package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CuentaContable;

import com.mrzolution.integridad.app.domain.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CuentaContableRepository")
public interface CuentaContableRepository extends CrudRepository<CuentaContable, UUID>{

    Iterable<CuentaContable> findByActive(boolean active);

    @Query("SELECT p FROM CuentaContable p WHERE p.userClient.id = (:id) AND p.active = true")
    Iterable<CuentaContable> findByUserClientId(@Param("id") UUID id);
        
    @Query("SELECT c FROM CuentaContable c WHERE c.type = (:typ) AND c.active = true")
    Iterable<CuentaContable> findByType(@Param("typ") String typ);
    
    @Query("SELECT c FROM CuentaContable c WHERE c.type = (:typ) AND c.accountType = (:atyp) AND c.active = true")
    Iterable<CuentaContable> findByTypeAndAccountType(@Param("typ") String typ, @Param("atyp") String atyp);
}

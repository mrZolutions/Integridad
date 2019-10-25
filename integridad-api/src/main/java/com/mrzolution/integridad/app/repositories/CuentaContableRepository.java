package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CuentaContable;

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

    @Query("SELECT cc FROM CuentaContable cc WHERE cc.userClient.id = (:id) AND cc.active = true ORDER BY cc.code")
    Iterable<CuentaContable> findByUserClientId(@Param("id") UUID id);
    
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.userClient.id = (:id) AND cc.type != 'BANC' AND cc.active = true")
    Iterable<CuentaContable> findByUserClientIdNoBank(@Param("id") UUID id);
    
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.userClient.id = (:id) AND cc.type = 'BANC' AND cc.active = true")
    Iterable<CuentaContable> findByUserClientIdAndBank(@Param("id") UUID id);
        
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.userClient.id = (:id) AND cc.type = (:typ) AND cc.active = true")
    Iterable<CuentaContable> findByType(@Param("id") UUID id, @Param("typ") String typ);
    
    @Query("SELECT cc FROM CuentaContable cc WHERE cc.type = (:typ) AND cc.accountType = (:atyp) AND cc.active = true")
    Iterable<CuentaContable> findByTypeAndAccountType(@Param("typ") String typ, @Param("atyp") String atyp);
}

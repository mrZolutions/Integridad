package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ConfigCuentas;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 *
 * @author daniel
 */

@Repository
@Qualifier(value="ConfigCuentasRepository")
public interface ConfigCuentasRepository extends CrudRepository<ConfigCuentas, UUID> {

    @Query("SELECT c from ConfigCuentas c where c.userClient.id = (:id) and c.active = true")
    Iterable<ConfigCuentas> findByUserClientId(@Param("id") UUID id);

    @Query("SELECT c from ConfigCuentas c where c.userClient.id = (:id) and c.option.code = (:code) and c.active = true")
    ConfigCuentas findByUserClientIdAndOptionCode(@Param("id") UUID id, @Param("code") String code);
}

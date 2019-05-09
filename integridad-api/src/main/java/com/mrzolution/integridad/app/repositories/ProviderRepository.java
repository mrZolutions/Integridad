package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="ProviderRepository")
public interface ProviderRepository extends CrudRepository<Provider, UUID> {
    Iterable<Provider> findByActive(boolean active);
    
    Iterable<Provider> findByProviderType(String type);

    @Query("SELECT p FROM Provider p WHERE p.userClient.id = (:id) AND p.active = true")
    Iterable<Provider> findProviderByUserClientId(@Param("id") UUID id);
       
    @Query("SELECT p FROM Provider p WHERE p.userClient.id = (:id) AND p.ruc = (:rucp) AND p.active = true")
    Iterable<Provider> findProviderByUserClientIdAndRuc(@Param("id") UUID id, @Param("rucp") String rucp);

}

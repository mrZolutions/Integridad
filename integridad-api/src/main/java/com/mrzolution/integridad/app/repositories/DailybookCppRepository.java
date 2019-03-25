package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCpp;
import com.mrzolution.integridad.app.domain.Provider;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "DailybookCppRepository")
public interface DailybookCppRepository extends CrudRepository<DailybookCpp, UUID> {
    Iterable<DailybookCpp> findByProvider(Provider provider);
    
    @Query("SELECT d FROM DailybookCpp d WHERE d.provider.id = (:id) AND d.active = true ORDER BY d.dailyCppSeq")
    Iterable<DailybookCpp> findDailybookCppByProviderId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookCpp d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true")
    Iterable<DailybookCpp>findDailybookCppByUserClientId(@Param("userClientId") UUID id);
}

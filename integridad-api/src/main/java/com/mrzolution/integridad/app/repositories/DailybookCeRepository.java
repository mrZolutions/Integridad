package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCe;
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
@Qualifier(value = "DailybookCeRepository")
public interface DailybookCeRepository extends CrudRepository<DailybookCe, UUID> {
    Iterable<DailybookCe> findByProvider(Provider provider);
    
    @Query("SELECT d FROM DailybookCe d WHERE d.provider.id = (:id) AND d.active = true ORDER BY d.dailyCeSeq")
    Iterable<DailybookCe> findDailybookCppByProviderId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookCe d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true")
    Iterable<DailybookCe> findDailybookCppByUserClientId(@Param("userClientId") UUID id);
}

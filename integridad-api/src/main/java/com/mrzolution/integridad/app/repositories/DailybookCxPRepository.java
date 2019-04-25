package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCxP;
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
public interface DailybookCxPRepository extends CrudRepository<DailybookCxP, UUID> {
    Iterable<DailybookCxP> findByProvider(Provider provider);
    
    @Query("SELECT d FROM DailybookCxP d WHERE d.provider.id = (:id) AND d.active = true ORDER BY d.dailycxpSeq")
    Iterable<DailybookCxP> findDailybookCxPByProviderId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookCxP d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true")
    Iterable<DailybookCxP> findDailybookCxPByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCxP d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.provider.id = (:providerId) AND d.billNumber = (:bill) AND d.active = true")
    Iterable<DailybookCxP> findDailybookCxPByUserClientIdAndProvIdAndBillNumber(@Param("userClientId") UUID id, @Param("providerId") UUID provid, @Param("bill") String billNumber);
}

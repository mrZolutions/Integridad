package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCi;
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
@Qualifier(value = "DailybookCiRepository")
public interface DailybookCiRepository extends CrudRepository<DailybookCi, UUID> {
    Iterable<DailybookCi> findByProvider(Provider provider);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.provider.id = (:id) AND d.active = true ORDER BY d.dailyCiSeq")
    Iterable<DailybookCi> findDailybookCiByProviderId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true AND d.provider.id = null")
    Iterable<DailybookCi> findDailybookCiByUserClientIdWithNoProvider(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true")
    Iterable<DailybookCi> findDailybookCiByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.provider.id = (:providerId) AND d.billNumber = (:bill) AND d.active = true")
    Iterable<DailybookCi> findDailybookCiByUserClientIdAndProvIdAndBillNumber(@Param("userClientId") UUID id, @Param("providerId") UUID provid, @Param("bill") String billNumber);
}
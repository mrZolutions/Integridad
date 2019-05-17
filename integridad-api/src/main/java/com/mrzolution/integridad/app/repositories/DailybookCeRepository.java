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
    
    @Query("SELECT d FROM DailybookCe d WHERE d.provider.id = (:id) ORDER BY d.dailyCeSeq")
    Iterable<DailybookCe> findDailybookCeByProviderId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookCe d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.provider.id = null")
    Iterable<DailybookCe> findDailybookCeByUserClientIdWithNoProvider(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCe d WHERE d.subsidiary.userClient.id = (:userClientId)")
    Iterable<DailybookCe> findDailybookCeByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCe d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.provider.id = (:providerId) AND d.billNumber = (:bill)")
    Iterable<DailybookCe> findDailybookCeByUserClientIdAndProvIdAndBillNumber(@Param("userClientId") UUID id, @Param("providerId") UUID provid, @Param("bill") String billNumber);
}
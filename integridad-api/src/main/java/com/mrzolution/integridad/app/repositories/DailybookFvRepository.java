package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookFv;
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
@Qualifier(value = "DailybookFvRepository")
public interface DailybookFvRepository extends CrudRepository<DailybookFv, UUID> {
    @Query("SELECT d FROM DailybookFv d WHERE d.client.id = (:id) ORDER BY d.dailyFvSeq")
    Iterable<DailybookFv> findDailybookFvByClientId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookFv d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.client.id = null")
    Iterable<DailybookFv> findDailybookFvByUserClientIdWithNoClient(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookFv d WHERE d.subsidiary.userClient.id = (:userClientId)")
    Iterable<DailybookFv> findDailybookFvByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookFv d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.client.id = (:clientId) AND d.billNumber = (:bill)")
    Iterable<DailybookFv> findDailybookFvByUserClientIdAndClientIdAndBillNumber(@Param("userClientId") UUID id, @Param("clientId") UUID clientId, @Param("bill") String billNumber);
}
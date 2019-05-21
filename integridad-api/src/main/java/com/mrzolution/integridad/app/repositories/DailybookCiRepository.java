package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCi;
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
    @Query("SELECT d FROM DailybookCi d WHERE d.client.id = (:id) AND d.active = true ORDER BY d.dailyCiSeq")
    Iterable<DailybookCi> findDailybookCiByClientId(@Param("id") UUID id);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true AND d.client.id = null")
    Iterable<DailybookCi> findDailybookCiByUserClientIdWithNoClient(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.active = true")
    Iterable<DailybookCi> findDailybookCiByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT d FROM DailybookCi d WHERE d.subsidiary.userClient.id = (:userClientId) AND d.client.id = (:clientId) AND d.billNumber = (:bill) AND d.active = true")
    Iterable<DailybookCi> findDailybookCiByUserClientIdAndClientIdAndBillNumber(@Param("userClientId") UUID id, @Param("clientId") UUID clientId, @Param("bill") String billNumber);
}
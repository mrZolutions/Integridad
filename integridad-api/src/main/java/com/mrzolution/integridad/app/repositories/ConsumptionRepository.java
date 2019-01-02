package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="ConsumptionRepository")
public interface ConsumptionRepository extends CrudRepository<Consumption, UUID> {
    Iterable<Consumption> findConsumptionByUserIntegridad(UserIntegridad user);
    
    @Query("SELECT c FROM Consumption c WHERE c.subsidiary.id = (:id) AND c.active = 'true' ORDER BY c.csmNumberSeq")
    Iterable<Consumption> findConsumptionBySubsidiaryId(@Param("id") UUID id);
    
    @Query("SELECT c FROM Consumption c WHERE c.csmNumberSeq = (:seq) AND c.subsidiary.id = (:subId) AND c.active = 'true' ORDER BY c.csmNumberSeq")
    Iterable<Consumption> findConsumptionByCsmNumberSeqAndSubsidiaryId(@Param("seq") String csmNumberSeq, @Param("subId") UUID id);
    
    @Query("SELECT c FROM Consumption c WHERE c.csmNumberSeq = (:seq) AND c.subsidiary.userClient.id = (:userClientId) AND c.active = 'true' ORDER BY c.csmNumberSeq")
    Iterable<Consumption> findConsumptionByWhNumberSeqAndUserClientId(@Param("seq") String whNumberSeq, @Param("userClientId") UUID id);
    
    @Query("SELECT c FROM Consumption c WHERE c.subsidiary.userClient.id = (:userClientId) AND c.active = 'true' ORDER BY c.csmNumberSeq")
    Iterable<Consumption> findConsumptionByUserClientId(@Param("userClientId") UUID id);
}

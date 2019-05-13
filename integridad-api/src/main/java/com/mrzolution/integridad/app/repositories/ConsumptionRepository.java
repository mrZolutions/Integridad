package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Client;
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
    
    Iterable<Consumption> findConsumptionByClient(Client client);
    
    @Query("SELECT cm FROM Consumption cm WHERE cm.client.id = (:id) AND cm.active = true ORDER BY cm.csmSeq")
    Iterable<Consumption> findConsumptionByClientId(@Param("id") UUID id);
    
    @Query("SELECT cm FROM Consumption cm WHERE cm.subsidiary.id = (:id) AND cm.active = true ORDER BY cm.csmSeq")
    Iterable<Consumption> findConsumptionBySubsidiaryId(@Param("id") UUID id);
    
    @Query("SELECT cm FROM Consumption cm WHERE cm.csmNumberSeq = (:seq) AND cm.subsidiary.id = (:subId) AND cm.active = true ORDER BY cm.csmSeq")
    Iterable<Consumption> findConsumptionByCsmNumberSeqAndSubsidiaryId(@Param("seq") String csmNumberSeq, @Param("subId") UUID id);
    
    @Query("SELECT cm FROM Consumption cm WHERE cm.csmNumberSeq = (:seq) AND cm.subsidiary.userClient.id = (:userClientId) AND cm.active = true ORDER BY cm.csmSeq")
    Iterable<Consumption> findConsumptionByCsmNumberSeqAndUserClientId(@Param("seq") String csmNumberSeq, @Param("userClientId") UUID id);
    
    @Query("SELECT cm FROM Consumption cm WHERE cm.subsidiary.userClient.id = (:userClientId) AND cm.active = true ORDER BY cm.csmSeq")
    Iterable<Consumption> findConsumptionByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT cm FROM Consumption cm WHERE cm.subsidiary.userClient.id = (:userClientId) AND cm.dateConsumption >= (:dateOne) AND cm.dateConsumption <= (:dateTwo) AND cm.active = true ORDER BY cm.csmSeq")
    Iterable<Consumption> findByUserClientIdAndDatesActives(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}
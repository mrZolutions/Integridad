package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.Provider;
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
@Qualifier(value="CellarRepository")
public interface CellarRepository extends CrudRepository<Cellar, UUID> {
    Iterable<Cellar> findCellarByProvider(Provider provider);
    
    Iterable<Cellar> findCellarByUserIntegridad(UserIntegridad user);
    
    @Query("SELECT c FROM Cellar c WHERE c.provider.id = (:id) AND c.active = true AND c.statusIngreso = 'INGRESADO' ORDER BY c.whNumberSeq")
    Iterable<Cellar> findCellarsByProviderId(@Param("id") UUID id);
    
    @Query("SELECT c FROM Cellar c WHERE c.id = (:id) AND c.active = true")
    Iterable<Cellar> findCellarById(@Param("id") UUID id);
    
    @Query("SELECT c FROM Cellar c WHERE c.whNumberSeq = (:seq) AND c.subsidiary.id = (:subId) AND c.active = true ORDER BY c.whNumberSeq")
    Iterable<Cellar> findCellarByWhNumberSeqAndSubsidiaryId(@Param("seq") String whNumberSeq, @Param("subId") UUID id);
    
    @Query("SELECT c FROM Cellar c WHERE c.whNumberSeq = (:seq) AND c.subsidiary.userClient.id = (:userClientId) AND c.active = true ORDER BY c.whNumberSeq")
    Iterable<Cellar> findCellarByWhNumberSeqAndUserClientId(@Param("seq") String whNumberSeq, @Param("userClientId") UUID id);
    
    @Query("SELECT c FROM Cellar c WHERE c.subsidiary.userClient.id = (:userClientId) AND c.active = true ORDER BY c.whNumberSeq")
    Iterable<Cellar> findCellarByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT c FROM Cellar c WHERE c.warehouse.id = (:id) AND c.statusIngreso = 'PENDIENTE' AND c.active = true ORDER BY c.whNumberSeq")
    Iterable<Cellar> findCellarPendingOfWarehouse(@Param("id") UUID id);
    
    @Query("SELECT c FROM Cellar c WHERE c.subsidiary.userClient.id = (:userClientId) AND c.dateEnterCellar >= (:dateOne) AND c.dateEnterCellar <= (:dateTwo) AND c.active = true ORDER BY c.whNumberSeq")
    Iterable<Cellar> findByUserClientIdAndDatesActives(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.UserIntegridad;
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
@Qualifier(value="BillOfflineRepository")
public interface BillOfflineRepository extends CrudRepository<BillOffline, UUID> {
    Iterable<BillOffline> findBillOfflineByClient(Client client);
	
    Iterable<BillOffline> findBillsOfflineByUserIntegridad(UserIntegridad user);
    
    Iterable<BillOffline> findBillsOfflineByStringSeq(String stringSeq);
    
    @Query("SELECT b FROM BillOffline b WHERE b.typeDocument = (:value) AND b.active = true")
    Iterable<BillOffline> findBillsOfflineByTypeDocument(@Param("value") int value);
    
    @Query("SELECT b FROM BillOffline b WHERE b.client.id = (:id) AND b.typeDocument = (:type) AND b.active = true ORDER BY b.stringSeq")
    Iterable<BillOffline> findBillsOfflineByClientId(@Param("id") UUID id, @Param("type") int type);
    
    @Query("SELECT b FROM BillOffline b WHERE b.subsidiary.id = (:subId) AND b.stringSeq = (:seq) AND b.typeDocument = 1 AND b.active = true")
    Iterable<BillOffline> findBillsOfflineByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);
    
    @Query("SELECT b FROM BillOffline b WHERE b.subsidiary.userClient.id = (:userClientId) AND b.dateCreated >= (:dateOne) AND b.dateCreated <= (:dateTwo) AND b.active = true AND b.typeDocument = 1 ORDER BY b.stringSeq")
    Iterable<BillOffline> findByUserClientIdAndDatesActives(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT b FROM BillOffline b WHERE b.subsidiary.userClient.id = (:userClientId) AND b.dateCreated >= (:dateOne) AND b.dateCreated <= (:dateTwo) AND b.typeDocument = 1 ORDER BY b.stringSeq")
    Iterable<BillOffline> findAllByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

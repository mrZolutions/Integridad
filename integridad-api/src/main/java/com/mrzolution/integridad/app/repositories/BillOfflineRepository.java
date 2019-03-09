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
    Iterable<BillOffline> findByClient(Client client);
	
    Iterable<BillOffline> findByUserIntegridad(UserIntegridad user);
    
    Iterable<BillOffline> findByStringSeq(String stringSeq);
    
    @Query("SELECT b FROM BillOffline b WHERE b.subsidiary.id = (:subId) AND b.stringSeq = (:seq) AND b.typeDocument = 1 AND b.active = true")
    Iterable<BillOffline> findByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="RetentionRepository")
public interface RetentionRepository extends CrudRepository<Retention, UUID> {
    Iterable<Retention> findRetentionByProvider(Provider provider);
	
    Iterable<Retention> findRetentionByUserIntegridad(UserIntegridad user);

    @Query("SELECT r FROM Retention r WHERE r.provider.id = (:id) AND r.active = true ORDER BY r.stringSeq")
    Iterable<Retention> findRetentionByProviderId(@Param("id") UUID id);

    Iterable<Retention> findRetentionByStringSeq(String stringSeq);

    @Query("SELECT r FROM Retention r WHERE r.subsidiary.id = (:subId) AND r.stringSeq = (:seq)")
    Iterable<Retention> findRetentionByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);

    @Query("SELECT r FROM Retention r WHERE r.subsidiary.userClient.id = (:userClientId) AND r.dateCreated >= (:dateOne) AND r.dateCreated <= (:dateTwo)")
    Iterable<Retention> findAllRetentionByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

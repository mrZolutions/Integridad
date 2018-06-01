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
public interface RetentionRepository extends CrudRepository<Retention, UUID>{
	
	Iterable<Retention> findByProvider(Provider provider);
	
	Iterable<Retention> findByUserIntegridad(UserIntegridad user);

	@Query("SELECT p FROM Retention p WHERE p.provider.id = (:id)")
	Iterable<Retention> findByProviderId(@Param("id") UUID id);

	Iterable<Retention> findByStringSeq(String stringSeq);

	@Query("SELECT p FROM Retention p WHERE p.subsidiary.id = (:subId) and p.stringSeq = (:seq)")
	Iterable<Retention> findByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);

	@Query("SELECT p FROM Retention p WHERE p.subsidiary.userClient.id = (:userClientId) and p.dateCreated >= (:dateOne) and p.dateCreated <= (:dateTwo)")
	Iterable<Retention> findAllByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

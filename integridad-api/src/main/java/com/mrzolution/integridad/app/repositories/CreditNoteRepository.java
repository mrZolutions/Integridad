package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.CreditNote;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CreditNoteRepository")
public interface CreditNoteRepository extends CrudRepository<CreditNote, UUID>{
	
	Iterable<Bill> findByClient(Client client);
	
//	Iterable<Bill> findByUserIntegridad(UserIntegridad user);
//
//	@Query("SELECT p FROM Bill p WHERE p.client.id = (:id) and p.active = 'true'")
//	Iterable<Bill> findByClientId(@Param("id") UUID id);
//
//	Iterable<Bill> findByStringSeq(String stringSeq);
//
//	@Query("SELECT p FROM Bill p WHERE p.subsidiary.id = (:subId) and p.stringSeq = (:seq)")
//	Iterable<Bill> findByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);
//
//	@Query("SELECT p FROM Bill p WHERE p.subsidiary.userClient.id = (:userClientId) and p.dateCreated >= (:dateOne)  and p.dateCreated <= (:dateTwo) and p.active = 'true'")
//	Iterable<Bill> findByUserClientIdAndDatesActives(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
//
//	@Query("SELECT p FROM Bill p WHERE p.subsidiary.userClient.id = (:userClientId) and p.dateCreated >= (:dateOne) and p.dateCreated <= (:dateTwo)")
//	Iterable<Bill> findAllByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

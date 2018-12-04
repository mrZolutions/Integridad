package com.mrzolution.integridad.app.repositories;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.UserIntegridad;

@Repository
@Qualifier(value="BillRepository")
public interface BillRepository extends CrudRepository<Bill, UUID> {
	
	Iterable<Bill> findByClient(Client client);
	
	Iterable<Bill> findByUserIntegridad(UserIntegridad user);

	@Query("SELECT p FROM Bill p WHERE p.typeDocument = (:value)")
	Iterable<Bill> findBillsByTypeDocument(@Param("value") int value);

	@Query("SELECT p FROM Bill p WHERE p.client.id = (:id) AND typeDocument = (:type) AND p.active = 'true' ORDER BY p.stringSeq")
	Iterable<Bill> findByClientIdAndType(@Param("id") UUID id, @Param("type") int type);

        @Query("SELECT p FROM Bill p WHERE p.client.id = (:id) AND p.typeDocument = (:type) AND p.active = 'true' AND p.priceType = 'CREDITO' ORDER BY p.stringSeq")
        Iterable<Bill> findAllCreditsByClientIdAndType(@Param("id") UUID id, @Param("type") int type);
                
	Iterable<Bill> findByStringSeq(String stringSeq);

	@Query("SELECT p FROM Bill p WHERE p.subsidiary.id = (:subId) AND p.stringSeq = (:seq) AND typeDocument = 1")
	Iterable<Bill> findByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);

	@Query("SELECT p FROM Bill p WHERE p.subsidiary.userClient.id = (:userClientId) AND p.dateCreated >= (:dateOne)  AND p.dateCreated <= (:dateTwo) AND p.active = 'true' AND typeDocument = 1")
	Iterable<Bill> findByUserClientIdAndDatesActives(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

	@Query("SELECT p FROM Bill p WHERE p.subsidiary.userClient.id = (:userClientId) AND p.dateCreated >= (:dateOne) AND p.dateCreated <= (:dateTwo) AND typeDocument = 1")
	Iterable<Bill> findAllByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

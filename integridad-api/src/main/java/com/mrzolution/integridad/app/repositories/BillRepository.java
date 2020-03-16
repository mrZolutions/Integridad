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

    @Query("SELECT b FROM Bill b WHERE b.typeDocument = (:value) AND b.active = true")
    Iterable<Bill> findBillsByTypeDocument(@Param("value") int value);

    @Query("SELECT b FROM Bill b WHERE b.client.id = (:id) AND b.typeDocument = (:type) AND b.saldo != '0' AND b.saldo != '0.00' AND b.active = true ORDER BY b.stringSeq")
    Iterable<Bill> findBillByClientIdWithSaldo(@Param("id") UUID id, @Param("type") int type);
        
    @Query("SELECT b FROM Bill b WHERE b.client.id = (:id) AND b.typeDocument = (:type) AND b.active = true ORDER BY b.stringSeq")
    Iterable<Bill> findBillByClientId(@Param("id") UUID id, @Param("type") int type);
        
    @Query("SELECT b FROM Bill b WHERE b.client.id = (:id) AND b.typeDocument = (:type) AND b.active = true AND b.creditNoteApplied = false ORDER BY b.stringSeq")
    Iterable<Bill> findBillByClientIdAndNoCN(@Param("id") UUID id, @Param("type") int type);
        
    Iterable<Bill> findByStringSeq(String stringSeq);
        
    @Query("SELECT b FROM Bill b WHERE b.id = (:id) AND b.active = true")
    Iterable<Bill> findBillById(@Param("id") UUID id);

    @Query("SELECT b FROM Bill b WHERE b.subsidiary.id = (:subId) AND b.stringSeq = (:seq) AND b.typeDocument = 1 AND b.active = true")
    Iterable<Bill> findByStringSeqAndSubsidiaryId(@Param("seq") String stringSeq, @Param("subId") UUID id);

    @Query("SELECT b FROM Bill b WHERE b.subsidiary.userClient.id = (:userClientId) AND b.dateCreated >= (:dateOne) AND b.dateCreated <= (:dateTwo) AND b.active = true AND b.typeDocument = 1 ORDER BY b.stringSeq")
    Iterable<Bill> findByUserClientIdAndDatesActives(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT b FROM Bill b WHERE b.subsidiary.userClient.id = (:userClientId) AND b.dateCreated >= (:dateOne) AND b.dateCreated <= (:dateTwo) AND b.typeDocument = 1 ORDER BY b.stringSeq")
    Iterable<Bill> findAllByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}
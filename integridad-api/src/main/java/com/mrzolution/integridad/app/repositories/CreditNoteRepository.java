package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Client;
import com.mrzolution.integridad.app.domain.CreditNote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="CreditNoteRepository")
public interface CreditNoteRepository extends CrudRepository<CreditNote, UUID> {
	
    Iterable<Bill> findByClient(Client client);
    
    @Query("SELECT c FROM CreditNote c WHERE c.documentStringSeq = (:docNum) AND c.billSeq = (:billId) AND c.active = true")
    Iterable<CreditNote> findByDocumentStringSeqAndBillId(@Param("docNum") String docNum, @Param("billId") String billId);
    
    @Query("SELECT c FROM CreditNote c WHERE c.client.id = (:id) AND c.active = true")
    Iterable<CreditNote> findCreditNotesByClientId(@Param("id") UUID id);
}
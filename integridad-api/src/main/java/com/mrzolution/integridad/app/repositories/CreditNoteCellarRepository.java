package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.CreditNoteCellar;
import com.mrzolution.integridad.app.domain.Provider;
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
@Qualifier(value="CreditNoteCellarRepository")
public interface CreditNoteCellarRepository extends CrudRepository<CreditNoteCellar, UUID> {
    
    Iterable<Cellar> findByProvider(Provider provider);
    
    @Query("SELECT c FROM CreditNoteCellar c WHERE c.documentStringSeq = (:docNum) AND c.cellarSeq = (:cellarId) AND c.active = true")
    Iterable<CreditNoteCellar> findByDocumentStringSeqAndCellarId(@Param("docNum") String docNum, @Param("cellarId") String cellarId);
    
    @Query("SELECT c FROM CreditNoteCellar c WHERE c.provider.id = (:id) AND c.active = true")
    Iterable<CreditNoteCellar> findCreditNotesCellarByProviderId(@Param("id") UUID id);
}
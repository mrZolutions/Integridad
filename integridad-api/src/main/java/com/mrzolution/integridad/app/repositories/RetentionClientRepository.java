package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.RetentionClient;
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
@Qualifier(value = "RetentionClientRepository")
public interface RetentionClientRepository extends CrudRepository<RetentionClient, UUID>{
    Iterable<RetentionClient> findByBill(Bill Bill);
    
    @Query("SELECT r FROM RetentionClient r WHERE r.documentNumber = (:docNum) and r.bill.id = (:id)")
    Iterable<RetentionClient> findByDocumentNumberAndBillId(@Param("docNum") String docNum, @Param("id") UUID id);
}

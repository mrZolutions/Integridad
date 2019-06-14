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
    Iterable<RetentionClient> findByBill(Bill bill);
    
    @Query("SELECT r FROM RetentionClient r WHERE r.bill.id = (:id) AND r.active = true")
    Iterable<RetentionClient> findRetentionClientByBillId(@Param("id") UUID id);
    
    @Query("SELECT r FROM RetentionClient r WHERE r.documentNumber = (:docNum) and r.bill.id = (:id) AND r.active = true")
    Iterable<RetentionClient> findRetentionClientByBillIdAndDocumentNumber(@Param("id") UUID id, @Param("docNum") String docNum);
    
    @Query("SELECT r FROM RetentionClient r JOIN r.bill b JOIN b.client cl WHERE cl.userClient.id = (:userClientId) AND r.dateToday >= (:dateOne) AND r.dateToday <= (:dateTwo) ORDER BY cl.name")
    Iterable<RetentionClient> findRetentionClientByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

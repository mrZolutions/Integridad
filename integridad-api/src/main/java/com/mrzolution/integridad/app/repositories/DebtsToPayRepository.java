package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.Provider;
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
@Qualifier(value = "DebtsToPayRepository")
public interface DebtsToPayRepository extends CrudRepository<DebtsToPay, UUID> {
    Iterable<DebtsToPay> findByProvider(Provider provider);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.provider.id = (:id) AND dp.active = true ORDER BY dp.billNumber")
    Iterable<DebtsToPay> findDebtsToPayByProviderId(@Param("id") UUID id);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.provider.id = (:id) AND dp.saldo > 0.0 AND dp.estado = 'PENDIENTE' AND dp.active = true ORDER BY dp.billNumber")
    Iterable<DebtsToPay> findDebtsToPayWithSaldoByProviderId(@Param("id") UUID id);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.subsidiary.id = (:subId) AND dp.debtsSeq = (:seq) AND dp.active = true")
    Iterable<DebtsToPay> findDebtsToPayByDebtsSeqAndSubsidiaryId(@Param("seq") String debtsSeq, @Param("subId") UUID id);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.subsidiary.userClient.id = (:userClientId) AND dp.fecha >= (:dateOne) AND dp.fecha <= (:dateTwo) AND dp.active = true ORDER BY dp.debtsSeq")
    Iterable<DebtsToPay> findDebtsToPayByUserClientIdAndDates(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.subsidiary.userClient.id = (:userClientId) AND dp.active = true ORDER BY dp.debtsSeq")
    Iterable<DebtsToPay> findDebtsToPayByUserClientId(@Param("userClientId") UUID id);
    
    @Query("SELECT dp FROM DebtsToPay dp WHERE dp.subsidiary.userClient.id = (:userClientId) AND dp.billNumber = (:bill) AND dp.authorizationNumber = (:autho) AND dp.active = true")
    Iterable<DebtsToPay> findDebtsToPayByBillNumberAndAuthoNumber(@Param("userClientId") UUID id, @Param("bill") String billNumber, @Param("autho") String authoNumber);
}

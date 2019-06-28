package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PagoDebts;
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
@Qualifier(value = "CreditsDebtsRepository")
public interface CreditsDebtsRepository extends CrudRepository<CreditsDebts, UUID> {
    Iterable<CreditsDebts> findByPagoDebts (PagoDebts pagoDebts);
    
    CreditsDebts findByDebtsToPayId(String debtsToPayId);
    
    @Query("SELECT c FROM CreditsDebts c JOIN c.pagoDebts p JOIN p.debtsToPay d WHERE d.id = (:id) AND d.active = true AND c.estadoCredits = 'PENDIENTE'")
    Iterable<CreditsDebts> findCreditsDebtsByDebtsToPayId(@Param("id") UUID id);
    
    @Query("SELECT c FROM CreditsDebts c WHERE c.id = (:id)")
    Iterable<CreditsDebts> findCreditsDebtsById(@Param("id") UUID id);
    
    @Query("SELECT c FROM CreditsDebts c WHERE c.pagoDebts.id = (:id)")
    Iterable<CreditsDebts> findCreditsDebtsByPagoDebtsId(@Param("id") UUID id);
    
    @Query("SELECT c FROM CreditsDebts c JOIN c.pagoDebts p JOIN p.debtsToPay d JOIN d.provider pr WHERE pr.userClient.id = (:id) AND c.estadoCredits = 'PENDIENTE' AND d.active = true AND d.fecha <= (:dateTwo) ORDER BY pr.name, d.fecha")
    Iterable<CreditsDebts> findCreditsDebtsPendingOfDebtsToPayByUserClientId(@Param("id") UUID id, @Param("dateTwo") long dateTwo);
}
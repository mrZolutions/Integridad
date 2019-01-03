package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PaymentDebts;
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
@Qualifier(value = "PaymentDebtsRepository")
public interface PaymentDebtsRepository extends CrudRepository<PaymentDebts, UUID> {
    Iterable<PaymentDebts> findByCreditsDebts(CreditsDebts creditsDebts);
    
    Iterable<PaymentDebts> findByDocumentNumber(String documentNumber);
    
    @Query("SELECT p FROM PaymentDebts p JOIN p.creditsDebts c JOIN c.pagoDebts pd JOIN pd.debtsToPay d JOIN d.provider pr WHERE pr.userClient.id = :id ORDER BY pr.name, pd.fechaCobro")
    Iterable<PaymentDebts> findPaymentsDebtsByUserClientId(@Param("id") UUID id);
}
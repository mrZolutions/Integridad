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
    
    @Query("SELECT p FROM PaymentDebts p JOIN p.creditsDebts c JOIN c.pagoDebts pd JOIN pd.debtsToPay d JOIN d.provider pr WHERE pr.userClient.id = (:id) ORDER BY pr.name, pd.fechaCobro")
    Iterable<PaymentDebts> findPaymentsDebtsByUserClientId(@Param("id") UUID id);
    
    @Query("SELECT p FROM PaymentDebts p JOIN p.creditsDebts c JOIN c.pagoDebts pd JOIN pd.debtsToPay d JOIN d.provider pr WHERE pr.userClient.id = (:id) AND p.datePayment >= (:dateOne) AND p.datePayment <= (:dateTwo) ORDER BY pr.name, p.datePayment")
    Iterable<PaymentDebts> findPaymentsDebtsByUserClientIdAndDates(@Param("id") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
    
    @Query("SELECT p FROM PaymentDebts p JOIN p.creditsDebts c JOIN c.pagoDebts pd JOIN pd.debtsToPay d JOIN d.provider pr WHERE pr.userClient.id = (:id) AND p.banco = (:banco) AND p.noDocument = (:nrodoc)")
    Iterable<PaymentDebts> findPaymentsDebtsByUserClientIdWithBankAndNroDocument(@Param("id") UUID id, @Param("banco") String banco, @Param("nrodoc") String nrodoc);
}

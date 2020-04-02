package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Qualifier(value = "PaymentRepository")
public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    
    Iterable<Payment> findByCredits(Credits credits);
    
    Iterable<Payment> findByDocumentNumber(String documentNumber);
    
    @Query("SELECT py FROM Payment py WHERE py.credits.id = (:id)")
    Iterable<Payment> findPaymentByCreditsId(@Param("id") UUID id);
    
    @Query("SELECT py FROM Payment py JOIN py.credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = (:id) AND py.datePayment >= (:dateOne) AND py.datePayment <= (:dateTwo) AND py.active = true ORDER BY cl.name, py.datePayment")
    Iterable<Payment> findAllPaymentsByUserClientIdAndDates(@Param("id") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
    
    @Query("SELECT py FROM Payment py JOIN py.credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.id = (:id) AND py.datePayment <= (:dateTwo) AND py.active = true ORDER BY cl.name, py.datePayment")
    Iterable<Payment> findStatementClientReport(@Param("id") UUID id, @Param("dateTwo") long dateTwo);
    
    @Query("SELECT py FROM Payment py JOIN py.credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = (:id) AND py.banco = (:banco) AND py.noDocument = (:nrodoc) AND py.active = true")
    Iterable<Payment> findPaymentsByUserClientIdWithBankAndNroDocument(@Param("id") UUID id, @Param("banco") String banco, @Param("nrodoc") String nrodoc);
    
    @Query("SELECT py FROM Payment py " +
            "WHERE py.active = true AND py.typePayment = 'PAC' AND py.credits.pago.bill.client.id = (:id)")
    Iterable<Payment> findPaymentsByClientIdOnLine(@Param("id") UUID id);

    @Query("SELECT py FROM Payment py " +
            "WHERE (py.active = true AND py.typePayment = 'PAC' AND py.credits.pagoOffline.billOffline.client.id = (:id)) ")
    Iterable<Payment> findPaymentsByClientIdOffline(@Param("id") UUID id);
}
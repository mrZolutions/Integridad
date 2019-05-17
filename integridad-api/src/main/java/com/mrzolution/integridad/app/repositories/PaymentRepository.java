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
    
    @Query("SELECT py FROM Payment py JOIN py.credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = (:id) AND py.datePayment >= (:dateOne) AND py.datePayment <= (:dateTwo) ORDER BY cl.name, py.datePayment")
    Iterable<Payment> findAllPaymentsByUserClientIdAndDates(@Param("id") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}

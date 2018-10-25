package com.mrzolution.integridad.app.repositories;

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
    Iterable<Payment> findByDocumentNumber(String documentNumber);
    
    @Query("SELECT py FROM Payment py JOIN py.credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = :id ORDER BY cl.name")
    Iterable<Payment> findAllPaymentsByUserClientId(@Param("id") UUID id);
}

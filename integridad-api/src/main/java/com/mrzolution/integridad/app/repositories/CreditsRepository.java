package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Pago;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Qualifier(value="CreditsRepository")
public interface CreditsRepository extends CrudRepository<Credits, UUID> {

    Iterable<Credits> findByPago(Pago pago);
    
    Credits findByBillId(String billId);

    @Query("SELECT c FROM Credits c JOIN c.pago p JOIN p.bill b WHERE b.id = (:id) AND b.active = true AND c.statusCredits = 'PENDIENTE'")
    Iterable<Credits> findCreditsByBillId(@Param("id") UUID id);
    
    @Query("SELECT c FROM Credits c WHERE c.id = (:id)")
    Iterable<Credits> findCreditsById(@Param("id") UUID id);
    
    @Query("SELECT c FROM Credits c WHERE c.pago.id = (:id)")
    Iterable<Credits> findCreditsByPagoId(@Param("id") UUID id);
    
    @Query("SELECT c FROM Credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = (:id) AND c.statusCredits = 'PENDIENTE' AND b.dateCreated <= (:dateTwo) AND b.active = true ORDER BY cl.name, b.dateCreated")
    Iterable<Credits> findCreditsPendingOfBillByUserClientId(@Param("id") UUID id, @Param("dateTwo") long dateTwo);

    @Query("SELECT c FROM Credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE " +
            "b.saldo != '0' AND b.saldo != '0.00' AND b.saldo != '0.0' "+
            "AND cl.userClient.id = (:id) AND b.dateCreated <= (:dateTwo) AND b.active = true ORDER BY cl.name, b.dateCreated")
    Iterable<Credits> findCreditsOfBillByUserClientId(@Param("id") UUID id, @Param("dateTwo") long dateTwo);
    
    @Query("SELECT c FROM Credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = (:id) AND c.statusCredits = 'PAGADO' AND b.active = true ORDER BY cl.name")
    Iterable<Credits> findCreditsPayedOfBillByUserClientId(@Param("id") UUID id);
}
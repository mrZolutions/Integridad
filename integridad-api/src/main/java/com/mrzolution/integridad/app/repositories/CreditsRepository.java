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
public interface CreditsRepository extends CrudRepository<Credits, UUID>{

    Iterable<Credits> findByPago(Pago pago);
    
    Credits findByBillId(String billId);

    @Query("SELECT c FROM Credits c JOIN c.pago p JOIN p.bill b WHERE b.id = :id AND c.statusCredits = 'PENDIENTE'")
    Iterable<Credits> findCreditsOfBillByBillId(@Param("id") UUID id);
    
    @Query("SELECT c FROM Credits c WHERE c.id = :id")
    Iterable<Credits> findCreditsById(@Param("id") UUID id);
    
    @Query("SELECT c FROM Credits c WHERE c.pago.id = :id")
    Iterable<Credits> findCreditsByPagoId(@Param("id") UUID id);
    
    @Query("SELECT c FROM Credits c JOIN c.pago p JOIN p.bill b JOIN b.client cl WHERE cl.userClient.id = :id AND c.statusCredits = 'PENDIENTE' AND b.active = 'true'")
    Iterable<Credits> findCreditsOfBillByUserClientId(@Param("id") UUID id);
}

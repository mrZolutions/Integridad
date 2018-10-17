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

    @Query("SELECT c FROM Credits c JOIN c.pago p WHERE p.bill.id = :id AND c.statusCredits = 'PENDIENTE'")
    Iterable<Credits> findCreditsOfBillByBillId(@Param("id") UUID id);
}

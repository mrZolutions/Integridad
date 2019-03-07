package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Pago;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Qualifier(value="PagoRepository")
public interface PagoRepository extends CrudRepository<Pago, UUID> {
    
    Iterable<Pago> findByBill(Bill bill);
    
    @Query("SELECT p FROM Pago p JOIN p.bill b WHERE b.subsidiary.userClient.id = (:userClientId) AND b.dateCreated >= (:dateOne) AND b.dateCreated <= (:dateTwo) AND b.active = true AND b.typeDocument = 1 ORDER BY p.medio")
    Iterable<Pago> findForCashClosureReport(@Param("userClientId") UUID id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

}

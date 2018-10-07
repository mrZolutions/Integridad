package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.domain.Pago;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Qualifier(value="PagoChildRepository")
public interface PagoChildRepository extends ChildRepository<Bill>, JpaRepository<Pago, UUID>{

    @Query("SELECT d.id FROM Pago d WHERE d.bill = (:id)")
    Iterable<UUID> findByFather(@Param("id") Bill bill);

}

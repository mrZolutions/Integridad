package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.PagoDebts;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="PagoDebtsChildRepository")
public interface PagoDebtsChildRepository extends ChildRepository<DebtsToPay>, JpaRepository<PagoDebts, UUID> {
    @Query("SELECT d.id FROM PagoDebts d WHERE d.debtsToPay = (:id)")
    Iterable<UUID> findByFather(@Param("id") DebtsToPay debtsToPay);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DebtsToPay;
import com.mrzolution.integridad.app.domain.DetailDebtsToPay;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DetailDebtsToPayChildRepository")
public interface DetailDebtsToPayChildRepository extends ChildRepository<DebtsToPay>, JpaRepository<DetailDebtsToPay, UUID>{
    @Query("SELECT d.id FROM DetailDebtsToPay d WHERE d.debtsToPay = (:id)")
    Iterable<UUID> findByFather(@Param("id") DebtsToPay debtsToPay);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PaymentDebts;
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
@Qualifier(value = "PaymentDebtsChildRepository")
public interface PaymentDebtsChildRepository extends ChildRepository<CreditsDebts>, JpaRepository<PaymentDebts, UUID> {
    @Query("SELECT p.id FROM PaymentDebts p WHERE p.creditsDebts = (:id)")
    Iterable<UUID> findByFather(@Param("id") CreditsDebts credits);
}

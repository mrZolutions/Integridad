package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Credits;
import com.mrzolution.integridad.app.domain.Payment;
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
@Qualifier(value="PaymentChildRepository")
public interface PaymentChildRepository extends ChildRepository<Credits>, JpaRepository<Payment, UUID>{
    @Query("SELECT p.id FROM Payment p WHERE p.credits = (:id)")
    Iterable<UUID> findByFather(@Param("id") Credits credits);
}

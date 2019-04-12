package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.CreditsDebts;
import com.mrzolution.integridad.app.domain.PagoDebts;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value="CreditsDebtsChildRepository")
public interface CreditsDebtsChildRepository extends ChildRepository<PagoDebts>, JpaRepository<CreditsDebts, UUID> {
    @Query("SELECT c.id FROM CreditsDebts c WHERE c.pagoDebts = (:id)")
    Iterable<UUID> findByFather(@Param("id") PagoDebts pagoDebts);
}

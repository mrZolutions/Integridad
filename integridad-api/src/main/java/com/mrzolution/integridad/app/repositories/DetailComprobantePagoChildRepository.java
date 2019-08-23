package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ComprobantePago;
import com.mrzolution.integridad.app.domain.DetailComprobantePago;
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
@Qualifier(value="DetailComprobantePagoChildRepository")
public interface DetailComprobantePagoChildRepository extends ChildRepository<ComprobantePago>, JpaRepository<DetailComprobantePago, UUID> {
    @Query("SELECT d.id FROM DetailComprobantePago d WHERE d.comprobantePago = (:id)")
    Iterable<UUID> findByFather(@Param("id") ComprobantePago comprobantePago);
}
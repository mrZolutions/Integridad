package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ComprobanteCobro;
import com.mrzolution.integridad.app.domain.DetailComprobanteCobro;
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
@Qualifier(value="DetailComprobanteCobroChildRepository")
public interface DetailComprobanteCobroChildRepository extends ChildRepository<ComprobanteCobro>, JpaRepository<DetailComprobanteCobro, UUID> {
    @Query("SELECT d.id FROM DetailComprobanteCobro d WHERE d.comprobanteCobro = (:id)")
    Iterable<UUID> findByFather(@Param("id") ComprobanteCobro comprobanteCobro);
}
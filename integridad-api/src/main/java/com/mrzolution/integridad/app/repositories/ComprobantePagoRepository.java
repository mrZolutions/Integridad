package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ComprobantePago;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "ComprobantePagoRepository")
public interface ComprobantePagoRepository extends CrudRepository<ComprobantePago, UUID> {
    @Query("SELECT c FROM ComprobantePago c WHERE c.provider.id = (:id) ORDER BY c.comprobanteSeq")
    Iterable<ComprobantePago> findComprobantePagoByProviderId(@Param("id") UUID id);
    
    @Query("SELECT c FROM ComprobantePago c WHERE c.subsidiary.userClient.id = (:userClientId)")
    Iterable<ComprobantePago> findComprobantePagoByUserClientId(@Param("userClientId") UUID id);
}
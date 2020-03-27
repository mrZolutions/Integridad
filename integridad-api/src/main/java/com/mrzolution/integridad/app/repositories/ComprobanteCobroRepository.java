package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ComprobanteCobro;
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
@Qualifier(value = "ComprobanteCobroRepository")
public interface ComprobanteCobroRepository extends CrudRepository<ComprobanteCobro, UUID> {
    @Query("SELECT c FROM ComprobanteCobro c WHERE c.client.id = (:id) ORDER BY c.comprobanteSeq")
    Iterable<ComprobanteCobro> findComprobanteCobroByClientId(@Param("id") UUID id);
    
    @Query("SELECT c FROM ComprobanteCobro c WHERE c.subsidiary.userClient.id = (:userClientId)")
    Iterable<ComprobanteCobro> findComprobanteCobroByUserClientId(@Param("userClientId") UUID id);

    @Query("SELECT c FROM ComprobanteCobro c WHERE c.subsidiary.userClient.id = (:userClientId) and dateComprobanteCreated = :dateCreated and c.billNumber like %:billNumber%")
    ComprobanteCobro findComprobanteCobroByBillNumberAndUserClientAndDateCreated(@Param("billNumber") String billNumber, @Param("userClientId") UUID userClientId, @Param("dateCreated") String dateCreated);
}
package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ComprobanteCobro;
import com.mrzolution.integridad.app.domain.DetailComprobanteCobro;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value="DetailComprobanteCobroRepository")
public interface DetailComprobanteCobroRepository extends CrudRepository<DetailComprobanteCobro, UUID> {
    Iterable<DetailComprobanteCobro> findByComprobanteCobro(ComprobanteCobro comprobanteCobro);
}
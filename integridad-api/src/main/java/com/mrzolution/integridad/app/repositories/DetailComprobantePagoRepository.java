package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.ComprobantePago;
import com.mrzolution.integridad.app.domain.DetailComprobantePago;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value="DetailComprobantePagoRepository")
public interface DetailComprobantePagoRepository extends CrudRepository<DetailComprobantePago, UUID> {
    Iterable<DetailComprobantePago> findByComprobantePago(ComprobantePago comprobantePago);
}
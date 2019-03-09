package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.PagoOffline;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value="PagoOfflineRepository")
public interface PagoOfflineRepository extends CrudRepository<PagoOffline, UUID> {
    Iterable<PagoOffline> findByBillOffline(BillOffline billOffline);
}

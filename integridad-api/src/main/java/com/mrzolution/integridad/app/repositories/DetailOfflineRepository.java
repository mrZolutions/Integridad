package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.DetailOffline;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value="DetailOfflineRepository")
public interface DetailOfflineRepository extends CrudRepository<DetailOffline, UUID> {
    Iterable<DetailOffline> findByBillOffline(BillOffline billOffline);
}

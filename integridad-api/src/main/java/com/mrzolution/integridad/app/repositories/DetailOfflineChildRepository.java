package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.DetailOffline;
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
@Qualifier(value="DetailOfflineChildRepository")
public interface DetailOfflineChildRepository extends ChildRepository<BillOffline>, JpaRepository<DetailOffline, UUID> {
    @Query("SELECT d.id FROM DetailOffline d WHERE d.billOffline = (:id)")
    Iterable<UUID> findByFather(@Param("id") BillOffline billOffline);
}

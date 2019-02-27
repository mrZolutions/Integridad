package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.domain.DetailDailybookCg;
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
@Qualifier(value = "DetailDailybookCgChildRepository")
public interface DetailDailybookCgChildRepository extends ChildRepository<DailybookCg>, JpaRepository<DetailDailybookCg, UUID> {
    @Query("SELECT b.id FROM DetailDailybookCg b WHERE b.dailybookCg = (:id)")
    Iterable<UUID> findByFather(@Param("id") DailybookCg dailybookCg);
}

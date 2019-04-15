package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.domain.DetailDailybookCe;
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
@Qualifier(value = "DetailDailybookCeChildRepository")
public interface DetailDailybookCeChildRepository extends ChildRepository<DailybookCe>, JpaRepository<DetailDailybookCe, UUID> {
    @Query("SELECT d.id FROM DetailDailybookCe d WHERE d.dailybookCe = (:id)")
    Iterable<UUID> findByFather(@Param("id") DailybookCe dailybookCe);
}

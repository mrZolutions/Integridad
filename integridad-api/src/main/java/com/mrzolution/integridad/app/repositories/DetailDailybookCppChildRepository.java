package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCpp;
import com.mrzolution.integridad.app.domain.DetailDailybookCpp;
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
@Qualifier(value = "DetailDailybookCppChildRepository")
public interface DetailDailybookCppChildRepository extends ChildRepository<DailybookCpp>, JpaRepository<DetailDailybookCpp, UUID> {
    @Query("SELECT b.id FROM DetailDailybookCpp b WHERE b.dailybookCpp = (:id)")
    Iterable<UUID> findByFather(@Param("id") DailybookCpp dailybookCpp);
}

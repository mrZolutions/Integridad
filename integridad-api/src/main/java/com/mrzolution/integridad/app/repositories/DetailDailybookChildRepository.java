package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Dailybook;
import com.mrzolution.integridad.app.domain.DetailDailybook;
import com.mrzolution.integridad.app.interfaces.ChildRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DetailDailybookChildRepository")
public interface DetailDailybookChildRepository extends ChildRepository<Dailybook>, JpaRepository<DetailDailybook, UUID> {
    @Query("SELECT dd.id FROM DetailDailybook dd WHERE dd.dailybook = (:id)")
    Iterable<UUID> findByFather(@Param("id") Dailybook dailybook);
}

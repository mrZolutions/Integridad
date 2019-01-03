package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.DetailConsumption;
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
@Qualifier(value="DetailConsumptionChildRepository")
public interface DetailConsumptionChildRepository extends ChildRepository<Consumption>, JpaRepository<DetailConsumption, UUID> {
    @Query("SELECT d.id FROM DetailConsumption d WHERE d.consumption = (:id)")
    Iterable<UUID> findByFather(@Param("id") Consumption consumption);
}

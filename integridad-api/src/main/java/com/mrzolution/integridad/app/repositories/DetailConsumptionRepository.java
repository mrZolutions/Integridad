package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Consumption;
import com.mrzolution.integridad.app.domain.DetailConsumption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="DetailConsumptionRepository")
public interface DetailConsumptionRepository extends CrudRepository<DetailConsumption, UUID> {
    Iterable<DetailConsumption> findByConsumption(Consumption consumption);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Dailybook;
import com.mrzolution.integridad.app.domain.DetailDailybook;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value = "DetailDailybookRepository")
public interface DetailDailybookRepository extends CrudRepository<DetailDailybook, UUID> {
    Iterable<DetailDailybook> findByDailybook(Dailybook dailybook);
}

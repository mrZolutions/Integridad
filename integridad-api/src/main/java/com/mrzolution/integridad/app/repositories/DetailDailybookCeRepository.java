package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.domain.DetailDailybookCe;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "DetailDailybookCeRepository")
public interface DetailDailybookCeRepository extends CrudRepository<DetailDailybookCe, UUID> {
    Iterable<DetailDailybookCe> findByDailybookCe(DailybookCe dailybookCe);
}

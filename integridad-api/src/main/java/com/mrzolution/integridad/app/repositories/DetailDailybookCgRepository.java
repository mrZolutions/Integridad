package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.domain.DetailDailybookCg;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "DetailDailybookCgRepository")
public interface DetailDailybookCgRepository extends CrudRepository<DetailDailybookCg, UUID> {
    Iterable<DetailDailybookCg> findByDailybookCg(DailybookCg dailybookCg);
}

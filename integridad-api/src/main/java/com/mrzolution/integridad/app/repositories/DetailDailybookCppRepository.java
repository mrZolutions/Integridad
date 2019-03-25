package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCpp;
import com.mrzolution.integridad.app.domain.DetailDailybookCpp;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "DetailDailybookCppRepository")
public interface DetailDailybookCppRepository extends CrudRepository<DetailDailybookCpp, UUID> {
    Iterable<DetailDailybookCpp> findByDailybookCpp(DailybookCpp dailybookCpp);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.domain.DailybookCxP;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "DetailDailybookContabRepository")
public interface DetailDailybookContabRepository extends CrudRepository<DetailDailybookContab, UUID> {
    Iterable<DetailDailybookContab> findByDailybookCg(DailybookCg dailybookCg);
    
    Iterable<DetailDailybookContab> findByDailybookCe(DailybookCe dailybookCe);
    
    Iterable<DetailDailybookContab> findByDailybookCxP(DailybookCxP dailybookCxP);
}

package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCg;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author daniel-one
 */

@Repository
@Qualifier(value = "DailybookCgRepository")
public interface DailybookCgRepository extends CrudRepository<DailybookCg, UUID> {
    @Query("SELECT db FROM DailybookCg db WHERE db.subsidiary.userClient.id = (:userClientId)")
    Iterable<DailybookCg>findDailybookCgByUserClientId(@Param("userClientId") UUID id);
}
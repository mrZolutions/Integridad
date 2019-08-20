package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.DailybookCe;
import com.mrzolution.integridad.app.domain.DailybookCg;
import com.mrzolution.integridad.app.domain.DailybookCi;
import com.mrzolution.integridad.app.domain.DailybookCxP;
import com.mrzolution.integridad.app.domain.DailybookFv;
import com.mrzolution.integridad.app.domain.DetailDailybookContab;
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
@Qualifier(value = "DetailDailybookContabRepository")
public interface DetailDailybookContabRepository extends CrudRepository<DetailDailybookContab, UUID> {
    Iterable<DetailDailybookContab> findByDailybookCg(DailybookCg dailybookCg);
    
    Iterable<DetailDailybookContab> findByDailybookCe(DailybookCe dailybookCe);
    
    Iterable<DetailDailybookContab> findByDailybookCi(DailybookCi dailybookCi);
    
    Iterable<DetailDailybookContab> findByDailybookCxP(DailybookCxP dailybookCxP);
    
    Iterable<DetailDailybookContab> findByDailybookFv(DailybookFv dailybookFv);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND d.codeConta = (:code) AND d.dateDetailDailybook <= (:dateOne) ORDER BY d.dateDetailDailybook")
    Iterable<DetailDailybookContab> findPreviousEspecificMajorByUserClientIdAndDate(@Param("id") String id, @Param("code") String code, @Param("dateOne") long dateOne);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND d.codeConta = (:code) AND d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo) ORDER BY d.dateDetailDailybook")
    Iterable<DetailDailybookContab> findEspecificMajorByUserClientIdAndDates(@Param("id") String id, @Param("code") String code, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}
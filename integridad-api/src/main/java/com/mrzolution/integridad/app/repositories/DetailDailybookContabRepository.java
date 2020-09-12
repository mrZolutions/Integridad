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
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.dailybookCg.id = (:id)")
    Iterable<DetailDailybookContab> findByDailybookCgId(@Param("id") UUID id);
    
    Iterable<DetailDailybookContab> findByDailybookCe(DailybookCe dailybookCe);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.dailybookCe.id = (:id)")
    Iterable<DetailDailybookContab> findByDailybookCeId(@Param("id") UUID id);
    
    Iterable<DetailDailybookContab> findByDailybookCi(DailybookCi dailybookCi);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.dailybookCi.id = (:id)")
    Iterable<DetailDailybookContab> findByDailybookCiId(@Param("id") UUID id);
    
    Iterable<DetailDailybookContab> findByDailybookCxP(DailybookCxP dailybookCxP);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.dailybookCxP.id = (:id)")
    Iterable<DetailDailybookContab> findByDailybookCxPId(@Param("id") UUID id);
    
    Iterable<DetailDailybookContab> findByDailybookFv(DailybookFv dailybookFv);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND d.codeConta = (:code) AND d.dateDetailDailybook <= (:dateOne) ORDER BY d.dateDetailDailybook")
    Iterable<DetailDailybookContab> findPreviousEspecificMajorByUsrClntIdAndDate(@Param("id") String id, @Param("code") String code, @Param("dateOne") long dateOne);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND d.codeConta = (:code) AND d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo) ORDER BY d.dateDetailDailybook")
    Iterable<DetailDailybookContab> findEspecificMajorByUsrClntIdAndDates(@Param("id") String id, @Param("code") String code, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
    
    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND (d.codeConta >= (:codeOne) AND d.codeConta <= (:codeTwo)) AND (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) ORDER BY d.codeConta, d.dateDetailDailybook")
    Iterable<DetailDailybookContab> findGeneralMajorByUsrClntIdAndCodeContaAndDate(@Param("id") String id, @Param("codeOne") String codeOne, @Param("codeTwo") String codeTwo, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND  (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) AND d.active = true GROUP BY d.dailybookNumber, d.id ORDER BY d.dateDetailDailybook DESC")
    Iterable<DetailDailybookContab> findAllByUsrClntIdAndAndDate(@Param("id") String id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND  (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) AND d.active = true " +
            "AND d.dailybookCe is not NULL GROUP BY d.dailybookCe, d.id ORDER BY d.dateDetailDailybook DESC")
    Iterable<DetailDailybookContab> findDailyCeByUsrClntIdAndAndDate(@Param("id") String id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND  (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) AND d.active = true " +
            "AND d.dailybookCg is not NULL GROUP BY d.dailybookCg, d.id ORDER BY d.dateDetailDailybook DESC")
    Iterable<DetailDailybookContab> findDailyCgByUsrClntIdAndAndDate(@Param("id") String id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND  (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) AND d.active = true " +
            "AND d.dailybookCi is not NULL GROUP BY d.dailybookCi, d.id ORDER BY d.dateDetailDailybook DESC")
    Iterable<DetailDailybookContab> findDailyCiByUsrClntIdAndAndDate(@Param("id") String id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND  (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) AND d.active = true " +
            "AND d.dailybookCxP is not NULL GROUP BY d.dailybookCxP, d.id ORDER BY d.dateDetailDailybook DESC")
    Iterable<DetailDailybookContab> findDailyCxpByUsrClntIdAndAndDate(@Param("id") String id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);

    @Query("SELECT d FROM DetailDailybookContab d WHERE d.userClientId = (:id) AND  (d.dateDetailDailybook >= (:dateOne) AND d.dateDetailDailybook <= (:dateTwo)) AND d.active = true " +
            "AND d.dailybookFv is not NULL GROUP BY d.dailybookFv, d.id ORDER BY d.dateDetailDailybook DESC")
    Iterable<DetailDailybookContab> findDailyFvByUsrClntIdAndAndDate(@Param("id") String id, @Param("dateOne") long dateOne, @Param("dateTwo") long dateTwo);
}
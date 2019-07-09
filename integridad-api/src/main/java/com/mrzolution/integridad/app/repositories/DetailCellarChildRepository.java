package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.DetailCellar;
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
@Qualifier(value="DetailCellarChildRepository")
public interface DetailCellarChildRepository extends ChildRepository<Cellar>, JpaRepository<DetailCellar, UUID> {
    @Query("SELECT d.id FROM DetailCellar d WHERE d.cellar = (:id)")
    Iterable<UUID> findByFather(@Param("id") Cellar cellar);
}
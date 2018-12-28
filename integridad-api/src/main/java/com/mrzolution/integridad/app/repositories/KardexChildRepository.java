package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.Kardex;
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
@Qualifier(value="KardexChildRepository")
public interface KardexChildRepository extends ChildRepository<Cellar>, JpaRepository<Kardex, UUID> {
    @Query("SELECT k.id FROM Kardex k WHERE k.cellar = (:id)")
    Iterable<UUID> findByFather(@Param("id") Cellar cellar);
}

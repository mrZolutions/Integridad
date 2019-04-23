package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.DetailCellar;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="DetailCellarRepository")
public interface DetailCellarRepository extends CrudRepository<DetailCellar, UUID> {
    Iterable<DetailCellar> findByCellar(Cellar cellar);
    
    @Query("SELECT d FROM DetailCellar d JOIN d.cellar c JOIN c.provider p WHERE p.userClient.id = (:userClientId) AND (d.adicional != null OR d.adicional != '') AND c.active = true ORDER BY c.billNumber")
    Iterable<DetailCellar> findDetailsOfCellarsByUserClientId(@Param("userClientId") UUID id);
}
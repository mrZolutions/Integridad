package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Cellar;
import com.mrzolution.integridad.app.domain.DetailCellar;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mrzolutions-daniel
 */

@Repository
@Qualifier(value="DetailCellarRepository")
public interface DetailCellarRepository extends CrudRepository<DetailCellar, UUID> {
    Iterable<DetailCellar> findByCellar(Cellar cellar);
}

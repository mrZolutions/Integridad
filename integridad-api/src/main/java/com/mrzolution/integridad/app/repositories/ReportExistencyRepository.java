/**
 *
 * @author daniel-one
 */
package com.mrzolution.integridad.app.repositories;

import com.mrzolution.integridad.app.domain.Existency;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
@Qualifier(value = "ReportExistencyRepository")
public interface ReportExistencyRepository extends CrudRepository<Existency, UUID> {
    @Query("SELECT p FROM Existency p WHERE p.userClient.id = (:userClientId)")
    Iterable<Existency>findProductsByUserClientId(@Param("userClientId") UUID id);
}
